package edu.isr.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Represents a set of instances. Each dataset may be represent by one or more folds.
 */
public class Fold {
    private final int foldId;
    private final ArrayList<Instance> instances = new ArrayList<>();

    private int numInst;
    private int numAttr;

    private double[][] distBetweenInst; // distances between the instances (always measured in the input space)

    /**
     * Builds an empty fold.
     * @param foldId The fold identifier.
     */
    public Fold(int foldId) {
        this.foldId = foldId;
    }

    /**
     * Adds a new instance to the fold.
     * @param inst The instance to be added.
     */
    void addInst(Instance inst) {
        instances.add(inst);
        numInst++;
    }

    /**
     * Measures the distance between each pair of instances, always based on the input space.
     * @param distMetric Metric used in order to calculate distances between instances.
     */
    void measureDistBetweenInst(double distMetric) {
        distBetweenInst = new double[numInst][numInst];

        for (int i = 1; i < numInst; i++) {
            double[] u = instances.get(i).getInput();

            for (int j = 0; j < i; j++) {
                double[] v = instances.get(j).getInput();

                distBetweenInst[i][j] = Utils.measureDist(u, v, numAttr - 1, distMetric);
                distBetweenInst[j][i] = distBetweenInst[i][j];
            }
        }
    }

    /**
     * Finds the set of neighbors of a specific instance.
     * @param instId Index of the instance for which we want to find the neighbors.
     * @param numNeighbors Number of instances taken as neighbors.
     */
    void findNeighbors(int instId, int numNeighbors) {
        double[] distOtherInst = distBetweenInst[instId];
        List<Map.Entry<Integer, Double>> neighborsIdsToDistances = new ArrayList<>();

        for (int i = 0; i < numInst; i++)
            neighborsIdsToDistances.add(Map.entry(i, distOtherInst[i]));

        neighborsIdsToDistances.sort(Comparator.comparingDouble(Map.Entry::getValue));

        int currRank = 0;
        int numNeighborsAdded = 0;
        while (numNeighborsAdded < numNeighbors) {
            int nextNeighborId = neighborsIdsToDistances.get(currRank).getKey();
            currRank++;

            /* Ignores the instance itself. In theory, checking for a distance value equals to zero should suffice, but
            in practice there may exist instances with same input attributes values and therefore a distance value which
            is also equals to zero. */
            if (nextNeighborId == instId)
                continue;

            getInst(instId).addNeighbor(getInst(nextNeighborId));

            if (!getInst(nextNeighborId).getAssociates().contains(getInst(instId)))
                getInst(nextNeighborId).addAssociate(getInst(instId));

            numNeighborsAdded++;
        }

        assert getInst(instId).getNeighbors().size() == numNeighbors : "incorrect number of neighbors.";
    }

    /** The instance with the smallest weight will be the next one to be ranked.
     * @return The instance with the smallest weight
     */
    Instance getInstSmallestWeight() {
        double smallestWeight = Double.POSITIVE_INFINITY;
        Instance instSmallestWeight = null;

        for (Instance inst : instances) {
            double weightCurrInst = inst.getWeight();

            if (weightCurrInst < smallestWeight) {
                smallestWeight = weightCurrInst;
                instSmallestWeight = inst;
            }
        }

        return instSmallestWeight;
    }

    /**
     * Gets the folder identifier.
     * @return An integer corresponding to the fold identifier.
     */
    public int getFoldId() {
        return foldId;
    }

    /**
     * Gets a specific instance.
     * @param instId Instance identifier. For now this id is equal to the instance index.
     * @return An instance with a specific id.
     */
    Instance getInst(int instId) {
        assert instId <= numInst - 1: "instance id must be smaller than the total number of instances.";

        return instances.get(instId);
    }


    /**
     * Gets the total number of instances of the fold.
     * @return The number of instances.
     */
    int getNumInst() {
        return numInst;
    }

    /**
     * Sets the total number of instances of the fold.
     * @param numInst The number of instances.
     */
    void setNumInst(int numInst) {
        this.numInst = numInst;
    }

    /**
     * Sets the number of attributes (input + output) of the instances (which is the same for all of them).
     * @param numAttr The number of attributes.
     */
    void setNumAttr(int numAttr) {
        this.numAttr = numAttr;
    }

    /**
     * During the selection process, when an instance gets its rank, it should be disregarded from that point forward.
     * This method updates the matrix distBetweenInst, setting the distance to or from the disregarded instance to
     * infinite.
     * @param instId The id of the instance that should be disregarded.
     */
    void updateDistMatrix(int instId) {
        Arrays.fill(distBetweenInst[instId], Double.POSITIVE_INFINITY);

        for (int i = 0; i < numInst; i++) {
            distBetweenInst[i][instId] = Double.POSITIVE_INFINITY;
        }
    }

    /**
     * When an instance is ranked, it is necessary to remove it from the neighbors list of its associates and from the
     * associates list of its neighbors. Not doing so would cause a future weight reassignment and therefore a new rank
     * could be assigned to the instance.
     * @param inst The instance for which we want to cleat the traces.
     */
    void clearInstTraces(Instance inst) {
        // removes the instance from the associates list of its neighbors
        for (Instance neighbor : inst.getNeighbors())
            neighbor.removeAssociate(inst);

        // removes the instance from the neighbors list of its associates
        for (Instance associate : inst.getAssociates())
            associate.removeNeighbor(inst);
    }
}
