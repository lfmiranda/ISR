package edu.isr;

import edu.isr.data.Fold;
import edu.isr.data.InputHandler;
import edu.isr.data.InstanceSelection;
import edu.isr.data.InstanceWeighting;
import edu.isr.data.OutputHandler;
import edu.isr.data.ParametersManager;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Responsible for triggering the main operations during the experiment.
 */
class ExperimentManager {
    private final String expId;
    private final ParametersManager params = new ParametersManager();

    /**
     * Parses the command line and set all the parameters necessary for running the experiment.
     * @param args Command line arguments.
     * @throws Exception If required arguments or parameters are not provided correctly or if some error occurs while
     * reading or creating files.
     */
    ExperimentManager(String[] args) throws Exception {
        params.parseCommandLine(args);
        params.setParameters();

        expId = params.getWeightingFunction() + "-L" + params.getDistMetric() + "-k" + params.getNumNeighbors();

        // registers and displays the loaded parameters
        OutputHandler.logLoadedParameters(expId, params);
        OutputHandler.printLoadedParameters(params);
    }

    /**
     * Runs the experiment.
     * @throws IOException If some error occurs while creating the output files.
     */
    void runExperiment() throws IOException {
        ArrayList<Fold> origTrFolds = InputHandler.readTrFolds(params, "orig");
        ArrayList<Fold> normTrFolds = InputHandler.readTrFolds(params, "norm");

        int numOrigTrFolds = origTrFolds.size();
        int numNormTrFolds = normTrFolds.size();

        assert numOrigTrFolds > 0 : "original training folds not found.";
        assert numNormTrFolds > 0 : "normalized training folds not found.";
        assert numOrigTrFolds == numNormTrFolds : "number of original and normalized training folds should be the same";

        for (int i = 0; i < numOrigTrFolds; i++) {
            System.out.println("Working on fold " + origTrFolds.get(i).getFoldId() + "...");
            InstanceWeighting.rankInstances(normTrFolds.get(i), expId, params);
            InstanceSelection.selectInstances(origTrFolds.get(i), normTrFolds.get(i), expId, params);
        }
    }
}
