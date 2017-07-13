from os import chmod, makedirs, path

# experiments variables
system_id = "6-gsgp-wf-03.07.2017"
jar_name = "GSGP-WeightedFitness.jar"
embeddings = {"original": "orig", "isomap": "imap", "mds": "mds", "pca": "pca", "tsne": "tsne"}
schemes = {"proximity-x": "pro_x", "proximity-xy": "pro_xy",
           "surrounding-x": "sur_x", "surrounding-xy": "sur_xy",
           "remoteness-x": "rem_y", "remoteness-xy": "rem_xy"}
dist_metrics = {"euclidean": "euc", "fractional": "fra"}
datasets = ["airfoil", "ccn", "ccun", "concrete", "energyCooling", "energyHeating", "keijzer-6", "keijzer-7",
            "parkinsons", "ppb", "towerData", "vladislavleva-1", "wineRed", "wineWhite", "yacht"]
exper_number = 1
exper_date = "05.07.2017"
output_number = 1

# local paths
root = path.expanduser("~") + "/Dropbox/my_files/research/ISR/"
exper_local_path = root + "experiments/" + system_id + "/"

# server paths
server_root = "~/research/isr/"
original_datasets_server_path = server_root + "datasets/original/"
weight_files_server_path = server_root + "datasets/2-08.06.2017/"
exper_server_path = server_root + "experiments/" + system_id + "/"
output_server_path = server_root + "outputs/"

# create and change the permition for the folder in which the output files will be written
if not path.exists(exper_local_path + "outputs/"):
    makedirs(exper_local_path + "outputs/")
chmod(exper_local_path + "outputs/", 0o777)

# change the permission for the jar file
chmod(exper_local_path + jar_name, 0o777)

# set-up the e-mail notification
email_notif_enabled = input("Notify end of execution by e-mail? (y/n) ")
if email_notif_enabled == "y":
    email_address = input("E-mail address: ")
else:
    email_address = None

# generation of the batch script and the configuration files
for embedding, embedding_alias in embeddings.items():
    for scheme, scheme_alias in schemes.items():
        for dist_metric, dist_metric_alias in dist_metrics.items():
            # full identifier for the experiment
            exper_id = "-".join([str(exper_number), exper_date, embedding_alias, scheme_alias, dist_metric_alias])

            # full identifier for the output folder
            output_folder = "-".join([str(output_number), system_id, exper_id]) + "/"

            # local folder in which the configuration files will be written
            config_files_local_path = exper_local_path + exper_id + "/"
            if not path.exists(config_files_local_path):
                makedirs(config_files_local_path)

            print("Creating config files at: " + config_files_local_path)

            # batch script header
            batch_file = open(config_files_local_path + "slurm_batch.sh", "w")
            chmod(config_files_local_path + "slurm_batch.sh", 0o777)
            batch_file.write("#!/bin/bash\n\n")
            batch_file.write("#SBATCH --nodelist <node>\n")
            batch_file.write("#SBATCH --exclusive\n\n")

            # create the master configuration file
            parent_file = open(config_files_local_path + "master.txt", "w")

            parent_file.write("experiment.output.dir = " + output_server_path + output_folder + "\n")
            parent_file.write("experiment.seed = 123456\n")
            parent_file.write("tree.build.terminals = edu.gsgp.nodes.terminals.ERC\n")
            parent_file.write("tree.build.functions = edu.gsgp.nodes.functions.AQ,edu.gsgp.nodes.functions.Sub,"
                              "edu.gsgp.nodes.functions.Add,edu.gsgp.nodes.functions.Mul\n")
            parent_file.write("pop.fitness = edu.gsgp.population.fitness.FitnessRMSE\n")
            parent_file.write("tree.build.builder = RHH\n")
            parent_file.write("tree.build.builder.random.tree = GROW\n")
            parent_file.write("pop.ind.selector = TOURNAMENT\n")
            parent_file.write("experiment.design = holdout\n")
            parent_file.write("tree.build.max.depth = 6\n")
            parent_file.write("tree.min.depth = 2\n")
            parent_file.write("evol.num.generation = 250\n")
            parent_file.write("experiment.num.repetition = 50\n")
            parent_file.write("pop.size = 1000\n")
            parent_file.write("rt.pool.size = 200\n")
            parent_file.write("evol.num.threads = 4\n")
            parent_file.write("pop.ind.selector.tourn.size = 10\n")
            parent_file.write("evol.min.error = 0\n")
            parent_file.write("breed.mut.step = 0.1\n")
            parent_file.write("breed.mut.step.sd = true\n")
            parent_file.write("breed.list = edu.gsgp.population.operator.GSMBreeder*0.5, "
                              "edu.gsgp.population.operator.GSXBreeder*0.5\n")
            parent_file.write("pop.initializer = edu.gsgp.population.populator.SimplePopulator\n")
            parent_file.write("pop.pipeline = edu.gsgp.population.pipeline.StandardPipe\n")
            parent_file.write("pop.initializer.attempts = 10\n")
            parent_file.write("breed.spread.prob = 0.5\n")
            parent_file.write("breed.spread.alpha = 2.0\n")

            for dataset in datasets:
                # create the child configuration file
                curr_child_file = open(config_files_local_path + dataset + ".txt", "w")

                curr_child_file.write("parent = " + exper_server_path + exper_id + "/master.txt\n")
                curr_child_file.write("experiment.data = " + original_datasets_server_path + dataset + "-train-#.csv\n")
                curr_child_file.write("experiment.data.test = " + original_datasets_server_path + dataset +
                                      "-test-#.csv\n")
                curr_child_file.write("experiment.weight.file = " + weight_files_server_path + embedding + "/weights/" +
                                      scheme + "-" + dist_metric + "-" + dataset)

                # The "ppb" dataset had to be modified in order to apply the embedding methods on it. That means that
                # there are two versions of the dataset (the other one is called "ppb-wth0s). In the experiments, the
                # weights are based on the modified version, but it is the original version that is passed to the GSGP.
                # This part takes care of it. Though this fix was excessively adhoc, it minimizes the risk of errors in
                # future experiments.
                if not dataset == "ppb":
                    curr_child_file.write("-train-#.csv\n")
                else:
                    curr_child_file.write("-wth0s-train-#.csv\n")

                curr_child_file.write("experiment.file.prefix = output-files/output-" + dataset + "\n")

                # append the execution command to the batch file
                batch_file.write("java -Xms512m -Xmx8g -jar " + "../GSGP-WeightedFitness.jar -p " + exper_server_path +
                                 exper_id + "/" + dataset + ".txt > " + output_server_path + output_folder + dataset +
                                 ".txt\n")

            # add the e-mail notification at the end of the batch file
            if email_notif_enabled == "y":
                batch_file.write("\necho \"\" | mail -s " + exper_id + " " + email_address)

            # server folder in which the output files will be written
            output_local_path = exper_local_path + "outputs/" + output_folder
            if not path.exists(output_local_path + "output-files/"):
                makedirs(output_local_path + "output-files/")
            chmod(output_local_path + "output-files/", 0o777)
            chmod(output_local_path, 0o777)

            exper_number += 1
            output_number += 1

print("Done.")
