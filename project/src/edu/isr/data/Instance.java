package edu.isr.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains data related to each of the instances.
 */
class Instance {
    private final int id;

    private final double[] allAttrs;
    private final double[] input;
    private double output;

    private final List<Instance> neighbors;

    private double weight;

    /**
     * Create a new {@code Instance}.
     * @param id Instance unique id.
     * @param allAttrs Input and output attributes.
     * @param input Input attributes.
     * @param output Output attribute.
     */
    Instance(int id, double[] allAttrs, double[] input, double output) {
        this.id = id;
        this.allAttrs = allAttrs;
        this.input = input;
        this.output = output;
        neighbors = new ArrayList<>();
    }

    /**
     * Return a zero-base index that identifies the instance.
     * @return The instance id.
     */
    int getId() {
        return id;
    }

    /**
     * Return a set of values representing all attributes of the instance.
     * @return The instance attributes.
     */
    double[] getAllAttrs() {
        return allAttrs;
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

    /**
     * Change the value of a specific attribute.
     * @param attrId Index of the attribute that will be changed.
     * @param numAttrs Number of attributes of the instance.
     * @param value The new value for the attribute.
     */
    void setAttrValue(int attrId, int numAttrs, double value) {
        assert attrId >= 0 && attrId <= (numAttrs - 1) : "invalid attribute id.";

        allAttrs[attrId] = value;

        if (attrId == (numAttrs - 1)) output = value;
        else input[attrId] = value;
    }

    /**
     * Return a list of instances representing this instance neighbors.
     * @return The instance neighbors.
     */
    public List<Instance> getNeighbors() {
        return neighbors;
    }

    /**
     * Register an instance as one of the neighbors of the instance.
     * @param neighbor The neighbor to be added.
     */
    void addNeighbor(Instance neighbor) {
        neighbors.add(neighbor);
    }

    /**
     * Return a number representing the instance weight.
     * @return The instance weight.
     */
    double getWeight() {
        return weight;
    }

    /**
     * Set the instance weight.
     * @param weight The weight value.
     */
    void setWeight(double weight) {
        this.weight = weight;
    }
}
