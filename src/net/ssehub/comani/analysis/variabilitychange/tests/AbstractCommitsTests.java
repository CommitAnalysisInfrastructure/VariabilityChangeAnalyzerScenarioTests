/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.ssehub.comani.analysis.variabilitychange.tests;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Properties;

import net.ssehub.comani.analysis.AnalysisSetupException;
import net.ssehub.comani.analysis.variabilitychange.core.VariabilityChangeAnalyzer;
import net.ssehub.comani.data.CommitQueue;
import net.ssehub.comani.data.CommitQueue.QueueState;
import net.ssehub.comani.extraction.AbstractCommitExtractor;
import net.ssehub.comani.extraction.ExtractionSetupException;
import net.ssehub.comani.extraction.git.GitCommitExtractor;
import net.ssehub.comani.extraction.svn.SvnCommitExtractor;
import net.ssehub.comani.utility.FileUtilities;

/**
 * This abstract class provides common attributes and methods used by the specific test classes, which perform tests
 * with commit files.
 * 
 * @author Christian Kroeher
 *
 */
public class AbstractCommitsTests {
    
    /**
     * The return value of the {@link VariabilityChangeAnalyzer#analyze()} method indicating whether the extraction
     * process terminated successfully (<code>true</code>) or not (<code>false</code>).
     * 
     * @see #setUp(File, String[])
     */
    protected static boolean commitAnalysisSuccessful;
    
    /**
     * The parsed commit analysis results. This two-dimensional array contains the following information as created
     * during {@link #setUp()}.
     * <ul>
     * <li>First index: the (number of) test commit files</li>
     * <li>Second index: the ten attributes in the following order:</li>
     *   <ul>
     *   <li>Test commit file name</li>
     *   <li>CCF = Changed Code Files</li>
     *   <li>CCLAI = Changed Code Lines containing Artifact-specific Information</li>
     *   <li>CCLVI = Changed Code Lines containing Variability Information</li>
     *   <li>CBF = Changed Build Files</li>
     *   <li>CBLAI = Changed Build Lines containing Artifact-specific Information</li>
     *   <li>CBLVI = Changed Build Lines containing Variability Information</li>
     *   <li>CMF = Changed variability Model Files</li>
     *   <li>CMLAI = Changed variability Model Lines containing Artifact-specific Information</li>
     *   <li>CMLVI = Changed variability Model Lines containing Variability Information</li>
     *   </ul>
     * </ul>
     * 
     * @see #parseAnalysisResults(String[])
     */
    protected static Object[][] parsedAnalysisResults;

    /**
     * Performs the commit extraction and analysis for each commit represented by an individual file in the
     * given test commits directory and sets the values for {@link #commitAnalysisSuccessful} as well as
     * {@link #parsedAnalysisResults}, which serve as input for the respective tests below.
     * 
     * @param testCommitsDirectory the {@link File} denoting the directory in which the test commit files are located
     * @param testCommitFileNames the names of the test commit files located in the given directory
     * @param vcs the name of the version control system from which the test commits stem from; either "git" or "svn"
     * @param vmFilesPattern the regular expression for identifying variability model files
     * @param codeFilesPattern the regular expression for identifying code files
     * @param buildFilesPattern the regular expression for identifying build files
     * @throws ExtractionSetupException if instantiating the respective commit extractor fails
     * @throws AnalysisSetupException if instantiating the {@link VariabilityChangeAnalyzer} fails
     */
    //checkstyle: stop parameter number check
    protected static void setUp(File testCommitsDirectory, String[] testCommitFileNames, String vcs, 
            String vmFilesPattern, String codeFilesPattern, String buildFilesPattern) 
            throws ExtractionSetupException, AnalysisSetupException {
        System.out.println("## Setting up tests based on commits located at \"" 
            + testCommitsDirectory.getPath() + "\" ##");
        // Define the required properties for the commit extractor and analyzer
        Properties pluginProperties = new Properties();
        pluginProperties.setProperty("core.version_control_system", vcs);
        pluginProperties.setProperty("analysis.output", AllTests.TESTOUTPUT_DIRECTORY.getAbsolutePath());
        pluginProperties.setProperty("analysis.variability_change_analyzer.vm_files_regex", vmFilesPattern);
        pluginProperties.setProperty("analysis.variability_change_analyzer.code_files_regex", codeFilesPattern);
        pluginProperties.setProperty("analysis.variability_change_analyzer.build_files_regex", buildFilesPattern);
        // Instantiate the common commit queue for the commit extractor and analyzer
        CommitQueue commitQueue = new CommitQueue(testCommitFileNames.length);
        // Instantiate the commit extractor and analyzer
//        GitCommitExtractor commitExtractor = new GitCommitExtractor(pluginProperties, commitQueue);
        AbstractCommitExtractor commitExtractor;
        if (vcs.equals("git")) {
            commitExtractor = new GitCommitExtractor(pluginProperties, commitQueue);
        } else {
            commitExtractor = new SvnCommitExtractor(pluginProperties, commitQueue);
        }
        VariabilityChangeAnalyzer commitAnalyzer = new VariabilityChangeAnalyzer(pluginProperties, commitQueue);
        // Extract the commits based on the commit files in the test commits directory
        commitQueue.setState(QueueState.OPEN);
        extractCommits(testCommitsDirectory, testCommitFileNames, commitExtractor);
        commitQueue.setState(QueueState.CLOSED); // Actual closing after all commits are analyzed
        // Analyze the extracted commits
        commitAnalysisSuccessful = commitAnalyzer.analyze();
        // Parse the result file containing the classification of changed lines per commit
        parseAnalysisResults(testCommitFileNames);
    }
    //checkstyle: resume parameter number check
    
