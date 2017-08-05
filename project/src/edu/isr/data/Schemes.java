package edu.isr.data;

/**
 * Contains the weighting schemes. Each scheme defines the importance of each instance based on a distance notion.
 */
class Schemes {
    /**
     * Find the weight of a specific instance.
     * @param inst The instance to be weighted.
     * @param params Experiment parameters.
     * @param scheme Weighting scheme. If the parameter "scheme" in the parameter file is "remoteness-x" or
     *               "remoteness-xy", this method will be alternately called with the corresponding proximity and
     *               surrounding schemes.
     * @return The weight.
     */
    static double findWeight(Instance inst, ParametersManager params, String scheme) {
        assert scheme.equals("proximity-x") || scheme.equals("proximity-xy") ||
                scheme.equals("surrounding-x") || scheme.equals("surrounding-xy") :
                "invalid weighting scheme.";

        /* It would be possible to find the values of the variables u, v and d by creating an array "distance to
        neighbors" as a field of the "Instance" class. We want to avoid bugs by not doing so. */

        double[] p = null;
        int numDim = 0;

        // set u and d according to the weighting scheme
        switch (scheme) {
            case "proximity-x":
            case "surrounding-x":
                p = inst.getInput();
                numDim = inst.getInput().length;
                break;
            case "proximity-xy":
            case "surrounding-xy":
                p = inst.getAllAttrs();
                numDim = inst.getAllAttrs().length;
                break;
        }

        double weight = 0;

        // find the weight according to the weighting scheme
        switch (scheme) {
            case "proximity-x":
            case "proximity-xy":
                weight = proximity(inst, scheme, params.getDistMetric(), p, numDim);
                break;
            case "surrounding-x":
            case "surrounding-xy":
                weight = surrounding(inst, scheme, params.getDistMetric(), p, numDim);
                break;
        }

        return weight;
    }

    /**
     * Weigh an instance based on the proximity to the k nearest neighbors in the input or in the input-output space,
     * defined as the average distance from the instance to its k nearest neighbors.
     * @param inst The instance to be weighted.
     * @param scheme Weighting scheme. If the parameter "scheme" in the parameter file is "remoteness-x" or
     *               "remoteness-xy", this method will be alternately called with the corresponding proximity and
     *               surrounding schemes.
     * @param distMetric Parameter of the parameterized Minkowski metric. For example, z = 1 means Manhattan distance
     *                   and z = 2 means Euclidean distance. If z &lt; 1, then we have the so called fractional distance.
     * @param p Source vector (corresponding to the instance for which we want to find the weights).
     * @param numDim Number of dimensions (input or input + output, depending on the weighting scheme).
     * @return The weight based on the proximity scheme.
     */
    private static double proximity(Instance inst, String scheme, double distMetric, double[] p, int numDim) {
        double sum = 0;

        // sum up the distances from the instance to its k nearest neighbors
        for (Instance neighbor : inst.getNeighbors()) {
            // end of the vector connecting the current neighbor to the instance for which we want to find the weight
            double[] q = getNeighborPos(neighbor, scheme);

            sum += Utils.measureDistance(p, q, numDim, distMetric);
        }

        /* It is not necessary to divide the sum of the distances by the number of neighbors, since the weights will be
        normalized. */

        return sum;
    }

    /**
     * Weigh an instance based on the surrounding by the k nearest neighbors in the input or in the input-output space,
     * defined as the length of the average of the vectors pointing at the nearest neighbors.
     * @param inst The instance to be weighted.
     * @param scheme Weighting scheme. If the parameter "scheme" in the parameter file is "remoteness-x" or
     *               "remoteness-xy", this method will be alternately called with the corresponding proximity and
     *               surrounding schemes.
     * @param distMetric Parameter of the parameterized Minkowski metric. For example, z = 1 means Manhattan distance
     *                   and z = 2 means Euclidean distance. If z &lt; 1, then we have the so called fractional distance.
     * @param p Origin of the vectors (corresponding to the instance for which we want to find the weights).
     * @param numDim Number of dimensions (input or input + output, depending on the weighting scheme).
     * @return The weight based on the surrounding scheme.
     */
    private static double surrounding(Instance inst, String scheme, double distMetric, double[] p, int numDim) {
        double[] resultant = new double[numDim];

        for (Instance neighbor : inst.getNeighbors()) {
            // end of the vector connecting the current neighbor to the instance for which we want to find the weight
            double[] q = getNeighborPos(neighbor, scheme);

            for (int i = 0; i < numDim; i++) // for each component
                resultant[i] += p[i] - q[i];
        }

        /* It is not necessary to divide the components of the resultant vector by the number of neighbors, since the
        weights will be normalized. */

        double[] origin = new double[numDim]; // origin of the Cartesian coordinate system

        // the weight is equals to the size of the resultant vector
        return Utils.measureDistance(origin, resultant, numDim, distMetric);
    }

    /**
     * Get the neighbor position. The number of dimensions is based on the weighting scheme.
     * @param neighbor Current neighbor of the instance of which we want to weigh.
     * @param scheme Weighting scheme.
     * @return The neighbor vector.
     */
    private static double[] getNeighborPos(Instance neighbor, String scheme) {
        double[] pos = null;

        switch (scheme) {
            case "proximity-x":
            case "surrounding-x":
                pos = neighbor.getInput();
                break;
            case "proximity-xy":
            case "surrounding-xy":
                pos = neighbor.getAllAttrs();
                break;
        }

        return pos;
    }
}
