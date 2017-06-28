package edu.isr.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Responsible for handling the input operations necessary for running the experiment.
 */
public class InputHandler {
    /**
     * Read a set of input folds.
     * @param params Experiment parameters.
     * @param readOrigFolds Flag indicating if the data should be read from the original folds or from the folds created
     *                      during the embedding generation step.
     * @return A set of folds.
     * @throws FileNotFoundException If a fold is not found or can't be read.
     */
    public static ArrayList<Fold> readFolds(ParametersManager params, boolean readOrigFolds)
            throws FileNotFoundException {
        ArrayList<Fold> folds = new ArrayList<>();

        int foldIndex = 0;
        String foldPath;

        if (readOrigFolds) foldPath = params.getOrigPath();
        else foldPath = params.getInPath();

        while (true) { // each iteration corresponds to a fold
            String foldName = params.getDatasetName() + "-" + foldIndex + ".csv";

            File inFile = new File(foldPath + foldName);
            if (!inFile.isFile()) break; // break the loop if there are no more folds to read

            try (Scanner sc = new Scanner(inFile)) {
                Fold fold = new Fold(foldName);

                int numAttr = 0;
                int id = 0;

                while (sc.hasNextLine()) { // each line corresponds to an instance
                    // split the line into a group of strings, each representing an attribute
                    String[] attrs = sc.nextLine().split(",");
                    
                    if (id == 0) numAttr = attrs.length; // find out the number of attributes

                    // copy the input attributes
                    double[] allAttrs = new double[numAttr];
                    double[] inputAttrs = new double[numAttr - 1];
                    for (int i = 0; i < numAttr - 1; i++) {
                        allAttrs[i] = Double.parseDouble(attrs[i]);
                        inputAttrs[i] = Double.parseDouble(attrs[i]);
                    }

                    // copy the output attribute
                    allAttrs[numAttr - 1] = Double.parseDouble(attrs[numAttr - 1]);
                    double outputAttr = Double.parseDouble(attrs[numAttr - 1]);

                    fold.addInst(new Instance(id, allAttrs, inputAttrs, outputAttr));
                    id++;
                }

                fold.setNumAttr(numAttr);

                /* The instance id is zero-based, but since the id variable was incremented after the addition of the
                 * last instance, its value contains the correct number of instances in the fold.
                 */
                fold.setNumInst(id);

                folds.add(fold);
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException("Fold not found: " + foldPath + foldName + ".");
            }

            foldIndex++;
        }

        return folds;
    }
}