    /**
     * Deletes the result files created by the commit analyzer during {@link #setUp()} from the
     * {@link #TEST_OUTPUT_DIRECTORY}.
     * 
     * @param testClassName the name of the test class calling this method
     */
    protected static void tearDown(String testClassName) {
        System.out.println("## Tearing down \"" + testClassName + "\" ##");
        File[] commitAnalyzerResultFiles = AllTests.TESTOUTPUT_DIRECTORY.listFiles();
        for (int i = 0; i < commitAnalyzerResultFiles.length; i++) {
            if (!commitAnalyzerResultFiles[i].delete()) {
                System.err.println(testClassName + ": Deleting commit analyzer result file \"" 
                        + commitAnalyzerResultFiles[i].getAbsolutePath() + "\" failed");
            }
        }
    }
    
    /**
     * Performs the commit extraction by calling the commit extractor for (a subset of) the files in the given test
     * commits directory. However, only the content of those files, where the file name matches on of the given test
     * commit file names, is passed to the extractor.
     * 
     * @param testCommitsDirectory the {@link File} denoting the directory in which the test commit files are located
     * @param testCommitFileNames the names of the test commit files located in the given directory
     * @param commitExtractor the {@link AbstractCommitExtractor}, which shall be used to extract the commits
     * 
     */
    // TODO Update Javadoc from GitCommitExtractor to simply commit extractor
    private static void extractCommits(File testCommitsDirectory, String[] testCommitFileNames,
            AbstractCommitExtractor commitExtractor) {
        File[] testCommitFiles = testCommitsDirectory.listFiles(new FilenameFilter() {
            // Extract only those commits where the corresponding file name matches one of the given file names
            @Override
            public boolean accept(File dir, String name) {
                boolean accept = false;
                int testCommitFileNamesCounter = 0;
                while (!accept && testCommitFileNamesCounter < testCommitFileNames.length) {
                    if (testCommitFileNames[testCommitFileNamesCounter].equals(name)) {
                        accept = true;
                    }
                    testCommitFileNamesCounter++;
                }
                return accept;
            }
        });
        for (int i = 0; i < testCommitFiles.length; i++) {
            List<String> commitFileLines = FileUtilities.getInstance().readFile(testCommitFiles[i]);
            StringBuilder commitBuilder = new StringBuilder();
            commitBuilder.append(commitFileLines.get(0));
            for (int j = 1; j < commitFileLines.size(); j++) {
                commitBuilder.append("\n");
                commitBuilder.append(commitFileLines.get(j));
            }
            commitExtractor.extract(commitBuilder.toString());
        }
    }
    
