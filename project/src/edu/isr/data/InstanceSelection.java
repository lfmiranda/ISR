package edu.isr.data;

import java.io.IOException;

/**
 * Responsible for selecting instances according to their relative importance.
 */
public class InstanceSelection {
    /**
     * Selects a subset of instances according to their relative importance.
     * @param fold Set of instances to be weighted.
     * @param expId Identifier based on the names of the weighting function, neighborhood size, and the distance metric.
     * @param params Experiment parameters.
     * @throws IOException If the output file was not found or could not be written.
     */
    public static void selectInstances(Fold fold, String expId, ParametersManager params) throws IOException {
        determineFinalRanks(fold, params);
        applySelection(fold, fold, expId, params);
    }

    /**
     * Determines the order of elimination of all instances.
     * @param fold Set of instances to be weighted.
     * @param params Experiment parameters.
     */
    private static void determineFinalRanks(Fold fold, ParametersManager params) {
        for (int currRank = 1; currRank <= fold.getNumInst() - params.getNumNeighbors(); currRank++) {
            Instance instSmallestWeight = fold.getInstSmallestWeight(); // selects the next less important instance

            instSmallestWeight.setRank(currRank); // ranks the instance by its order of elimination
            instSmallestWeight.setWeight(Double.POSITIVE_INFINITY); // the instance will be disregarded from now

            // updates the distances matrix, setting the distance to or from the ranked instance to infinite
            fold.updateDistMatrix(instSmallestWeight.getId());

            // updates the weights of instances that had the eliminated instance among its nearest neighbors
            InstanceWeighting.updateAssociatesWeights(fold, instSmallestWeight, params);
        }
    }

    /**
     * Selects the instances that will be written in the output file.
     * @param origFold The fold before the application of the embedding method.
     * @param fold The fold in which the selecting will be based.
     * @param expId Identifier based on the names of the weighting function, neighborhood size, and the distance metric.
     * @param params Experiment parameters.
     * @throws IOException If the output file was not found or could not be written.
     */
    private static void applySelection(Fold origFold, Fold fold, String expId, ParametersManager params) throws IOException {
        int numSelectedInst = (int) Math.round(fold.getNumInst() * (100 - params.getSelectionLevel()) / 100);
        Instance[] selectedInst = new Instance[numSelectedInst];

        int count = 0;
        for (int i = 0; i < fold.getNumInst(); i++) {
            Instance currInst = fold.getInst(i);

            if (currInst.getRank() <= numSelectedInst) {
                selectedInst[count] = currInst;
                count++;
            }
        }

        assert count == numSelectedInst : "Something's wrong!";

        OutputHandler.writeInstances(selectedInst, expId, params, fold.getFoldId());
    }
}
