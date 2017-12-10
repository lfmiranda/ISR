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

    // parameters
    private String origFoldsPath;
    private String normFoldsPath;
    // private String foldsPath;
    private String outPath;
    private String datasetName;
    private String weightingFunction;
    private double[] selectionLevels;
    private double distMetric;
    private int numNeighbors;
    private String combMethod;

    // the loaded parameters will be written in a file, which will be saved in the output directory
    private final StringBuilder loadedParametersLog = new StringBuilder();

    /**
     * Creates a new {@code ParametersManager} and sets the command line options.
     */
    public ParametersManager() {
        setOptions();
    }

    /**
     * Sets the command line options. There are two options:
     * -p: The path for the parameter file (mandatory).
     * -h: A flag indicating that the program should create a parameter file model (optional).
     */
    private void setOptions() {
        commandLineOptions = new Options();

        commandLineOptions.addOption(Option.builder("p")
                .required(false)
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
     * Parses the command line arguments.
     * @param args Command line arguments.
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
     * Creates a file (in the directory where the program is called) with the name and description of all parameters.
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
     * List and description of all available parameters.
     */
    private enum ParameterList {
        PARENT_FILE("parent", "Path to the parent parameter file. Parameters in the child file overwrite parameters " +
                "with the same name in the parent file."),
        ORIGINAL_FOLDS_PATH("original.folds.path", "Path to the folder containing the original folds."),
        NORMALIZED_FOLDS_PATH("normalized.folds.path", "Path to the folder containing the normalized folds."),
        /* INPUT_PATH("input.path", "Path to the folder containing the input folds. It is the same as the " +
                "\"original.folds.path\" parameter if no dimensionality reduction method was applied to the datasets."),*/
        OUTPUT_PATH("output.path", "Path to the output folder."),
        DATASET_NAME("dataset.name", "Dataset name."),
        WEIGHTING_FUNCTION("weighting.function", "Weighting function."),
        SELECTION_LEVELS("selection.levels", "Array with the percentages of instances that should be removed. Each" +
                "value corresponds to a set of output files."),
        DISTANCE_METRIC("distance.metric", "Distance metric."),
        NUM_NEIGHBORS("number.neighbors", "Number of instances taken as neighbors."),
        COMB_METHOD("combination.method", "Method use to combine weights when using the remoteness weighting function.");

        final String name;
        final String description;

        ParameterList(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    /**
     * Loads and sets all the parameters necessary for running the experiment.
     * @throws IOException If some error occurs while reading any of the parameter files.
     * @throws MissingOptionException If a required parameter was not found in any of the parameter files.
     * @throws NumberFormatException If a parameter that is supposed to be integer is actually a string.
     */
    public void setParameters() throws IOException, MissingOptionException, NumberFormatException {
        String parameterFilePath = parsedArgs.getOptionValue("p");

        if (parameterFilePath == null)
            return;
        else
            loadedParameters = loadParameterFile(parameterFilePath);

        assignParameters();
    }

    /**
     * Loads all the parameters.
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
     * Assigns the loaded parameter to the corresponding variables.
     * @throws MissingOptionException If a required parameter is not found in any of the parameter files.
     * @throws NumberFormatException If a parameter that is supposed to be integer is actually a string.
     */
    private void assignParameters() throws MissingOptionException {
        origFoldsPath = getStringParameter(ParameterList.ORIGINAL_FOLDS_PATH, true);
        normFoldsPath = getStringParameter(ParameterList.NORMALIZED_FOLDS_PATH, true);
        // foldsPath = getStringParameter(ParameterList.INPUT_PATH, true);
        outPath = getStringParameter(ParameterList.OUTPUT_PATH, true);
        datasetName = getStringParameter(ParameterList.DATASET_NAME, false);
        weightingFunction = getStringParameter(ParameterList.WEIGHTING_FUNCTION, false);
        selectionLevels = getDoubleArrayParameter(ParameterList.SELECTION_LEVELS);
        distMetric = getDoubleParameter(ParameterList.DISTANCE_METRIC);
        numNeighbors = getIntegerParameter(ParameterList.NUM_NEIGHBORS);
        combMethod = getStringParameter(ParameterList.COMB_METHOD, false);

        assertParameters();
    }

    /**
     * Checks if the weighting function and the selection levels parameters have valid values.
     */
    private void assertParameters() {
        assert weightingFunction.equals("proximity-x") || weightingFunction.equals("proximity-xy") ||
                weightingFunction.equals("surrounding-x") || weightingFunction.equals("surrounding-xy") ||
                weightingFunction.equals("remoteness-x") || weightingFunction.equals("remoteness-xy") ||
                weightingFunction.equals("nonlinearity") : "invalid weighting function.";

        for (double selectionLevel : selectionLevels)
            assert (selectionLevel >= 0) && (selectionLevel <= 100) : "invalid selection level.";

        assert (combMethod.equals("cardinal") || combMethod.equals("ordinal")) : "invalid combination method.";
    }

    /**
     * Loads an integer parameter from the parameter file.
     * @param key The name of the parameter.
     * @return The parameter loaded from the parameter file.
     * @throws MissingOptionException If the parameter is mandatory and is not present in any of the parameter files.
     * @throws NumberFormatException If the loaded value is actually a string.
     */
    private int getIntegerParameter(ParameterList key) throws MissingOptionException, NumberFormatException {
        boolean keyPresent = loadedParameters.containsKey(key.name);

        // so far all integer parameters are mandatory, so an exception is thrown if any of them are not present
        if (!keyPresent) throw new MissingOptionException("The parameter \"" + key.name + "\" was not found.");

        try {
            // gets the parameter value
            int parameterValue = Integer.parseInt(loadedParameters.getProperty(key.name).replaceAll("\\s", ""));

            // stores the value in the log file
            loadedParametersLog.append(key.name).append(" = ").append(parameterValue).append("\n");

            return parameterValue;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("The parameter \"" + key.name + "\" could not be converted to int.");
        }
    }

    /**
     * Loads a double parameter from the parameter file.
     * @param key The name of the parameter.
     * @return The parameter loaded from the parameter file.
     * @throws MissingOptionException If the parameter is mandatory and is not present in any of the parameter files.
     * @throws NumberFormatException If the loaded value is actually a string.
     */
    private double getDoubleParameter(ParameterList key) throws MissingOptionException {
        boolean keyPresent = loadedParameters.containsKey(key.name);

        // so far all double parameters are mandatory, so an exception is thrown if any of them are not present
        if (!keyPresent) throw new MissingOptionException("The parameter \"" + key.name + "\" was not found.");

        try {
            // gets the parameter value
            double parameterValue = Double.parseDouble(loadedParameters.getProperty(key.name).replaceAll("\\s", ""));

            // stores the value in the log file
            loadedParametersLog.append(key.name).append(" = ").append(parameterValue).append("\n");

            return parameterValue;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("The parameter \"" + key.name + "\" could not be converted to double.");
        }
    }

    /**
     * Loads a double array parameter from the parameter file.
     * @param key The name of the parameter.
     * @return The parameter loaded from the parameter file.
     * @throws MissingOptionException If the parameter is mandatory and is not present in any of the parameter files.
     * @throws NumberFormatException If the loaded value is actually a string.
     */
    private double[] getDoubleArrayParameter(ParameterList key) throws MissingOptionException {
        boolean keyPresent = loadedParameters.containsKey(key.name);

        // so far all double parameters are mandatory, so an exception is thrown if any of them are not present
        if (!keyPresent) throw new MissingOptionException("The parameter \"" + key.name + "\" was not found.");

        try {
            String loadedParameter = loadedParameters.getProperty(key.name).replaceAll("\\s", "");
            int numSelectionLevels = loadedParameter.split(",").length;

            double[] parameterValue = new double[numSelectionLevels];

            // gets the parameter values
            for (int i = 0; i < numSelectionLevels; i++)
                parameterValue[i] = Double.parseDouble(loadedParameter.split(",")[i]);

            // stores the value in the log file
            loadedParametersLog.append(key.name).append(" = ").append(loadedParameter).append("\n");

            return parameterValue;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("The parameter \"" + key.name + "\" could not be converted to double.");
        }
    }

    /**
     * Loads a string parameter from the parameter file.
     * @param key The name of the parameter.
     * @param isFile Flag indicating if the string describes a file path.
     * @return The parameter loaded from the parameter file.
     * @throws MissingOptionException If the parameter is mandatory and is not present in any of the parameter files.
     */
    private String getStringParameter(ParameterList key, boolean isFile) throws MissingOptionException {
        boolean keyPresent = loadedParameters.containsKey(key.name);

        /*
         * So far all string parameters for which this method is called are mandatory, so an exception is thrown if any
         * of them are not present.
         */
        if (!keyPresent) throw new MissingOptionException("The parameter \"" + key.name + "\" was not found.");

        // gets the parameter value
        String parameterValue = loadedParameters.getProperty(key.name).replaceAll("\\s", "");

        // stores the value in the log file
        if (parameterValue.isEmpty()) throw new MissingOptionException("Empty parameter: \"" + key.name + "\".");

        parameterValue = parameterValue.replaceAll("\\s", "");
        if (isFile) parameterValue = parameterValue.replaceFirst("^~",System.getProperty("user.home"));

        // stores the value in the log file
        loadedParametersLog.append(key.name).append(" = ").append(parameterValue).append("\n");

        return parameterValue;
    }

    /**
     * Return a log with parameter values. Useful for keeping track of the parameters used in the experiment.
     * @return A structured string containing the name and description of all parameters.
     */
    StringBuilder getLoadedParametersLog() {
        return loadedParametersLog;
    }

    /**
     * After the execution of the weighting step, each instance get a rank. The construction of this rank may be based
     * on the folds created after a normalization or dimensionality reduction step. The selection, however, is made over
     * the original folds, which the path is stored by the variable {@code normFoldsPath} or {@code foldsPath}.
     * @return The path to the folder containing the original folds.
     */
    String getOrigFoldsPath() {
        return origFoldsPath;
    }

    /**
     * Get the path for the normalized folds, which should be used for the weighting and ranking process.
     * @return The path to the folder containing the normalized folds.
     */
    String getNormFoldsPath() {
        return normFoldsPath;
    }

    /*
    /**
     * Returns the path to the folder containing the input folds (possibly created during a dimensionality reduction
     * step) that should be used as input in the experiment. For real experiments, only training folds should be read.
     * @return The path to the input folds.
     */
    /* String getFoldsPath() {
        return foldsPath;
    } */

    /**
     * Returns the path where the output files should be saved.
     * @return The output path.
     */
    String getOutPath() {
        return outPath;
    }

    /**
     * Returns the name of the dataset used in the experiment.
     * @return The dataset name.
     */
    String getDatasetName() {
        return datasetName;
    }

    /**
     * Returns the weighting function that will be used to weigh the instances. Four functions were implemented:
     * proximity, surrounding, remoteness (which combines the first two), and non-linearity.
     * @return The weighting function.
     */
    public String getWeightingFunction() {
        return weightingFunction;
    }

    /**
     * Returns the array with the percentages of instances that should be removed. A value 0 means that the instance
     * selection step should not be performed.
     * @return The array containing all selection levels.
     */
    double[] getSelectionLevels() {
        return selectionLevels;
    }

    /**
     * Returns the metric used in order to calculate distances between instances. Two metrics were implemented: Euclidean
     * and fractional.
     * @return The distance metric.
     */
    public double getDistMetric() {
        return distMetric;
    }

    /**
     * Returns the number of instances taken as neighbors when applying methods that depend on the notion of closeness.
     * @return The number of neighbors.
     */
    public int getNumNeighbors() {
        return numNeighbors;
    }

    /**
     * Returns the strategy used to combine the proximity and surrounding weighting functions when applying the
     * remoteness function.
     * @return "ordinal" if the ranks were combined or "cardinal" if the weights were combined.
     */
    String getCombMethod() {
        return combMethod;
    }
}