    /**
     * Parses the result file containing the classification of changed files and lines per commit created during
     * {@link #setUp(File, String[])} and saves these results in the {@link #parsedAnalysisResults} for later use by the
     * respective test methods.
     * 
     * @param testCommitFileNames the names of the test commit files located in the test commits directory
     */
    private static void parseAnalysisResults(String[] testCommitFileNames) {
        parsedAnalysisResults = new Object[testCommitFileNames.length][10];
        File commitAnalyzerResultFile = new File(AllTests.TESTOUTPUT_DIRECTORY, AllTests.ANALYZER_RESULTS_FILE);
        List<String> commitAnalyzerResultFileLines = FileUtilities.getInstance().readFile(commitAnalyzerResultFile);
        /*
         * Line index 0 = Column headers, like "Commit Date", "Commit ID", "CCF", "CCLAI", etc.
         * Line index 1 = first extracted commit results
         * Line index 2 = second extracted commit results
         * Line index 3 = ...
         */
        String testCommitFileNameWithoutPostfix;
        String[] commitAnalysisResults;
        int commitAnalyzerResultFileLinesCounter;
        boolean resultsFound;
        for (int i = 0; i < testCommitFileNames.length; i++) {
            testCommitFileNameWithoutPostfix = testCommitFileNames[i].substring(0, testCommitFileNames[i].indexOf('.'));
            commitAnalyzerResultFileLinesCounter = 1;
            resultsFound = false;
            while (!resultsFound && commitAnalyzerResultFileLinesCounter < commitAnalyzerResultFileLines.size()) {
                commitAnalysisResults = 
                        commitAnalyzerResultFileLines.get(commitAnalyzerResultFileLinesCounter).split("\t");
                /*
                 * commitAnalysisResults: [<DATE>, <ID>, 0, 0, 0, 1, 6, 6, 0, 0, 0]
                 * Index after splitting:    0       1   2  3  4  5  6  7  8  9  10
                 */
                if (commitAnalysisResults[1].startsWith(testCommitFileNameWithoutPostfix)) {
                    // Analysis results for current test commit file found
                    resultsFound = true;
                    parsedAnalysisResults[i][0] = commitAnalysisResults[1];
                    for (int k = 2; k < commitAnalysisResults.length; k++) {
                        parsedAnalysisResults[i][k - 1] = commitAnalysisResults[k];
                    }
                }
                commitAnalyzerResultFileLinesCounter++;
            }
        }
    }
    
    /**
     * The core elements of the variability change analysis results. These are the number of:
     * <ul>
     * <li>CCF = Changed Code Files</li>
     * <li>CCLAI = Changed Code Lines containing Artifact-specific Information</li>
     * <li>CCLVI = Changed Code Lines containing Variability Information</li>
     * <li>CBF = Changed Build Files</li>
     * <li>CBLAI = Changed Build Lines containing Artifact-specific Information</li>
     * <li>CBLVI = Changed Build Lines containing Variability Information</li>
     * <li>CMF = Changed variability Model Files</li>
     * <li>CMLAI = Changed variability Model Lines containing Artifact-specific Information</li>
     * <li>CMLVI = Changed variability Model Lines containing Variability Information</li>
     * </ul>
     * Further, each element provides its index in the {@link #parsedAnalysisResults}.
     * 
     * @author Christian Kroeher
     *
     */
    protected enum AnalysisResultElement {
        
        CCF(1), CCLAI(2), CCLVI(3), CBF(4), CBLAI(5), CBLVI(6), CMF(7), CMLAI(8), CMLVI(9);
        
        /**
         * The index of this element in the {@link #parsedAnalysisResults}.
         */
        private final int index;
        
        /**
         * Constructs a new {@link AnalysisResultElement} with the given index.
         * 
         * @param index the index of this element in the {@link #parsedAnalysisResults}
         */
        private AnalysisResultElement(int index) {
            this.index = index;
        }
        
        /**
         * Returns the number for this {@link AnalysisResultElement} defining its index in the
         * {@link #parsedAnalysisResults}.
         *  
         * @return the index of this element in the {@link #parsedAnalysisResults}
         */
        public int getIndex() {
            return index;
        }
    };
    
    /**
     * Returns the number of the given {@link AnalysisResultElement} for the given test commit file (name) as provided
     * by the {@link #parsedAnalysisResults}.
     *  
     * @param testCommitFileName the name of the test commit file for which the number of the given
     *        {@link AnalysisResultElement} shall be returned
     * @param element the {@link AnalysisResultElement} for which the number provided by the analysis results for the
     *        given commit shall be returned
     * @return the number greater than or equal to zero representing the number of counted changed elements or
     *         <tt>-1</tt> if the given commit file name or {@link AnalysisResultElement} is not part of the 
     *         {@link #parsedAnalysisResults} 
     */
    protected int getAnalysisResultValue(String testCommitFileName, AnalysisResultElement element) {
        int analysisResultValue = -1;
        int availableTestCommitFileCounter = 0;
        Object[] commitAnalysisResult;
        String commitAnalysisCommitId;
        while (analysisResultValue < 0 && availableTestCommitFileCounter < parsedAnalysisResults.length) {
            commitAnalysisResult = parsedAnalysisResults[availableTestCommitFileCounter];
            commitAnalysisCommitId = String.valueOf(commitAnalysisResult[0]);
            if (commitAnalysisCommitId.startsWith(testCommitFileName)) {
                analysisResultValue = Integer.valueOf(
                        String.valueOf(commitAnalysisResult[element.getIndex()]));
                
            }
            availableTestCommitFileCounter++;
        }
        return analysisResultValue;
    }
}
