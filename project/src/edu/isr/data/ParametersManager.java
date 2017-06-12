package edu.isr.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Responsible for parsing command line arguments and setting up the parameters necessary for running the experiment.
 */
public class ParametersManager {
    private Options commandLineOptions;
    private CommandLine parsedArgs;

    private Properties loadedParameters; // parameters loaded from the parameter file

    // the loaded parameters will be written in a file, which will be saved in the output directory
    private final StringBuilder loadedParametersLog;

    // parameters
    private String inPath;
    private String outPath;
    private String datasetName;
    private String scheme;
    private String selectionLevel;
    private String distMetric;

    /**
     * Create a new {@code ParametersManager} and set the command line options.
     */
    public ParametersManager() {
        setOptions();
        loadedParametersLog = new StringBuilder();
    }

    /**
     * Set the command line options. There are two options:
     * -p: The path for the parameter file (mandatory).
     * -h: A flag indicating that the program should create a parameter file model (optional).
     */
    private void setOptions() {
        commandLineOptions = new Options();

        commandLineOptions.addOption(Option.builder("p")
                .required(true)
                .hasArg()
                .desc("Path for the parameter file")
                .type(String.class)
                .build());

        commandLineOptions.addOption(Option.builder("h")
                .required(false)
                .desc("Indicates that the program should create a parameter file model.")
                .type(String.class)
                .build());
    }

    /**
     * Parameter list.
     */
    private enum ParameterList {
        PARENT_FILE("parent", "Path to the parent parameter file." +
                "Parameters in the child file overwrite parameters with the same name in the parent file."),
        INPUT_PATH("input.path", "Path to the folder containing the input files."),
        OUTPUT_PATH("output.path", "Path to the output folder."),
        DATASET_NAME("dataset.name", "Dataset name."),
        SCHEME("scheme", "Weighting scheme."),
        SELECTION_LEVEL("selection.level", "Percentage of instances removed."),
        DISTANCE_METRIC("distance.metric", "Distance metric.");

        public final String name;
        public final String description;

