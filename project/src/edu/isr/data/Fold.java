package edu.isr.data;

import java.util.ArrayList;

/**
 * Represents a set of instances. Each dataset may be represent by one or more folds.
 */
public class Fold {
    private final ArrayList<Instance> instances;

    private int numInst;
    private int numAttr;

    private final String foldName;

    /**
     * Create an empty {@code Fold}.
     */
    Fold(String foldName) {
        instances = new ArrayList<>();
        this.foldName = foldName;
    }

    /**
     * Add a new instance to the fold.
     * @param inst The instance to be added.
     */
    void addInst(Instance inst) {
        instances.add(inst);
        numInst++;
    }

    /**
     * Return an instance by its index.
     * @param index The instance index.
     * @return A specific instance of the fold.
     */
    public Instance getInst(int index) {
        return instances.get(index);
    }

    /**
     * Return the total number of instances.
     * @return The number of instances.
     */
    public int getNumInst() {
        return numInst;
    }

    /**
     * Set the total number of instances.
     * @param numInst The number of instances.
     */
    void setNumInst(int numInst) {
        this.numInst = numInst;
    }

    /**
     * Return the number of attributes (input + output) of the instances (any of them).
     * @return The number of attributes.
     */
    public int getNumAttr() {
        return numAttr;
    }

    /**
     * Set the number of attributes (input + output) of the instances (any of them).
     * @param numAttr The number of attributes.
     */
    void setNumAttr(int numAttr) {
        this.numAttr = numAttr;
    }

    /**
     * Return the name (including the extension) of the file containing the instances.
     * @return The fold name.
     */
    public String getFoldName() {
        return foldName;
    }
}
