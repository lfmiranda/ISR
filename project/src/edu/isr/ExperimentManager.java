package edu.isr;

import edu.isr.data.Fold;
import edu.isr.data.InputHandler;
import edu.isr.data.OutputHandler;
import edu.isr.data.ParametersManager;

import java.io.IOException;
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

        new OutputHandler(params);
        if (!params.getSelectionLevel().equals("no_selection")) OutputHandler.writeLoadedParametersLog(params);
    }

    /**
     * Run the experiment.
     * @throws IOException If some error occurs while creating the output files.
     */
    void runExperiment() throws IOException {
        ArrayList<Fold> embeddingFolds = InputHandler.readFolds(params, false);

        for (Fold currFold : embeddingFolds) {
            currFold.normalizeData();
            currFold.assignRanks(params);
        }
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
