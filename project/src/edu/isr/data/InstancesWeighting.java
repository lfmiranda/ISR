package edu.isr.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;

/**
 * Responsible for assigning the initial weights to the instances.
 */
public class InstancesWeighting {
    /**
     *
     * @param fold Set of instances to be weighted.
     * @param expId Identifier based on the names of the weighting function, neighborhood size, and the distance metric.
     * @param params Experiment parameters.
     * @throws IOException If some error occurs while creating the file containing the weights.
     */
    public static void rankInstances(Fold fold, String expId, ParametersManager params) throws IOException {
        fold.measureDistBetweenInst(params.getDistMetric()); // measures the distance between each pair of instances

        for (int i = 0; i < fold.getNumInst(); i++)
            fold.findNeighbors(i, params.getNumNeighbors());

        String functionName = params.getWeightingFunction();
        double[] weights;

        // weighs the entire fold
        if (functionName.equals("remoteness-x") || functionName.equals("remoteness-xy")) {
            // compound cases: two weighting functions will be used.
            weights = getCompoundWeights(fold, functionName, params.getDistMetric(), params.getCombMethod());

            // for the remoteness function, the rank value is always equal to the initial weight value
            for (int i = 0; i < fold.getNumInst(); i++)
                fold.getInst(i).setRank(weights[i]);
        } else {
            // simple cases: only one weighting function will be used.
            weights = getWeights(fold, functionName, params.getDistMetric());

            /* Uses the weights to find out the rank value of each instance and then assigns the ranks to the
            corresponding instances. */
            int[] ranks = getRanks(weights);
            for (int i = 0; i < fold.getNumInst(); i++)
                fold.getInst(i).setRank(ranks[i]);
        }

        normalizeWeights(weights);
        setWeights(fold, weights);
        OutputHandler.writeWeights(weights, expId, params, fold.getFoldId()); // saves the weights in a file
    }

    /**
     *
     * @param fold Set of instances to be weighted.
     * @param functionName Weighting function name.
     * @param distMetric Parameter of the parameterized Minkowski metric. For example, {@code distMetric} = 1 means
     *                   Manhattan distance and {@code distMetric} = 2 means Euclidean distance.
     * @return An array with the compound weights of all instances.
     */
    private static double[] getCompoundWeights(Fold fold, String functionName, double distMetric, String combMethod) {
        String proxFunction;
        String surrFunction;

        if (functionName.equals("remoteness-x")) {
            proxFunction = "proximity-x";
            surrFunction = "surrounding-x";
        } else {
            proxFunction = "proximity-xy";
            surrFunction = "surrounding-xy";
        }

        double[] proxWeights = getWeights(fold, proxFunction, distMetric);
        double[] surrWeights = getWeights(fold, surrFunction, distMetric);

        int[] proxRanks = getRanks(proxWeights);
        int[] surrRanks = getRanks(surrWeights);

        int numInst = fold.getNumInst();
        double[] weights = new double[numInst];


        if (combMethod.equals("cardinal"))
            for (int i = 0; i < numInst; i++)
                weights[i] = (proxWeights[i] + surrWeights[i]) / 2;
        else // combination method = ordinal
            for (int i = 0; i < numInst; i++)
                weights[i] = numInst - ((proxRanks[i] + surrRanks[i]) / 2);

        return weights;
    }

    /**
     *
     * @param fold Set of instances to be weighted.
     * @param functionName Weighting function name.
     * @param distMetric Parameter of the parameterized Minkowski metric. For example, {@code distMetric} = 1 means
     *                   Manhattan distance and {@code distMetric} = 2 means Euclidean distance.
     * @return An array with the weights of all instances.
     */
    private static double[] getWeights(Fold fold, String functionName, double distMetric) {
        double[] weights = new double[fold.getNumInst()];

        for (int i = 0; i < fold.getNumInst(); i++)
            weights[i] = WeightingFunctions.applyWeightingFunction(fold.getInst(i), functionName, distMetric);

        return weights;
    }

    /**
     * Creates a ranking based on an array of weight values.
     * @param weights Array with the weight values of all instances.
     * @return An array with the rank of all instances.
     */
    private static int[] getRanks(double[] weights) {
        List<Map.Entry<Integer, Double>> idsToWeights = new ArrayList<>();

        for (int i = 0; i < weights.length; i++)
            idsToWeights.add(Map.entry(i, weights[i]));

        idsToWeights.sort(Comparator.comparingDouble(Map.Entry::getValue));

        int[] ranks = new int[weights.length];

        for (Map.Entry<Integer, Double> entry : idsToWeights)
            ranks[entry.getKey()] = idsToWeights.indexOf(entry);

        return ranks;
    }

    /**
     * Sets the initial weights of all instances at once. The weight value can be modified later during the instance
     * selection process. This process was not done "on the fly" due to way the remoteness function works.
     * @param fold Set of instances to be weighted.
     * @param weights Array with the weight values of all instances.
     */
    private static void setWeights(Fold fold, double[] weights) {
        for (int i = 0; i < fold.getNumInst(); i++)
            fold.getInst(i).setWeight(weights[i]);
    }

    /**
     * Normalizes the weights. The normalized weights should add up to 1.
     * @param weights Array with the weight values of all instances.
     */
    private static void normalizeWeights(double[] weights) {
        double sumWeights = DoubleStream.of(weights).sum();

        for (int i = 0; i < weights.length; i++)
            weights[i] /= sumWeights;

        assert Math.round(DoubleStream.of(weights).sum()) == 1 : "the normalized weights should add up to 1.";
    }
}
