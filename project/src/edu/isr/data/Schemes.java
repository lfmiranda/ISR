package edu.isr.data;

/**
 * Contains the weighting schemes. Each scheme defines the importance of each instance based on a distance notion.
 */
class Schemes {
    /**
     * Weigh an instance based on the proximity of the k nearest neighbors in the input or in the input-output space,
     * defined as the average distance from the instance to its k nearest neighbors.
     * @param inst The instance to be weighted.
     * @param params Experiment parameters.
     * @return The weight based on the proximity scheme.
     */
    static double proximity(Instance inst, ParametersManager params) {
        String scheme = params.getScheme();
        String distMetric = params.getDistMetric();

        assert distMetric.equals("euclidean") || distMetric.equals("fractional") : "invalid distance metric.";
        assert scheme.equals("proximity-x") || scheme.equals("proximity-xy") : "invalid weighting scheme.";

        /* It would be possible to find the values of the variables u, v and d by creating an array "distance to
        neighbors" as a field of the "Instance" class. We want to avoid bugs by not doing so. */

        double[] u = null;
        int d = 0;

        switch (scheme) {
            case "proximity-x":
                u = inst.getInput();
                d = inst.getInput().length;
                break;
            case "proximity-xy":
                u = inst.getAllAttrs();
                d = inst.getAllAttrs().length;
                break;
        }

        double sum = 0;

        for (Instance neighbor : inst.getNeighbors()) {
            double[] v = null;

            switch (scheme) {
                case "proximity-x":
                    v = neighbor.getInput();
                    break;
                case "proximity-xy":
                    v = neighbor.getAllAttrs();
                    break;
            }

            switch (distMetric) {
                case "euclidean":
                    sum += Utils.measureEuclideanDistance(u, v, d);
                    break;
                case "fractional":
                    sum += Utils.measureEuclideanDistance(u, v, d);
                    break;
            }
        }

        return sum / params.getNumNeighbors();
    }
}
