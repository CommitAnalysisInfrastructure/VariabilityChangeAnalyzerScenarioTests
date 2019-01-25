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
package net.ssehub.comani.analysis.variabilitychange.tests.comani;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.ssehub.comani.analysis.AnalysisSetupException;
import net.ssehub.comani.analysis.variabilitychange.tests.AbstractCommitsTests;
import net.ssehub.comani.analysis.variabilitychange.tests.AllTests;
import net.ssehub.comani.extraction.ExtractionSetupException;
import net.ssehub.comani.utility.FileUtilities;

/**
 * This class provides some {@link net.ssehub.comani.analysis.variabilitychange.core.VariabilityChangeAnalyzer} tests
 * based on merge commits from the Linux kernel.<br>
 * <br>
 * These tests are similar to those defined in 
 * {@link net.ssehub.comani.analysis.variabilitychange.tests.coman.LinuxMergeCommitsTests}, but use commits, which are
 * extracted by executing the updated git commands of the {@link net.ssehub.comani.extraction.git.GitCommitExtractor}.
 * While these commits are the same as in the other tests, here, we ensure that the updated git commands in combination
 * with the "old" analyzer still provide the same results as before.<br>
 * <br>
 * Hence, the only differences to the 
 * {@link net.ssehub.comani.analysis.variabilitychange.tests.coman.LinuxMergeCommitsTests} are the different 
 * {@link #TEST_COMMITS_DIRECTORY} and the way the commits located in this directory were extracted.
 * 
 * @author Christian Kröher
 *
 */
public class LinuxMergeCommitsTests extends AbstractCommitsTests {
    
    /**
     * The directory in which the test commit files are located. Each file contains the information of a particular
     * commit from the Linux kernel repository.
     */
    private static final File TEST_COMMITS_DIRECTORY = new File(AllTests.TESTDATA_DIRECTORY, "comani/linux");
    
    /**
     * The names of the test commit files located in the {@link #TEST_COMMITS_DIRECTORY}. Each file contains the
     * information about a particular merge commit.
     */
    private static final String[] TEST_COMMIT_FILE_NAMES = {"4294616.txt", "79c7c7a.txt", "efde611.txt"};
    
    /**
     * The result file containing the list of commits, which were not analyzed as the do not provide detailed change
     * information. Each line of that file contains exactly one id of such a commit, like merge commits.
     */
    private static final File UNANALYZED_COMMITS_SUMMARY_FILE = 
            new File(AllTests.TESTOUTPUT_DIRECTORY, AllTests.ANALYZER_UNANALYZED_COMMITS_SUMMARY_FILE);
    
    /**
     * Calls the {@link #setUp(File, String[])} of the parent class with {@link #TEST_COMMITS_DIRECTORY} and
     * {@link #TEST_COMMIT_FILE_NAMES}.
     * 
     * @throws ExtractionSetupException if instantiating the {@link net.ssehub.comani.extraction.git.GitCommitExtractor}
     *         during {@link #setUp(File, String[])} fails
     * @throws AnalysisSetupException if instantiating the 
     *         {@link net.ssehub.comani.analysis.variabilitychange.core.VariabilityChangeAnalyzer} during
     *         {@link #setUp(File, String[])} fails
     */
    @BeforeClass
    public static void setUp() throws ExtractionSetupException, AnalysisSetupException {
        setUp(TEST_COMMITS_DIRECTORY, TEST_COMMIT_FILE_NAMES);
    }
    
    /**
     * Calls the {@link #tearDown(String)} of the parent class with the name of this class.
     */
    @AfterClass
    public static void tearDown() {
        tearDown("LinuxMergeCommitsTests");
    }

    /**
     * Tests the successful termination of the commit analysis process.
     */
    @Test
    public void testCommitAnalysisSuccessful() {
        assertTrue("The commit analysis process should terminate successfully", commitAnalysisSuccessful);
    }
    
    /**
     * Tests the availability of the {@link #UNANALYZED_COMMITS_SUMMARY_FILE}.
     */
    @Test
    public void testUnanalyzedFileAvailable() {
        assertTrue("The file summarizing all unanalyzed commits should be available",
                UNANALYZED_COMMITS_SUMMARY_FILE.exists());
    }
    
    /**
     * Tests whether the analysis result file does not contain any entries describing changes of commits.
     */
    @Test
    public void testResultFileEmpty() {
        File commitAnalyzerResultFile = new File(AllTests.TESTOUTPUT_DIRECTORY, AllTests.ANALYZER_RESULTS_FILE);
        List<String> commitAnalyzerResultFileLines = FileUtilities.getInstance().readFile(commitAnalyzerResultFile);
        assertEquals("The analysis result file should be empty", 0, commitAnalyzerResultFileLines.size());
    }
    
    /**
     * Tests whether the commits identified by the elements of the {@link #TEST_COMMIT_FILE_NAMES} are part of the
     * {@link #UNANALYZED_COMMITS_SUMMARY_FILE}.
     */
    @Test
    public void testCommitsUnanalyzed() {
        List<String> commitAnalyzerUnanalyzedFileLines = 
                FileUtilities.getInstance().readFile(UNANALYZED_COMMITS_SUMMARY_FILE);
        String testCommitFileNameWithoutPostfix;
        for (int i = 0; i < TEST_COMMIT_FILE_NAMES.length; i++) {
            testCommitFileNameWithoutPostfix = 
                    TEST_COMMIT_FILE_NAMES[i].substring(0, TEST_COMMIT_FILE_NAMES[i].indexOf('.'));
            assertTrue("Commit \"" + testCommitFileNameWithoutPostfix 
                    + "\" is not listed in the unanalyzed commits file",
                    contains(commitAnalyzerUnanalyzedFileLines, testCommitFileNameWithoutPostfix));
        }
    }
    
    /**
     * Checks whether the given list contains an element, which starts with the given prefix. If multiple elements start
     * with the given prefix, the first element found in the list is considered the desired one.
     * 
     * @param list the list of strings in which the element with the given prefix should be found
     * @param prefix the prefix with which an element in the given list should start
     * @return <code>true</code> if an element in the list starts with the given prefix; <code>false</code> otherwise
     */
    private boolean contains(List<String> list, String prefix) {
        boolean elementFound = false;
        int indexCounter = 0;
        while (!elementFound && indexCounter < list.size()) {
            elementFound = list.get(indexCounter).startsWith(prefix);
            indexCounter++;
        }
        return elementFound;
    }
    
}
