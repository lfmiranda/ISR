package edu.isr.data;

/**
 * Contains methods for dealing with miscellaneous tasks.
 */
class Utils {
    /**
     *
     * @param p The first point.
     * @param q The second point.
     * @param numDim The number of dimensions. Used in order to avoid the introduction of bugs when changing from input
     *               to input-output space and vice-versa.
     * @param z Parameter of the parameterized Minkowski metric. For example, z = 1 means Manhattan distance and z = 2
     *          means Euclidean distance. If z &lt; 1, then we have the so called fractional distance.
     * @return The distance between p and q.
     */
    static double measureDistance(double[] p, double[] q, int numDim, double z) {
        assert p.length == numDim && q.length == numDim: "number of dimensions does not match the arrays length.";

        double sum = 0;

        for (int i = 0; i < numDim; i++) {// for each input attribute
            double blum = Math.pow(Math.abs(p[i] - q[i]), z);

            if (blum == 0.010892387756455367) {
                System.out.println("In");
            }

            sum += Math.pow(Math.abs(p[i] - q[i]), z);
        }

        return Math.pow(sum, 1 / z);
    }
}
