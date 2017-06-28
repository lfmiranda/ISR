package edu.isr.data;

import com.google.common.collect.TreeMultimap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Represents a set of instances. Each dataset may be represent by one or more folds.
 */
public class Fold {
    private final ArrayList<Instance> instances;

    private int numInst;
    private int numAttr;

    private final String foldName;
    private double[][] distBetweenInst; // distances between the instances in the input space

    /**
     * Create an empty {@code Fold}.
     * @param foldName Full name of the folder, including its index and extension.
     */
    Fold(String foldName) {
        instances = new ArrayList<>();
        this.foldName = foldName;
    }

    /**
     * Assign ranks to the instances in the fold. The position of each instance in the ranking depends on its weight,
     * which are assigned and updated to it during the iterations of the procedure.
     * @param params Experiment parameters.
     * @throws IOException If some error occurs while creating the file containing the weights.
     */
    public void assignRanks(ParametersManager params) throws IOException {
        measureDistBetweenInst(params.getDistMetric()); // measure the distance between each pair of instances
        weighInstances(params); // weigh the entire fold
        OutputHandler.writeWeights(foldName, instances, params); // write the weights in a file
    }

    /**
     * Measure the distance between each pair of instances.
     * @param distMetric Metric used in order to calculate distances between instances.
     */
    private void measureDistBetweenInst(String distMetric) {
        assert distMetric.equals("euclidean") || distMetric.equals("fractional") : "invalid distance metric.";

        // the distances between the instances will always be based on the input space
        distBetweenInst = new double[numInst][numInst];

        for (int i = 1; i < numInst; i++) {
            for (int j = 0; j < i; j++) {
                double[] u = instances.get(i).getInput();
                double[] v = instances.get(j).getInput();
                int d = numAttr - 1;

                switch (distMetric) {
                    case "euclidean":
                        distBetweenInst[i][j] = Utils.measureEuclideanDistance(u, v, d);
                        break;
                    case "fractional":
                        distBetweenInst[i][j] = Utils.measureFractionalDistance(u, v, d);
                        break;
                }

                distBetweenInst[j][i] = distBetweenInst[i][j];
            }
        }
    }

    /**
     * Weigh the entire fold.
     * @param params Experiment parameters.
     */
    private void weighInstances(ParametersManager params) {
        for (Instance inst : instances) {
            /* Distances (considering only the input space) between the instance for which we want to weigh and all
            other instances */
            double[] distOtherInst = distBetweenInst[inst.getId()];

            /* Map, for the current instance, the distance to the other instances and the id corresponding to each one
            of them. The entries are sorted by their distance value (and duplicates are allowed). */
            TreeMultimap<Double, Integer> sortedDistances = TreeMultimap.create();

            // fill the map
            for (int i = 0; i < numInst; i++) {
                sortedDistances.put(distOtherInst[i], i);
            }

            weighInstance(inst, params, sortedDistances); // weigh the current instance
        }
    }

    /**
     * Weigh an specific instance.
     * @param inst The instance to be weighted.
     * @param params Experiment parameters.
     * @param sortedDistances Map containing, for the current instance, the distance to the other instances and the id
     *                        corresponding to each one of them. The entries are sorted by their distance value (and
     *                        duplicates are allowed).
     */
    private void weighInstance(Instance inst, ParametersManager params, TreeMultimap<Double, Integer> sortedDistances) {
        String scheme = params.getScheme();
        assert scheme.equals("proximity-x") || scheme.equals("proximity-xy") : "invalid weighting scheme.";

        // find the instance neighbors (using distances based only on the input space)
        findNeighbors(inst.getId(), params.getNumNeighbors(), sortedDistances);

        inst.setWeight(Schemes.proximity(inst, params)); // find and set the weight for the instance.
    }

    /**
     * Finds the set of neighbors of an instance and registers their distances.
     * @param instId Id of the instance for which we want to find the neighbors.
     * @param numNeighbors Number of instances taken as neighbors.
     * @param sortedDistances Map containing, for the current instance, the distance to the other instances and the id
     *                        corresponding to each one of them. The entries are sorted by their distance value (and
     *                        duplicates are allowed).
     */
    private void findNeighbors(int instId, int numNeighbors, TreeMultimap<Double, Integer> sortedDistances) {
        /* Set of entries ["distance to instance", "instance id"], sorted by the distance. The distances are not
        unique. */
        Set<Map.Entry<Double, Integer>> entries = sortedDistances.entries();

        final Iterator<Map.Entry<Double, Integer>> iterator = entries.iterator();

        for (int i = 0; i < numNeighbors; i++) {
            Map.Entry<Double, Integer> nextNeighbor = iterator.next(); // distance and id of the next neighbor

            int neighborId = nextNeighbor.getValue();

            /* Ignore the instance itself. In theory, checking for a distance value equals to zero should suffice, but
            in practice there may exist instances with same input attributes values and therefore a distance value which
            is also equals to zero. */
            if (neighborId == instId) {
                i--;
            // register the next instance in set as one of the neighbors of the current instance.
            } else {
                instances.get(instId).addNeighbor(instances.get(neighborId));
            }
        }
    }

    /**
     * Add a new instance to the fold.
     * @param inst The instance to be added.
     */
    void addInst(Instance inst) {
        instances.add(inst);
        numInst++;
    }

    /**
     * Set the total number of instances.
     * @param numInst The number of instances.
     */
    void setNumInst(int numInst) {
        this.numInst = numInst;
    }

    /**
     * Set the number of attributes (input + output) of the instances (any of them).
     * @param numAttr The number of attributes.
     */
    void setNumAttr(int numAttr) {
        this.numAttr = numAttr;
    }
}
