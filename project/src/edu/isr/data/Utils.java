package edu.isr.data;

/**
 * Contains methods for dealing with miscellaneous tasks.
 */
class Utils {
    /**
     * Measures the Euclidean distance between two arrays.
     * @param u The first array.
     * @param v The second array.
     * @param d The number of dimensions. Used in order to avoid the introduction of bugs when changing from input to
     *          input-output space and vice-versa.
     * @return The Euclidean distance.
     */
    static double measureEuclideanDistance(double[] u, double[] v, int d) {
        assert u.length == d && v.length == d: "number of dimensions does not match the arrays length.";

        double sum = 0;

        for (int i = 0; i < d; i++) { // for each input attribute
            sum += Math.pow(Math.abs(u[i] - v[i]), 2);
        }

        return Math.sqrt(sum);
    }

    /**
     * Measures the fractional distance between two arrays.
     * @param u The first array.
     * @param v The second array.
     * @param d The number of dimensions. Used in order to avoid the introduction of bugs when changing from input to
     *          input-output space and vice-versa.
     * @return The fractional distance.
     */
    static double measureFractionalDistance(double[] u, double[] v, int d) {
        assert u.length == d && v.length == d: "number of dimensions does not match the arrays length.";

        double sum = 0;

        for (int i = 0; i < d; i++) {
            sum += Math.pow(Math.abs(u[i] - v[i]), d);
        }

        return Math.pow(sum, 1 / d);
    }
}
