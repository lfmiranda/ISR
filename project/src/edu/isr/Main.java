package edu.isr;

class Main {
    /**
     * Start the experiment and keep track of its execution time.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        try {
            // parse the command line and set all the parameters necessary for running the experiment
            new ExperimentManager(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Elapsed time: " + ((endTime - startTime) / 1000) + " seconds.");
    }
}
