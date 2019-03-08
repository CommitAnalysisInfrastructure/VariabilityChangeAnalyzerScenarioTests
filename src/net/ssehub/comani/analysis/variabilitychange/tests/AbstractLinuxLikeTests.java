package net.ssehub.comani.analysis.variabilitychange.tests;

import java.io.File;

import net.ssehub.comani.analysis.AnalysisSetupException;
import net.ssehub.comani.extraction.ExtractionSetupException;

/**
 * This abstract class provides common regular expressions for all test classes, which rely on commits of Linx-like
 * projects and, hence, use the same regular expressions for identifying the variability model, code, and build files.
 * 
 * @author Christian Kroeher
 *
 */
public abstract class AbstractLinuxLikeTests extends AbstractCommitsTests {

    /**
     * The regular expression for identifying variability model files in the axTLS project.
     */
    private static final String VM_FILES_REGEX = ".*/Kconfig((\\.|\\-|\\_|\\+|\\~).*)?";
    
    /**
     * The regular expression for identifying code files in the axTLS project.
     */
    private static final String CODE_FILES_REGEX = ".*/.*\\.[hcS]((\\.|\\-|\\_|\\+|\\~).*)?";
    
    /**
     * The regular expression for identifying build files in the axTLS project.
     */
    private static final String BUILD_FILES_REGEX = ".*/(Makefile|Kbuild)((\\.|\\-|\\_|\\+|\\~).*)?";
    // Possible addition: |(.*/.*\\.(mak|make)))
    
    /**
     * Calls the {@link #setUp(File, String[])} of the parent class with the given parameters, {@link #VM_FILES_REGEX},
     * {@link #CODE_FILES_REGEX}, and {@link #BUILD_FILES_REGEX} as well as "git".
     * 
     * @param testCommitsDirectory the {@link File} denoting the directory in which the test commit files are located
     * @param testCommitFileNames the names of the test commit files located in the given directory
     * @throws ExtractionSetupException if instantiating the {@link net.ssehub.comani.extraction.git.GitCommitExtractor}
     *         during {@link #setUp(File, String[])} fails
     * @throws AnalysisSetupException if instantiating the 
     *         {@link net.ssehub.comani.analysis.variabilitychange.core.VariabilityChangeAnalyzer} during
     *         {@link #setUp(File, String[])} fails
     */
    public static void setUp(File testCommitsDirectory, String[] testCommitFileNames) 
            throws ExtractionSetupException, AnalysisSetupException {
        setUp(testCommitsDirectory, testCommitFileNames, "git", VM_FILES_REGEX, CODE_FILES_REGEX, BUILD_FILES_REGEX);
    }
}
