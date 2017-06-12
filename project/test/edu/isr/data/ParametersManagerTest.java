package edu.isr.data;

import org.apache.commons.cli.MissingOptionException;
import org.junit.Test;

import java.io.FileNotFoundException;

public class ParametersManagerTest {
    private ParametersManager parameters;

    /**
     * Since the option "-p" is mandatory, the method {@code parseCommandLine} should throw a
     * {@code MissingOptionException} if such option is not found while parsing the command line.
     * @throws Exception If required arguments or parameters were not provided correctly or if some error occurs while
     * reading or creating files.
     */
    @Test(expected = MissingOptionException.class)
    public void parseCommandLineWithoutRequiredOption() throws Exception {
        String[] args = {"-h"};

        parameters = new ParametersManager();
        parameters.parseCommandLine(args);
    }

    /**
     * The method {@code setParameters} should throw a {@code FileNotFoundException} if the parameter files are not
     * present in the path indicated in the command line (for the child parameter file) or in the "parent" parameter.
     * @throws Exception If required arguments or parameters were not provided correctly or if some error occurs while
     * reading or creating files.
     */
    @Test(expected = FileNotFoundException.class)
    public void loadNonexistentParameterFile() throws Exception {
        String[] args = {"-p", "parameters/nonexistent_file.txt", "-h"};
        parameters = new ParametersManager();
        parameters.parseCommandLine(args);
        parameters.setParameters();
    }

    /**
     * The method {@code setParameters} should throw a {@code MissingOptionException} if one of the parameters are
     * missing.
     * @throws Exception If required arguments or parameters were not provided correctly or if some error occurs while
     * reading or creating files.
     */
    @Test(expected = MissingOptionException.class)
    public void loadParameterFileWithMissingParameter() throws Exception {
        String[] args = {"-p", "parameters/parameter_file-with_missing_parameter.txt", "-h"};
        parameters = new ParametersManager();
        parameters.parseCommandLine(args);
        parameters.setParameters();
    }

    /**
     * The method {@code setParameters} should throw a {@code MissingOptionException} if one of the parameters are
     * empty.
     * @throws Exception If required arguments or parameters were not provided correctly or if some error occurs while
     * reading or creating files.
     */
    @Test(expected = MissingOptionException.class)
    public void loadParameterFileWithEmptyParameter() throws Exception {
        String[] args = {"-p", "parameters/parameter_file-with_empty_parameter.txt", "-h"};
        parameters = new ParametersManager();
        parameters.parseCommandLine(args);
        parameters.setParameters();
    }
}
