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
     * Reads a set of input folds.
     * @param params Experiment parameters.
     * @param foldType Flag indicating which type of fold data should be read (can be "orig" or "norm").
     * @return A set of folds.
     * @throws FileNotFoundException If a fold was not found or could not be read.
     */
    public static ArrayList<Fold> readTrFolds(ParametersManager params, String foldType)
            throws FileNotFoundException {
        ArrayList<Fold> folds = new ArrayList<>();

        int foldId = 0;
        String foldPath = null;

        switch (foldType) {
            case "orig":
                foldPath = params.getOrigFoldsPath();
                break;
            case "norm":
                foldPath = params.getNormFoldsPath();
                break;
            default:
                // foldPath = params.getFoldsPath();
                break;
        }

        while (true) { // each iteration corresponds to a fold
            String foldName = params.getDatasetName() + "-" + foldId + ".csv";

            File inFile = new File(foldPath + foldName);
            if (!inFile.isFile()) break; // breaks the loop if there are no more folds to read

            try (Scanner sc = new Scanner(inFile)) {
                Fold fold = new Fold(foldId);

                int numAttr = 0;
                int instId = 0;

                while (sc.hasNextLine()) { // each line corresponds to an instance
                    // splits the line into a group of strings, each representing an attribute
                    String[] attrs = sc.nextLine().split(",");

                    if (instId == 0) numAttr = attrs.length; // find out the number of attributes

                    // copies the input attributes
                    double[] allAttrs = new double[numAttr];
                    double[] inputAttrs = new double[numAttr - 1];
                    for (int i = 0; i < numAttr - 1; i++) {
                        allAttrs[i] = Double.parseDouble(attrs[i]);
                        inputAttrs[i] = Double.parseDouble(attrs[i]);
                    }

                    // copies the output attribute
                    allAttrs[numAttr - 1] = Double.parseDouble(attrs[numAttr - 1]);
                    double outputAttr = Double.parseDouble(attrs[numAttr - 1]);

                    fold.addInst(new Instance(instId, allAttrs, inputAttrs, outputAttr));
                    instId++;
                }

                fold.setNumAttr(numAttr);

                /* The instance id is zero-based, but since the id variable was incremented after the addition of the
                 * last instance, its value contains the correct number of instances in the fold.
                 */
                fold.setNumInst(instId);

                folds.add(fold);
            } catch (FileNotFoundException e) {
                throw new FileNotFoundException("Fold not found: " + foldPath + foldName + ".");
            }

            foldId++;
        }

        return folds;
    }
}
