package edu.isr.data;

/**
 * Contains methods for dealing with miscellaneous tasks.
 */
class Utils {
    /**
     * Measures the distance between a specific point and the origin of the Cartesian coordinate system.
     * @param coordinates Set of coordinates indicating the position of the point.
     * @param numDimensions Number of dimensions of the space.
     * @param distMetric Parameter of the parameterized Minkowski metric. For example, {@code distMetric} = 1 means
     *                   Manhattan distance and {@code distMetric} = 2 means Euclidean distance.
     * @return The length of the vector linking a specific point to the origin of the Cartesian coordinate system.
     */
    static double measureDist(double[] coordinates, int numDimensions, double distMetric) {
        assert coordinates.length == numDimensions : "number of dimensions does not match the number of coordinates.";

        double sum = 0;
        for (double coordinate : coordinates)
            sum += Math.pow(coordinate, distMetric);

        return Math.pow(sum, 1 / distMetric);
    }

    /**
     * Measures the distance between two points.
     * @param coordinatesP1 Set of coordinates indicating the position of the first point.
     * @param coordinatesP2 Set of coordinates indicating the position of the second point.
     * @param numDimensions Number of dimensions of the space.
     * @param distMetric Parameter of the parameterized Minkowski metric. For example, {@code distMetric} = 1 means
     *                   Manhattan distance and {@code distMetric} = 2 means Euclidean distance.
     * @return The length of the vector linking the two points.
     */
    static double measureDist(double[] coordinatesP1, double[] coordinatesP2, int numDimensions, double distMetric) {
        assert (coordinatesP1.length == numDimensions) && (coordinatesP2.length == numDimensions) :
                "number of dimensions does not match the number of coordinates.";

        double sum = 0;
        for (int i = 0; i < numDimensions; i++)
            sum += Math.pow(coordinatesP1[i] - coordinatesP2[i], distMetric);

        return Math.pow(sum, 1 / distMetric);
    }
}
