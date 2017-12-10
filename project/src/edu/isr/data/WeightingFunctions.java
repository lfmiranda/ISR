package edu.isr.data;

import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

/**
 * Contains the weighting functions. Each function defines the importance of each instance based on a distance notion.
 */
class WeightingFunctions {
    /**
     * Weights an instance using one of the weighting functions (it does not change the instance's weight value yet.
     * Because of the way the remoteness weight is calculated, that has to be done latter).
     * @param inst Instance to be weighted.
     * @param functionName Weighting function name.
     * @param distMetric Parameter of the parameterized Minkowski metric. For example, {@code distMetric} = 1 means
     *                   Manhattan distance and {@code distMetric} = 2 means Euclidean distance.
     * @return The weight value.
     */
    static double applyWeightingFunction(Instance inst, String functionName, double distMetric) {
        switch (functionName) {
        case "proximity-x":
            return getProximityWeight(inst, distMetric, false);
        case "proximity-xy":
            return getProximityWeight(inst, distMetric, true);
        case "surrounding-x":
            return getSurroundingWeight(inst, distMetric, false);
        case "surrounding-xy":
            return getSurroundingWeight(inst, distMetric, true);
        case "nonlinearity":
            return getNonLinearityWeight(inst);
        default:
            System.out.println("Invalid weighting function");
            return 0.0;
        }
    }

    /**
     * Weighs an instance by measuring the average distance to its k nearest neighbors (in the input or in the
     * input-output space).
     * @param inst Instance to be weighted.
     * @param distMetric Parameter of the parameterized Minkowski metric. For example, {@code distMetric} = 1 means
     *                   Manhattan distance and {@code distMetric} = 2 means Euclidean distance.
     * @param includeOutput Flag indicating if the output attribute should be included in the weight calculation.
     * @return The weight based on the proximity function.
     */
    private static double getProximityWeight(Instance inst, double distMetric, boolean includeOutput) {
        double[] instCoordinates = includeOutput ? inst.getAllAttrs() : inst.getInput();
        int numDimensions = instCoordinates.length;

        double weight = 0;

        // sums up the distances from the instance to its k nearest neighbors
        for (Instance neighbor : inst.getNeighbors()) {
            double[] neighborCoordinates = includeOutput ? neighbor.getAllAttrs() : neighbor.getInput();

            weight += Utils.measureDist(instCoordinates, neighborCoordinates, numDimensions, distMetric);
        }

        return weight / inst.getNeighbors().size();
    }

    /**
     * Weighs an instance by measuring the average length of the vectors pointing from the instance to its k nearest
     * neighbors (in the input or in the input-output space).
     * @param inst Instance to be weighted.
     * @param distMetric Parameter of the parameterized Minkowski metric. For example, {@code distMetric} = 1 means
     *                   Manhattan distance and {@code distMetric} = 2 means Euclidean distance.
     * @param includeOutput Flag indicating if the output attribute should be included in the weight calculation.
     * @return The weight based on the surrounding function.
     */
    private static double getSurroundingWeight(Instance inst, double distMetric, boolean includeOutput) {
        double[] instCoordinates = includeOutput ? inst.getAllAttrs() : inst.getInput();
        int numDimensions = instCoordinates.length;

        double[] resultant = new double[numDimensions];

        for (Instance neighbor : inst.getNeighbors()) {
            double[] neighborCoordinates = includeOutput ? neighbor.getAllAttrs() : neighbor.getInput();

            // sums up the distance between the instance and its neighbor regarding one specific coordinate
            for (int i = 0; i < numDimensions; i++)
                resultant[i] += instCoordinates[i] - neighborCoordinates[i];
        }


        double weight = Utils.measureDist(resultant, numDimensions, distMetric); // length of the resultant vector
        return weight / inst.getNeighbors().size();
    }

    /**
     * Weighs an instance by measuring its distance from a least-squares hyperplane passing through its k nearest
     * neighbors (always using Euclidean distance and including the output attribute in the weight calculation).
     * @param inst Instance to be weighted.
     * @return The weight based on the non-linearity function.
     */
    private static double getNonLinearityWeight(Instance inst) {
        int numNeighbors = inst.getNeighbors().size();

        double[][] xValues = new double[numNeighbors][];
        double[] yValues = new double[numNeighbors];

        // gets the position of each neighbor
        for (int i = 0; i < numNeighbors; i++) {
            Instance neighbor = inst.getNeighbors().get(i);
            xValues[i] = neighbor.getInput();
            yValues[i] = neighbor.getOutput();
        }

        OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.newSampleData(yValues, xValues); // performs the multiple linear regression

        int numDimensions = inst.getAllAttrs().length;
        double[] hyperPlane = new double[numDimensions + 1];

        double[] hyperPlaneParams;
        try {
            hyperPlaneParams = regression.estimateRegressionParameters();
        } catch (SingularMatrixException e) {
            /* When this exception occurs, there are multiple planes that can pass across all the points. In the
            specific context of the nonlinearity weighting function, a singular matrix exception indicates that the
            instance and all its neighbors are aligned with each other. In other words, with a single straight line, we
            can connect all of them. This situation implies that the distance between the instance and the plane (any of them)
            that pass through it and its neighbors is zero. Therefore, its weight is also 0. */
            return 0;
        }

        System.arraycopy(hyperPlaneParams, 1, hyperPlane, 0, numDimensions - 1);
        hyperPlane[numDimensions - 1] = -1;
        hyperPlane[numDimensions] = hyperPlaneParams[0];

        double[] instCoordinates = inst.getAllAttrs();

        double num = 0;
        double den = 0;
        for (int i = 0; i < numDimensions; i++) {
            num += hyperPlane[i] * instCoordinates[i];
            den += Math.pow(hyperPlane[i], 2);
        }

        num += hyperPlane[numDimensions];
        den = Math.sqrt(den);

        return Math.abs(num / den);
    }
}
