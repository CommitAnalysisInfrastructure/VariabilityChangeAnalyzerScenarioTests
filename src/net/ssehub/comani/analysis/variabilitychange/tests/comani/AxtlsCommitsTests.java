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
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.ssehub.comani.analysis.AnalysisSetupException;
import net.ssehub.comani.analysis.variabilitychange.tests.AbstractCommitsTests;
import net.ssehub.comani.analysis.variabilitychange.tests.AllTests;
import net.ssehub.comani.extraction.ExtractionSetupException;
import net.ssehub.comani.utility.FileUtilities;

/**
 * This class provides some {@link net.ssehub.comani.analysis.variabilitychange.core.VariabilityChangeAnalyzer} tests
 * based on commits from the axTLS embedded SSL project.<br>
 * 
 * @author Christian Kroeher
 *
 */
@RunWith(Parameterized.class)
public class AxtlsCommitsTests extends AbstractCommitsTests {
    
    /**
     * The directory in which the test commit files are located. Each file contains the information of a particular
     * commit from the axTLS repository.
     */
    private static final File TEST_COMMITS_DIRECTORY = new File(AllTests.TESTDATA_DIRECTORY, "comani/axtls");
    
    /**
     * The names of the test commit files located in the {@link #TEST_COMMITS_DIRECTORY}.
     */
    private static final String[] TEST_COMMIT_FILE_NAMES = {"r79.txt", "r88.txt", "r91.txt", "r98.txt", "r100.txt",
        "r101.txt", "r122.txt", "r130.txt", "r153.txt", "r169.txt", "r176.txt", "r198.txt", "r201.txt", "r213.txt",
        "r217.txt", "r218.txt", "r226.txt", "r234.txt", "r275.txt", "r276.txt"};
    
    /**
     * The regular expression for identifying variability model files in the axTLS project.
     */
    private static final String VM_FILES_REGEX = ".*/Config\\.in((\\.|\\-|\\_|\\+|\\~).*)?";
    
    /**
     * The regular expression for identifying code files in the axTLS project.
     */
    private static final String CODE_FILES_REGEX = ".*/.*\\.[hcS]((\\.|\\-|\\_|\\+|\\~).*)?";
    
    /**
     * The regular expression for identifying build files in the axTLS project.
     */
    private static final String BUILD_FILES_REGEX = ".*/(Makefile|Kbuild)((\\.|\\-|\\_|\\+|\\~).*)?";
    // Possible addition: |(.*/.*\\.(mak|make)))
    //                    | makefile.*
    //                    | in general all GNU default makefile names
    
    /**
     * The name of the test commit file currently under test.
     */
    private String testCommitFileName;
    
    /**
     * The expected number of code files changed by the current test commit (file).
     * 
     * @see #testCommitFileName
     */
    private int expectedChangedCodeFiles;
    
    /**
     * The expected number of code lines containing artifact-specific information changed by the current test commit
     * (file).
     * 
     * @see #testCommitFileName
     */
    private int expectedChangedCodeLinesArtifactSpecific;
    
    /**
     * The expected number of code lines containing variability information changed by the current test commit
     * (file).
     * 
     * @see #testCommitFileName
     */
    private int expectedChangedCodeLinesVariability;
    
    /**
     * The expected number of build files changed by the current test commit (file).
     * 
     * @see #testCommitFileName
     */
    private int expectedChangedBuildFiles;
    
    /**
     * The expected number of build lines containing artifact-specific information changed by the current test commit
     * (file).
     * 
     * @see #testCommitFileName
     */
    private int expectedChangedBuildLinesArtifactSpecific;
    
    /**
     * The expected number of build lines containing variability information changed by the current test commit
     * (file).
     * 
     * @see #testCommitFileName
     */
    private int expectedChangedBuildLinesVariability;
    
    /**
     * The expected number of variability model files changed by the current test commit (file).
     * 
     * @see #testCommitFileName
     */
    private int expectedChangedModelFiles;
    
    /**
     * The expected number of variability model lines containing artifact-specific information changed by the current
     * test commit (file).
     * 
     * @see #testCommitFileName
     */
    private int expectedChangedModelLinesArtifactSpecific;
    
