package edu.isr;

public class Main {
    /**
     * Triggers the experiment and keeps track of its execution time.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        try {
            // parses the command line and sets all the parameters necessary for running the experiment
            ExperimentManager experimentManager = new ExperimentManager(args);
            experimentManager.runExperiment();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Elapsed time: " + ((endTime - startTime) / 1000) + " seconds.\n");
    }
}