        ParameterList(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    /**
     * Parse the command line arguments.
     * @param  args Command line arguments.
     * @throws IOException If a required argument is not present or if some error occurs while creating the model file.
     * @throws ParseException If an error is reached while parsing the command line.
     */
    public void parseCommandLine(String[] args) throws IOException, ParseException {
        try {
            CommandLineParser parser = new DefaultParser();
            parsedArgs = parser.parse(commandLineOptions, args);

            if (parsedArgs.hasOption("h")) writeParameterFileModel();
        } catch (MissingOptionException e) {
            throw new MissingOptionException("Usage: java ISR.jar -p parameter_file [-h]");
        } catch (ParseException e) {
            throw new ParseException("Error while parsing the command line.");
        }
    }

    /**
     * Load and set all the parameters necessary for running the experiment.
     * @throws IOException If some error occurs while reading any of the parameter files.
     * @throws MissingOptionException If a required parameter was not found in any of the parameter files.
     */
    public void setParameters() throws IOException, MissingOptionException {
        String parameterFilePath = parsedArgs.getOptionValue("p");

        loadedParameters = loadParameterFile(parameterFilePath);
        assignParameters();
    }

    /**
     * Load all the parameters.
     * @param parameterFilePath Path to the child parameter file.
     * @return A set of properties containing all the loaded parameters.
     * @throws IOException If some error occurs while reading any of the parameter files.
     */
    private Properties loadParameterFile(String parameterFilePath) throws IOException {
        parameterFilePath = parameterFilePath.replaceFirst("^~", System.getProperty("user.home"));

        File parameterFile = new File(parameterFilePath);
        Properties loadedParameters = new Properties();

        try (FileInputStream inputStream = new FileInputStream(parameterFile)) {
            loadedParameters.load(inputStream);

            /*
             * If the "parent" parameter is present in the parameter file, then we repeat the loading process for the
             * parent file and merge the sets of parameters.
             */
            if (loadedParameters.containsKey(ParameterList.PARENT_FILE.name)) {
                Properties parentParameters = loadParameterFile(loadedParameters.getProperty("parent").trim());
                parentParameters.putAll(loadedParameters);
                loadedParameters = parentParameters;
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Parameter file not found: " + parameterFilePath + ".");
        } catch (IOException e) {
            throw new IOException("Error while reading the parameter file.");
        }

        return loadedParameters;
    }

    /**
     * Create a file with the name and description of all parameters.
     * @throws IOException If some error occurs while creating the model file.
     */
    private void writeParameterFileModel() throws IOException {
        try (PrintWriter out = new PrintWriter("parameterFileModel.txt", "UTF-8")) {
            for(ParameterList p : ParameterList.values()) {
                out.println("# " + p.description);
                out.println(p.name + " = \n");
            }
        } catch (IOException e) {
            throw new FileNotFoundException("Error while writing the parameter file model.");
        }
    }

    /**
     * Assign the loaded parameter to the corresponding variables.
     * @throws MissingOptionException If a required parameter is not found in any of the parameter files.
     */
    private void assignParameters() throws MissingOptionException {
        inPath = getStringParameter(ParameterList.INPUT_PATH, true);
        outPath = getStringParameter(ParameterList.OUTPUT_PATH, true);
        datasetName = getStringParameter(ParameterList.DATASET_NAME, false);
        scheme = getStringParameter(ParameterList.SCHEME, false);
        selectionLevel = getStringParameter(ParameterList.SELECTION_LEVEL, false);
        distMetric = getStringParameter(ParameterList.DISTANCE_METRIC, false);
    }

    /**
     * Return a specific parameter.
     * @param key The name of the parameter.
     * @param isFile Flag indicating if the string describes a file path.
     * @return The parameter loaded from the parameter file.
     * @throws MissingOptionException If the parameter is mandatory and is not present in any of the parameter files.
     */
    private String getStringParameter(ParameterList key, boolean isFile) throws MissingOptionException {
        boolean keyPresent = loadedParameters.containsKey(key.name);

        /*
         * So far all parameters for which this method is called are mandatory, so an exception is thrown if any of them
         * are not present.
         */
        if (!keyPresent) throw new MissingOptionException("The parameter \"" + key.name + "\" was not found.");

        String parameterValue = loadedParameters.getProperty(key.name);

        if (parameterValue.isEmpty()) throw new MissingOptionException("Empty parameter: \"" + key.name + "\".");

        String parameter = parameterValue.replaceAll("\\s", ""); // get the parameter value
        if (isFile) parameter = parameter.replaceFirst("^~",System.getProperty("user.home"));
        loadedParametersLog.append(key.name).append(" = ").append(parameter).append("\n");

        return parameter;
    }

    /**
     * Return a log with parameter values. Useful for keeping track of the parameters used in the experiment.
     * @return A structured string containing the name and description of all parameters.
     */
    StringBuilder getLoadedParametersLog() {
        return loadedParametersLog;
    }

    /**
     * Return the path to the files that should be used as input in the experiment. At first, these files will be
     * training sets only, since the ISR algorithm is currently applied as an pre-processing step. Test sets will not be
     * read.
     * @return The input path.
     */
    public String getInPath() {
        return inPath;
    }

    /**
     * Return the path where the output files should be saved.
     * @return The output path.
     */
    public String getOutPath() {
        return outPath;
    }

    /**
     * Return the name of the dataset used in the experiment.
     * @return The dataset name.
     */
    public String getDatasetName() {
        return datasetName;
    }

    /**
     * Return the scheme that will be used in order to weigh the instances. Three schemes were implemented: proximity,
     * surrounding, and remoteness (which combines the first two). There is also a random scheme, which can be used for
     * validation purposes.
     * @return The weighting scheme.
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * Return an identifier that indicates the percentage of instances that should be removed. This identifier needs to
     * be parsed to a numeric value before being used.
     * @return The selection level.
     */
    public String getSelectionLevel() {
        return selectionLevel;
    }

    /**
     * Return the metric used in order to calculate distances between instances. Two metrics were implemented: Euclidean
     * and fractional.
     * @return The distance metric.
     */
    public String getDistMetric() {
        return distMetric;
    }
}