    /**
     * The expected number of variability model lines containing variability information changed by the current test
     * commit (file).
     * 
     * @see #testCommitFileName
     */
    private int expectedChangedModelLinesVariability;
    
    /**
     * Constructs a new {@link AxtlsCommitsTests} object for injecting test parameters.
     * 
     * @param testcommitFileName the name of the test commit file; one of {@link #TEST_COMMIT_FILE_NAMES}
     * @param expectedChangedCodeFiles the expected number of changed code files
     * @param expectedChangedCodeLinesArtifactSpecific the expected number of changed code lines containing
     *        artifact-specific information
     * @param expectedChangedCodeLinesVariability the expected number of changed code lines containing variability
     *        information
     * @param expectedChangedBuildFiles the expected number of changed build files
     * @param expectedChangedBuildLinesArtifactSpecific the expected number of changed build lines containing
     *        artifact-specific information
     * @param expectedChangedBuildLinesVariability the expected number of changed build lines containing variability
     *        information
     * @param expectedChangedModelFiles the expected number of changed variability model files
     * @param expectedChangedModelLinesArtifactSpecific the expected number of changed variability model lines
     *        containing artifact-specific information
     * @param expectedChangedModelLinesVariability the expected number of changed variability model lines containing
     *        variability information
     */
    //checkstyle: stop parameter number check
    public AxtlsCommitsTests(String testcommitFileName, int expectedChangedCodeFiles,
            int expectedChangedCodeLinesArtifactSpecific, int expectedChangedCodeLinesVariability, 
            int expectedChangedBuildFiles, int expectedChangedBuildLinesArtifactSpecific, 
            int expectedChangedBuildLinesVariability, int expectedChangedModelFiles,
            int expectedChangedModelLinesArtifactSpecific, int expectedChangedModelLinesVariability) {
        this.testCommitFileName = testcommitFileName;
        this.expectedChangedCodeFiles = expectedChangedCodeFiles;
        this.expectedChangedCodeLinesArtifactSpecific = expectedChangedCodeLinesArtifactSpecific;
        this.expectedChangedCodeLinesVariability = expectedChangedCodeLinesVariability;
        this.expectedChangedBuildFiles = expectedChangedBuildFiles;
        this.expectedChangedBuildLinesArtifactSpecific = expectedChangedBuildLinesArtifactSpecific;
        this.expectedChangedBuildLinesVariability = expectedChangedBuildLinesVariability;
        this.expectedChangedModelFiles = expectedChangedModelFiles;
        this.expectedChangedModelLinesArtifactSpecific = expectedChangedModelLinesArtifactSpecific;
        this.expectedChangedModelLinesVariability = expectedChangedModelLinesVariability;
    }
    //checkstyle: resume parameter number check
    
