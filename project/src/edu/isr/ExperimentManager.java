package edu.isr;

import edu.isr.data.OutputHandler;
import edu.isr.data.ParametersManager;

/**
 * Responsible for triggering the main operations during the experiment.
 */
class ExperimentManager {
    private final ParametersManager parameters;

    /**
     * Parse the command line and set all the parameters necessary for running the experiment.
     * @param args Command line arguments.
     * @throws Exception If required arguments or parameters are not provided correctly or if some error occurs while
     * reading or creating files.
     */
    ExperimentManager(String[] args) throws Exception {
        parameters = new ParametersManager();
        parameters.parseCommandLine(args);
        parameters.setParameters();

        printLoadedParameters();

        OutputHandler outputHandler = new OutputHandler(parameters);
        outputHandler.writeLoadedParametersLog(parameters);
    }

    /**
     * Print the main parameters.
     */
    private void printLoadedParameters() {
        System.out.println("Loaded parameters:");

        System.out.println("  Input path: " + parameters.getInPath());
        System.out.println("  Output path: " + parameters.getOutPath());
        System.out.println("  Dataset name: " + parameters.getDatasetName());
        System.out.println("  Weighting scheme: " + parameters.getScheme());
        System.out.println("  Selection level: " + parameters.getSelectionLevel());
        System.out.println("  Distance metric: " + parameters.getDistMetric() + "\n");
    }
}
