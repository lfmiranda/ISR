package edu.isr.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Responsible for handling the output operations necessary for running the experiment.
 */
public class OutputHandler {
    /**
     * Prints all loaded parameters.
     * @param params Experiment parameters.
     */
    public static void printLoadedParameters(ParametersManager params) {
        System.out.println("Loaded parameters:");

        System.out.println("  Path to original folds: " + params.getOrigFoldsPath());
        System.out.println("  Input path: " + params.getFoldsPath());
        System.out.println("  Output path: " + params.getOutPath());
        System.out.println("  Dataset name: " + params.getDatasetName());
        System.out.println("  Weighting scheme: " + params.getWeightingFunction());
        System.out.println("  Selection level: " + params.getSelectionLevel());
        System.out.println("  Distance metric: " + params.getDistMetric());
        System.out.println("  Number of neighbors: " + params.getNumNeighbors());
        System.out.println("  Combination method: " + params.getCombMethod() + "\n");
    }

    /**
     * Create a file registering all parameters loaded.
     * @param expId Identifier based on the names of the weighting function, neighborhood size, and the distance metric.
     * @param params Experiment parameters.
     * @throws IOException If some error occurs while creating the log file.
     */
    public static void logLoadedParameters(String expId, ParametersManager params) throws IOException {
        Path outPath = Paths.get(params.getOutPath() + expId + "/logs/" + params.getSelectionLevel());

        try {
            // creates the folder where the logs should be written if it does not exist yet
            if (!Files.exists(outPath))
                Files.createDirectories(outPath);
        } catch (IOException e) {
            throw new IOException("Error while creating the folder where the logs be written.");
        }

        String fileName = outPath + "/" + params.getDatasetName() + ".txt";

        try (PrintWriter out = new PrintWriter(fileName, "UTF-8")) {
            out.print(params.getLoadedParametersLog());
        } catch (IOException e) {
            throw new IOException("Error while writing the parameter log.");
        }
    }

    /**
     * Writes a set of weight values in a file.
     * @param weights Array with the weight values of all instances.
     * @param expId Identifier based on the names of the weighting function, neighborhood size, and the distance metric.
     * @param params Experiment parameters.
     * @throws IOException If some error occurs while creating the file containing the weight values.
     */
    static void writeWeights(double[] weights, String expId, ParametersManager params, int foldId) throws IOException {
        Path outPath = Paths.get(params.getOutPath() + expId + "/weights");

        try {
            // creates the folder where the weights file should be written if it does not exist yet
            if (!Files.exists(outPath))
                Files.createDirectories(outPath);
        } catch (IOException e) {
            throw new IOException("Error while creating the folder where the weights should be written.");
        }

        String fileName = outPath + "/" + params.getDatasetName() + "-" + foldId + ".csv";

        try (PrintWriter out = new PrintWriter(fileName, "UTF-8")) {
            // writes the weight values
            for (double weightValue : weights)
                out.println(weightValue);
        } catch (IOException e) {
            throw new IOException("Error while writing the weights.");
        }
    }

    /**
     * Write the set of selected instances in the output file.
     * @param selectedInst Array with the selected instances.
     * @param expId Identifier based on the names of the weighting function, neighborhood size, and the distance metric.
     * @param params Experiment parameters.
     * @throws IOException If the output file was not found or could not be written.
     */
    static void writeInstances(Instance[] selectedInst, String expId, ParametersManager params, int foldId)
            throws IOException {
        Path outPath = Paths.get(params.getOutPath() + expId + "/s" + params.getSelectionLevel());

        try {
            // creates the folder where the weights file should be written if it does not exist yet
            if (!Files.exists(outPath))
                Files.createDirectories(outPath);
        } catch (IOException e) {
            throw new IOException("Error while creating the folder where the instances should be written.");
        }

        String fileName = outPath + "/" + params.getDatasetName() + "-" + foldId + ".csv";

        try (PrintWriter out = new PrintWriter(fileName, "UTF-8")) {
            // writes the weight values
            for (Instance inst : selectedInst) {
                for (Double inputAttrValue : inst.getInput()) {
                    out.print(inputAttrValue + ",");
                }

                out.println(inst.getOutput());
            }
        } catch (IOException e) {
            throw new IOException("Error while writing the weights.");
        }
    }
}