    /**
     * Returns the expected values for each test commit file as a list of object arrays. Each element of that list
     * contains the expected data of a test commit (file), which in turn contains the test commit
     * file name (first element) and the expected numbers of changed files and lines (subsequent elements) as follows:
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
     * 
     * @return the list of object arrays representing the test commit files and the expected analysis results
     */
    @Parameters
    public static List<Object[]> getTestData() {
        Object[][] expectedResults = new Object[TEST_COMMIT_FILE_NAMES.length][10];
        File expectedResultsFile = new File(TEST_COMMITS_DIRECTORY, "axtls_ExpectedValues.txt");
        List<String> expectedResultsFileLines = FileUtilities.getInstance().readFile(expectedResultsFile);
        /*
         * Line index 0 = Column headers, like "Commit", "Changed Model Files", "Changed Source Lines", etc.
         * Line index 1 = first extracted commit expected results
         * Line index 2 = second extracted commit expected results
         * Line index 3 = ...
         */
        String testCommitFileNameWithoutPostfix;
        int expectedResultsFileLinesCounter;
        boolean resultsFound;
        for (int i = 0; i < TEST_COMMIT_FILE_NAMES.length; i++) {
            testCommitFileNameWithoutPostfix = 
                    TEST_COMMIT_FILE_NAMES[i].substring(0, TEST_COMMIT_FILE_NAMES[i].indexOf('.'));
            expectedResultsFileLinesCounter = 1;
            resultsFound = false;
            while (!resultsFound && expectedResultsFileLinesCounter < expectedResultsFileLines.size()) {
                String[] expectedResultsFileLineElements = 
                        expectedResultsFileLines.get(expectedResultsFileLinesCounter).split("\t");
                if (expectedResultsFileLineElements[0].equals(testCommitFileNameWithoutPostfix)) {
                    resultsFound = true;
                    expectedResults[i][0] = expectedResultsFileLineElements[0]; // Commit id
                    expectedResults[i][1] = Integer.valueOf(expectedResultsFileLineElements[2]); // CCF
                    expectedResults[i][2] = Integer.valueOf(expectedResultsFileLineElements[6]); // CCLAI
                    expectedResults[i][3] = Integer.valueOf(expectedResultsFileLineElements[7]); // CCLVI
                    expectedResults[i][4] = Integer.valueOf(expectedResultsFileLineElements[3]); // CBF
                    expectedResults[i][5] = Integer.valueOf(expectedResultsFileLineElements[8]); // CBLAI
                    expectedResults[i][6] = Integer.valueOf(expectedResultsFileLineElements[9]); // CBLVI
                    expectedResults[i][7] = Integer.valueOf(expectedResultsFileLineElements[1]); // CMF
                    expectedResults[i][8] = Integer.valueOf(expectedResultsFileLineElements[4]); // CMLAI
                    expectedResults[i][9] = Integer.valueOf(expectedResultsFileLineElements[5]); // CMLVI
                }
                expectedResultsFileLinesCounter++;
            }
        }
        return Arrays.asList(expectedResults);
    }
    
    /**
     * Calls the {@link #setUp(File, String[])} of the parent class with {@link #TEST_COMMITS_DIRECTORY}, 
     * {@link #TEST_COMMIT_FILE_NAMES}, {@link #VM_FILES_REGEX}, {@link #CODE_FILES_REGEX}, and 
     * {@link #BUILD_FILES_REGEX} as well as "svn".
     * 
     * @throws ExtractionSetupException if instantiating the {@link net.ssehub.comani.extraction.svn.SvnCommitExtractor}
     *         during {@link #setUp(File, String[])} fails
     * @throws AnalysisSetupException if instantiating the 
     *         {@link net.ssehub.comani.analysis.variabilitychange.core.VariabilityChangeAnalyzer} during
     *         {@link #setUp(File, String[])} fails
     */
    @BeforeClass
    public static void setUp() throws ExtractionSetupException, AnalysisSetupException {
        setUp(TEST_COMMITS_DIRECTORY, TEST_COMMIT_FILE_NAMES, "svn", VM_FILES_REGEX, CODE_FILES_REGEX,
                BUILD_FILES_REGEX);
    }
    
    /**
     * Calls the {@link #tearDown(String)} of the parent class with the name of this class.
     */
    @AfterClass
    public static void tearDown() {
        tearDown("AxtlsCommitsTests");
    }

    /**
     * Tests the successful termination of the commit analysis process.
     */
    @Test
    public void testCommitAnalysisSuccessful() {
        assertTrue("The commit analysis process should terminate successfully", commitAnalysisSuccessful);
    }
    
    /**
     * Tests whether the analysis provides the correct number of changed code files.
     */
    @Test
    public void testCorrectChangedCodeFileCount() {
        int actualChangedCodeFiles = getAnalysisResultValue(testCommitFileName, AnalysisResultElement.CCF);
        assertEquals("The number of changed code files for commit \"" + testCommitFileName + "\" is incorrect",
                expectedChangedCodeFiles, actualChangedCodeFiles);
    }
    
