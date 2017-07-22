package edu.isr.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Responsible for handling most of the output operations necessary for running the experiment.
 */
public class OutputHandler {
    private static String scheme;
    private static String selectionLevel;
    private static String distMetric;

    /**
     * Create a new {@code OutputHandler} and the folder where all output files will be saved.
     * @param params Experiment parameters.
     * @throws IOException If some error occurs while creating the output folder.
     */
    public OutputHandler(ParametersManager params) throws IOException {
        scheme = params.getScheme();
        selectionLevel = "s" + params.getSelectionLevel();
        distMetric = Double.toString(params.getDistMetric());

        Path experimentPath = Paths.get(params.getOutPath() + String.join("/", selectionLevel, scheme, distMetric, ""));
        Path weightsPath = Paths.get(params.getOutPath() + "weights/");

        try {
            // if any of the output folders do not exist, create it
            if (!Files.exists(experimentPath) && (!params.getSelectionLevel().equals("no_selection"))) {
                Files.createDirectories(experimentPath);
            }
            if (!Files.exists(weightsPath)) Files.createDirectories(weightsPath);
        } catch (IOException e) {
            throw new IOException("Error while creating the output folder.");
        }
    }

    /**
     * Create a file registering all parameters loaded.
     * @param params Experiment parameters.
     * @throws IOException If some error occurs while creating the log file.
     */
    public static void writeLoadedParametersLog(ParametersManager params) throws IOException {
        String fileName = params.getOutPath() + String.join("/", selectionLevel, scheme, distMetric, "") +
                "loaded_parameters-" + params.getDatasetName() + ".txt";

        try (PrintWriter out = new PrintWriter(fileName, "UTF-8")) {
            out.print(params.getLoadedParametersLog());
        } catch (IOException e) {
            throw new IOException("Error while writing the parameter log.");
        }
    }

    /**
     * Create a file registering the weights of all instances in a specific fold.
     * @param foldName Full name of the folder, including its index and extension.
     * @param instances List containing all the instances of the fold.
     * @param params Experiment parameters.
     * @throws IOException If some error occurs while creating the file.
     */
    static void writeWeights(String foldName, List<Instance> instances, ParametersManager params) throws IOException {
        String fileName = params.getOutPath() + "weights/" +
                String.join("-", params.getScheme(), "L" + Double.toString(params.getDistMetric()), foldName);

        try (PrintWriter out = new PrintWriter(fileName, "UTF-8")) {
            for (Instance inst : instances) {
                out.println(inst.getWeight());
            }
            out.println();
        } catch (IOException e) {
            throw new IOException("Error while writing the weights.");
        }
    }
}
