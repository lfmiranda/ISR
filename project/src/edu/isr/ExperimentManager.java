package edu.isr;

import edu.isr.data.Fold;
import edu.isr.data.InputHandler;
import edu.isr.data.InstancesWeighting;
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
    void runExperiment() throws Exception {
        ArrayList<Fold> trFolds = InputHandler.readTrFolds(params, false);

        for (Fold currTrFold : trFolds) {
            InstancesWeighting.rankInstances(currTrFold, expId, params);
        }
    }
}