    /**
     * Tests whether the analysis provides the correct number of changed code lines containing artifact-specific 
     * information.
     */
    @Test
    public void testCorrectChangedCodeLinesArtifactSpecificCount() {
        int actualChangedCodeLinesArtifactSpecific = getAnalysisResultValue(testCommitFileName,
                AnalysisResultElement.CCLAI);
        assertEquals("The number of changed code lines containing artifact-specific information for commit \"" 
                + testCommitFileName + "\" is incorrect", expectedChangedCodeLinesArtifactSpecific,
                actualChangedCodeLinesArtifactSpecific);
    }
    
    /**
     * Tests whether the analysis provides the correct number of changed code lines containing variability information. 
     */
    @Test
    public void testCorrectChangedCodeLinesVariabilityCount() {
        int actualChangedCodeLinesVariability = getAnalysisResultValue(testCommitFileName, AnalysisResultElement.CCLVI);
        assertEquals("The number of changed code lines containing variability information for commit \"" 
                + testCommitFileName + "\" is incorrect", expectedChangedCodeLinesVariability,
                actualChangedCodeLinesVariability);
    }
    
    /**
     * Tests whether the analysis provides the correct number of changed build files.
     */
    @Test
    public void testCorrectChangedBuildFileCount() {
        int actualChangedBuildFiles = getAnalysisResultValue(testCommitFileName, AnalysisResultElement.CBF);
        assertEquals("The number of changed build files for commit \"" + testCommitFileName + "\" is incorrect",
                expectedChangedBuildFiles, actualChangedBuildFiles);
    }
    
    /**
     * Tests whether the analysis provides the correct number of changed build lines containing artifact-specific 
     * information.
     */
    @Test
    public void testCorrectChangedBuildLinesArtifactSpecificCount() {
        int actualChangedBuildLinesArtifactSpecific = getAnalysisResultValue(testCommitFileName,
                AnalysisResultElement.CBLAI);
        assertEquals("The number of changed build lines containing artifact-specific information for commit \"" 
                + testCommitFileName + "\" is incorrect", expectedChangedBuildLinesArtifactSpecific,
                actualChangedBuildLinesArtifactSpecific);
    }
    
    /**
     * Tests whether the analysis provides the correct number of changed build lines containing variability information.
     */
    @Test
    public void testCorrectChangedBuildLinesVariabilityCount() {
        int actualChangedBuildLinesVariability = getAnalysisResultValue(testCommitFileName,
                AnalysisResultElement.CBLVI);
        assertEquals("The number of changed code lines containing variability information for commit \"" 
                + testCommitFileName + "\" is incorrect", expectedChangedBuildLinesVariability,
                actualChangedBuildLinesVariability);
    }
    
    /**
     * Tests whether the analysis provides the correct number of changed variability model files.
     */
    @Test
    public void testCorrectChangedModelFileCount() {
        int actualChangedModelFiles = getAnalysisResultValue(testCommitFileName, AnalysisResultElement.CMF);
        assertEquals("The number of changed model files for commit \"" + testCommitFileName + "\" is incorrect",
                expectedChangedModelFiles, actualChangedModelFiles);
    }
    
    /**
     * Tests whether the analysis provides the correct number of changed variability model lines containing
     * artifact-specific information.
     */
    @Test
    public void testCorrectChangedModelLinesArtifactSpecificCount() {
        int actualChangedModelLinesArtifactSpecific = getAnalysisResultValue(testCommitFileName,
                AnalysisResultElement.CMLAI);
        assertEquals("The number of changed model lines containing artifact-specific information for commit \"" 
                + testCommitFileName + "\" is incorrect", expectedChangedModelLinesArtifactSpecific,
                actualChangedModelLinesArtifactSpecific);
    }
    
    /**
     * Tests whether the analysis provides the correct number of changed variability model lines containing variability
     * information.
     */
    @Test
    public void testCorrectChangedModelLinesVariabilityCount() {
        int actualChangedModelLinesVariability = getAnalysisResultValue(testCommitFileName, 
                AnalysisResultElement.CMLVI);
        assertEquals("The number of changed model lines containing variability information for commit \"" 
                + testCommitFileName + "\" is incorrect", expectedChangedModelLinesVariability,
                actualChangedModelLinesVariability);
    }
    
}
