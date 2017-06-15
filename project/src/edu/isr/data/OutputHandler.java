package edu.isr.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Responsible for handling most of the output operations necessary for running the experiment.
 */
public class OutputHandler {
    /**
     * Create a new {@code OutputHandler} and the folder where all output files will be saved.
     * @param params Experiment parameters.
     * @throws IOException If some error occurs while creating the output folder.
     */
    public OutputHandler(ParametersManager params) throws IOException {
        Path path = Paths.get(params.getOutPath());

        // if the output folder does not exists, create it
        if (!Files.exists(path)) try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new IOException("Error while creating the output folder.");
        }
    }

    /**
     * Create a file registering all parameters loaded.
     * @param params Experiment parameters.
     * @throws IOException If some error occurs while creating the log file.
     */
    public void writeLoadedParametersLog(ParametersManager params) throws IOException {
        String path = params.getOutPath() + "loaded_parameters-" + params.getDatasetName() + ".txt";

        try (PrintWriter out = new PrintWriter(path, "UTF-8")) {
            out.print(params.getLoadedParametersLog());
        } catch (IOException e) {
            throw new IOException("Error while writing the parameter log.");
        }
    }
}
