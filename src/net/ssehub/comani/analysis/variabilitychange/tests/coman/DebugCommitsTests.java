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
package net.ssehub.comani.analysis.variabilitychange.tests.coman;

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

/**
 * This class provides some {@link net.ssehub.comani.analysis.variabilitychange.core.VariabilityChangeAnalyzer} tests
 * based on selected, (partial) real commits previously causing problems during the development of the ComAn
 * tool-set.<br>
 * <br>
 * These tests were used initially to check the correct counting and categorization of changed lines of the ComAn 
 * tool-set.
 * 
 * @author Christian Kröher
 *
 */
@RunWith(Parameterized.class)
public class DebugCommitsTests extends AbstractCommitsTests {
    
    /**
     * The directory in which the test commit files are located. Each file contains the information of a particular
     * commit.
     */
    private static final File TEST_COMMITS_DIRECTORY = new File(AllTests.TESTDATA_DIRECTORY, "coman/debug");
    
    /**
     * The names of the test commit files located in the {@link #TEST_COMMITS_DIRECTORY}.
     */
    private static final String[] TEST_COMMIT_FILE_NAMES = {"882cbcd.txt", "4694836.txt", "c8eedd5.txt", "e73fda8.txt",
        "5332369.txt", "caffb6e.txt", "4da1aa8.txt", "11302f3.txt", "9344bde.txt", "5c22825.txt", "8f372d0.txt",
        "5b35300.txt", "503e4fe.txt", "892d129.txt", "911cedf.txt", "ce011ec.txt", "e15dfc1.txt", "9c8a06a.txt",
        "540ae01.txt", "05f26fc.txt", "2c018fb.txt", "45cc550.txt"};
    
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
     * Constructs a new {@link DebugCommitsTests} object for injecting test parameters.
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
    public DebugCommitsTests(String testcommitFileName, int expectedChangedCodeFiles,
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
        return Arrays.asList(new Object[][] {
            {"882cbcd", 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {"4694836", 1, 13, 0, 1, 2, 0, 0, 0, 0},
            {"c8eedd5", 2, 3, 0, 0, 0, 0, 0, 0, 0},
            {"e73fda8", 2, 298, 0, 0, 0, 0, 0, 0, 0},
            {"5332369", 2, 0, 0, 0, 0, 0, 0, 0, 0},
            {"caffb6e", 5, 622, 0, 1, 6, 0, 0, 0, 0},
            {"4da1aa8", 11, 270, 13, 0, 0, 0, 0, 0, 0},
            {"11302f3", 1, 9, 2, 1, 0, 1, 1, 0, 10},
            {"9344bde", 11, 48, 0, 0, 0, 0, 0, 0, 0},
            {"5c22825", 8, 1600, 18, 0, 0, 0, 0, 0, 0},
            {"8f372d0", 4, 342, 0, 0, 0, 0, 0, 0, 0},
            {"5b35300", 4, 11, 4, 0, 0, 0, 0, 0, 0},
            {"503e4fe", 1, 7, 6, 0, 0, 0, 1, 0, 1},
            {"892d129", 15, 1918, 44, 1, 19, 5, 3, 0, 81},
            {"911cedf", 4, 69, 8, 0, 0, 0, 1, 3, 3},
            {"ce011ec", 7, 82, 5, 1, 6, 0, 0, 0, 0},
            {"e15dfc1", 1, 372, 0, 0, 0, 0, 0, 0, 0},
            {"9c8a06a", 4, 498, 0, 0, 0, 0, 0, 0, 0},
            {"540ae01", 7, 207, 0, 0, 0, 0, 0, 0, 0},
            {"05f26fc", 12, 1185, 0, 1, 4, 0, 0, 0, 0},
            {"2c018fb", 29, 949, 12, 0, 0, 0, 0, 0, 0},
            {"45cc550", 4, 649, 12, 0, 0, 0, 0, 0, 0}
            });
    }
    
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
        tearDown("DebugCommitsTests");
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
