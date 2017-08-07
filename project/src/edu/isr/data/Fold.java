package edu.isr.data;

import com.google.common.collect.TreeMultimap;

import java.io.IOException;
import java.util.*;

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
     * Normalize all the attributes to similar ranges in order to ensure that no unadapted data ranges bias the weights.
     */
    public void normalizeData() {
        for (int i = 0; i < numAttr; i++) { // for each attribute (input and output)
            double[] allValuesSingleAttr = new double[numInst];
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;

            // find the minimum and maximum value for the current attribute
            for (int j = 0; j < numInst; j++) {
                double value = instances.get(j).getAllAttrs()[i];

                allValuesSingleAttr[j] = value;
                if (value < min) min = value;
                if (value > max) max = value;
            }

            double range = Math.abs(max - min);

            // normalize the attribute values
            for (int j = 0; j < numInst; j++) {
                instances.get(j).setAttrValue(i, numAttr, (allValuesSingleAttr[j] - min) / range);
            }
        }
    }

    /**
     * Assign ranks to the instances in the fold. The position of each instance in the ranking depends on its weight,
     * which are assigned and updated to it during the iterations of the procedure.
     * @param params Experiment parameters.
     * @throws IOException If some error occurs while creating the file containing the weights.
     */
    public void assignRanks(ParametersManager params) throws IOException {
        measureDistBetweenInst(params.getDistMetric()); // measure the distance between each pair of instances

        double[] weights = new double[numInst];

        String scheme = params.getScheme();
        if (! (scheme.equals("remoteness-x") || (scheme.equals("remoteness-xy")))) {
            weights = weighInstances(params, scheme);
        } else { // remoteness-x or remoteness-xy
            double[] proximityWeights;
            double[] surroundingWeights;

            if (scheme.equals("remoteness-x")) {
                proximityWeights = weighInstances(params, "proximity-x");
                surroundingWeights = weighInstances(params, "surrounding-x");
            } else { //remoteness-xy
                proximityWeights = weighInstances(params, "proximity-xy");
                surroundingWeights = weighInstances(params, "surrounding-xy");
            }

            // determine the remoteness weight
            for (int i = 0; i < numInst; i++)
                weights[i] = (proximityWeights[i] + surroundingWeights[i]) / 2;
        }

        for (int i = 0; i < numInst; i++)
            instances.get(i).setWeight(weights[i]);

        // the normalized weights should add up to 1. This piece of code checks that.
        double sumWeights = 0;
        for (int i = 0; i < numInst; i++)
            sumWeights += weights[i];
        assert Math.round(sumWeights) == 1 : "the normalized weights should add up to 1.";

        OutputHandler.writeWeights(foldName, instances, params); // write the weights in a file
    }

    /**
     * Measure the distance between each pair of instances, always based on the input space.
     * @param distMetric Metric used in order to calculate distances between instances.
     */
    private void measureDistBetweenInst(double distMetric) {
        distBetweenInst = new double[numInst][numInst];

        for (int i = 1; i < numInst; i++) {
            for (int j = 0; j < i; j++) {
                double[] u = instances.get(i).getInput();
                double[] v = instances.get(j).getInput();
                int d = numAttr - 1;

                distBetweenInst[i][j] = Utils.measureDistance(u, v, d, distMetric);
                distBetweenInst[j][i] = distBetweenInst[i][j];
            }
        }
    }

    /**
     * Get the weights of the entire fold.
     * @param params Experiment parameters.
     * @param scheme Weighting scheme. If the parameter "scheme" in the parameter file is "remoteness-x" or
     *               "remoteness-xy", this method will be alternately called with the corresponding proximity and
     *               surrounding schemes.
     * @return An array with all the weights.
     */
    private double[] weighInstances(ParametersManager params, String scheme) {
        double lowestWeight = Double.POSITIVE_INFINITY;
        double highestWeight = 0;

        double[] weights = new double[numInst];
        double sumWeights = 0;

        for (Instance inst : instances) {
            /* Distances (considering only the input space) between the instance for which we want to weigh and all
            other instances */
            double[] distOtherInst = distBetweenInst[inst.getId()];

            /* Map, for the current instance, the distance to the other instances and the id corresponding to each one
            of them. The entries are sorted by their distance value (and duplicates are allowed). */
            TreeMultimap<Double, Integer> sortedDistances = TreeMultimap.create();

            // fill the map
            for (int i = 0; i < numInst; i++)
                sortedDistances.put(distOtherInst[i], i);

            double weight = weighInstance(inst, params, scheme, sortedDistances); // weigh the current instance
            inst.setWeight(weight);

            sumWeights += weight;
        }

        // normalize the weights
        for (Instance inst : instances)
            weights[inst.getId()] = inst.getWeight() / sumWeights;

        return weights;
    }

    /**
     * Weigh an specific instance.
     * @param inst The instance to be weighted.
     * @param params Experiment parameters.
     * @param scheme Weighting scheme. If the parameter "scheme" in the parameter file is "remoteness-x" or
     *               "remoteness-xy", this method will be alternately called with the corresponding proximity and
     *               surrounding schemes.
     * @param sortedDistances Map containing, for the current instance, the distance to the other instances and the id
     *                        corresponding to each one of them. The entries are sorted by their distance value (and
     *                        duplicates are allowed).
     * @return The weight.
     */
    private double weighInstance(Instance inst, ParametersManager params, String scheme,
                                 TreeMultimap<Double, Integer> sortedDistances) {
        // find the instance neighbors (using distances based only on the input space)
        findNeighbors(inst.getId(), params.getNumNeighbors(), sortedDistances);

        return Schemes.findWeight(inst, params, scheme); // find and return the weight for the instance.
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
