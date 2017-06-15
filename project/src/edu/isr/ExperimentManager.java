package edu.isr;

import edu.isr.data.Fold;
import edu.isr.data.InputHandler;
import edu.isr.data.OutputHandler;
import edu.isr.data.ParametersManager;

import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Responsible for triggering the main operations during the experiment.
 */
class ExperimentManager {
    private final ParametersManager params;

    /**
     * Parse the command line and set all the parameters necessary for running the experiment.
     * @param args Command line arguments.
     * @throws Exception If required arguments or parameters are not provided correctly or if some error occurs while
     * reading or creating files.
     */
    ExperimentManager(String[] args) throws Exception {
        params = new ParametersManager();
        params.parseCommandLine(args);
        params.setParameters();

        printLoadedParameters();

        OutputHandler outputHandler = new OutputHandler(params);
        outputHandler.writeLoadedParametersLog(params);
    }

    /**
     * Run the experiment.
     */
    void runExperiment() throws FileNotFoundException {
        ArrayList<Fold> originalFolds = InputHandler.readFolds(params, true);
        ArrayList<Fold> embeddingFolds = InputHandler.readFolds(params, false);
    }

    /**
     * Print the main parameters.
     */
    private void printLoadedParameters() {
        System.out.println("Loaded parameters:");

        System.out.println("  Path to original folds: " + params.getOrigPath());
        System.out.println("  Input path: " + params.getInPath());
        System.out.println("  Output path: " + params.getOutPath());
        System.out.println("  Dataset name: " + params.getDatasetName());
        System.out.println("  Weighting scheme: " + params.getScheme());
        System.out.println("  Selection level: " + params.getSelectionLevel());
        System.out.println("  Distance metric: " + params.getDistMetric());
        System.out.println("  Number of neighbors: " + params.getNumNeighbors() + "\n");
    }
}
