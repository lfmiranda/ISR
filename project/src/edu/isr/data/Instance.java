package edu.isr.data;

/**
 * Contains data related to each of the instances.
 */
public class Instance {
    private final double[] input;
    private final double output;

    /**
     * Create a new {@code Instance}.
     * @param input Input attributes.
     * @param output Output attribute.
     */
    public Instance(double[] input, double output) {
        this.input = input;
        this.output = output;
    }

    /**
     * Return a set of values representing the input attributes of the instance.
     * @return The input attributes.
     */
    public double[] getInput() {
        return input;
    }

    /**
     * Return a value representing the output attribute of the instance.
     * @return The output attribute.
     */
    public double getOutput() {
        return output;
    }
}
