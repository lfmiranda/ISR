package edu.isr.data;

import java.io.IOException;

/**
 * Responsible for selecting instances according to their relative importance.
 */
public class InstanceSelection {
    /**
     * Selects a subset of instances according to their relative importance.
     * @param origFold Set of instances on which the the selection will be applied after the ranking process.
     * @param normFold Set of instances to be weighted.
     * @param expId Identifier based on the names of the weighting function, neighborhood size, and the distance metric.
     * @param params Experiment parameters.
     * @throws IOException If the output file was not found or could not be written.
     */
    public static void selectInstances(Fold origFold, Fold normFold, String expId, ParametersManager params)
            throws IOException {
        determineFinalRanks(normFold, params);

        for (double selectionLevel : params.getSelectionLevels())
            applySelection(origFold, normFold, expId, params, selectionLevel);
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

            // removes the traces of the ranked instance out of the other instances
            fold.clearInstTraces(instSmallestWeight);

            /* For instances with rank value smaller than the number of neighbors, the relative position in the ranking
            is irrelevant (and, by construction, impossible to determine). Therefore, only the first steps of the
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
     * @param origFold The fold before the application of the normalization step.
     * @param normFold The fold in which the selection will be based.
     * @param expId Identifier based on the names of the weighting function, neighborhood size, and the distance metric.
     * @param params Experiment parameters.
     * @param selectionLevel Current selection level.
     * @throws IOException If the output file was not found or could not be written.
     */
    private static void applySelection(Fold origFold, Fold normFold, String expId, ParametersManager params,
                                       double selectionLevel) throws IOException {
        int numInst = normFold.getNumInst();
        int numInstRemoved = (int) Math.round(selectionLevel / 100 * numInst);
        int numInstKept = numInst - numInstRemoved;

        Instance[] instKept = new Instance[numInstKept];

        int index = 0;
        for (int i = 0; i < numInst; i++) {
            Instance currInst = normFold.getInst(i);

            if (currInst.getRank() <= numInstKept) {
                instKept[index] = origFold.getInst(i);
                index++;
            }
        }

        System.out.println("  Number of instances kept: " + numInstKept);
        System.out.println("  Number of instances removed: " + numInstRemoved);
        System.out.println("  Total number of instances: " + numInst + "\n");

        assert index + numInstRemoved == numInst : "The index at the end of the for loop responsible for selecting" +
                " instances (" + index + ") should be the same as the number of instances that should be kept (" +
                numInstKept + ").";

        OutputHandler.writeInstances(instKept, expId, params, origFold.getFoldId(), selectionLevel);
    }
}
