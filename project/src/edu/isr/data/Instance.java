package edu.isr.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains data related to each instance.
 */
class Instance {
    private final int id;

    private final double[] allAttrs;
    private final double[] input;
    private final double output;

    private final List<Instance> neighbors  = new ArrayList<>();
    private final List<Instance> associates = new ArrayList<>();

    private double weight;
    private int rank;

    /**
     * Creates a new {@code Instance}.
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
    }

    /**
     * Register an instance as one of the neighbors of a specific instance.
     * @param neighbor The neighbor to be added.
     */
    void addNeighbor(Instance neighbor) {
        neighbors.add(neighbor);
    }

    /**
     * Register an instance as one of the associates of a specific instance.
     * @param associate The associate to be added.
     */
    void addAssociate(Instance associate) {
        associates.add(associate);
    }

    /**
     * Returns an unique integer identifying each instance.
     * @return The instance id.
     */
    int getId() {
        return id;
    }

    /**
     * Returns a set of values representing all attributes of the instance.
     * @return The instance attributes.
     */
    double[] getAllAttrs() {
        return allAttrs;
    }

    /**
     * Returns a set of values representing the input attributes of the instance.
     * @return The input attributes.
     */
    double[] getInput() {
        return input;
    }

    /**
     * Returns a value representing the output attribute of the instance.
     * @return The output attribute.
     */
    double getOutput() {
        return output;
    }

    /**
     * Returns a list of instances representing this instance neighbors.
     * @return The instance neighbors.
     */
    List<Instance> getNeighbors() {
        return neighbors;
    }

    /**
     * Returns a list of instances representing this instance associates.
     * @return The instance associates.
     */
    List<Instance> getAssociates() {
        return associates;
    }

    /**
     * Returns a number representing the instance weight.
     * @return The instance weight.
     */
    double getWeight() {
        return weight;
    }

    /**
     * Sets the instance weight.
     * @param weight The weight value.
     */
    void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Returns the rank of the instance, which represents its order of elimination.
     * @return The rank.
     */
    int getRank() {
        return rank;
    }

    /**
     * Sets the instance rank.
     * @param rank The rank value.
     */
    void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Removes an instance from the associates list of this instance
     * @param inst The instance to be removed
     */
    void removeAssociate(Instance inst) {
        associates.remove(inst);
    }

    /**
     * Removes an instance from the neighbors list of this instance
     * @param inst The instance to be removed
     */
    void removeNeighbor(Instance inst) {
        neighbors.remove(inst);
    }

    // clears neighbors list
    void clearNeighborsList() {
        neighbors.clear();
    }

    // clears associates list
    void clearAssociatesList() {
        associates.clear();
    }
}
