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
        for (int currRank = fold.getNumInst(); currRank >= 1; currRank--) {
            Instance instSmallestWeight = fold.getInstSmallestWeight(); // selects the next less important instance

            instSmallestWeight.setRank(currRank); // ranks the instance by its order of elimination
            instSmallestWeight.setWeight(Double.POSITIVE_INFINITY); // the instance will be disregarded from now

            /* For instances with rank value smaller than the number of neighbors, the relative position in the ranking
            is not relevant (and, by construction, impossible to determine). Therefore, only the first steps of the
            ranking are performed for these instances. */
            if (currRank <= params.getNumNeighbors()) continue;

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
    private static void applySelection(Fold origFold, Fold fold, String expId, ParametersManager params)
            throws IOException {
        int numInst = fold.getNumInst();
        int numInstRemoved = (int) Math.round(params.getSelectionLevel() / 100 * numInst);
        int numInstKept = numInst - numInstRemoved;

        Instance[] instKept = new Instance[numInstKept];

        int index = 0;
        for (int i = 0; i < numInst; i++) {
            Instance currInst = fold.getInst(i);

            if (currInst.getRank() <= numInstKept) {
                instKept[index] = currInst;
                index++;
            }
        }

        System.out.println("Number of instances kept: " + numInstKept);
        System.out.println("Number of instances removed: " + numInstRemoved);
        System.out.println("Total number of instances: " + numInst + "\n");

        assert index + numInstRemoved == numInst : "Something's wrong!";

        OutputHandler.writeInstances(instKept, expId, params, fold.getFoldId());
    }
}
