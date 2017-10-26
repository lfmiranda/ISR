from os import chmod, makedirs, path

# experiments variables
system_id = "6-gsgp-wf-03.07.2017"
jar_name = "GSGP-WeightedFitness.jar"
schemes = {"proximity-xy": "pro_xy", "surrounding-xy": "sur_xy", "remoteness-xy": "rem_xy", "nonlinearity": "nlin"}
datasets = ["airfoil", "ccn", "ccun", "concrete", "energyCooling", "energyHeating", "keijzer-6", "keijzer-7",
            "parkinsons", "towerData", "vladislavleva-1", "wineRed", "wineWhite", "yacht"]
dist_metrics = ["L1.0", "L2.0"]
exper_number = 202
exper_date = "18.09.2017"
output_number = 204

# kna stands for k = number of attributes.
# k1pni and k5pni stands for k = 1% and 5% of the number of instances, respectively.
neighborhood_size_ids = ["k1", "k2", "k5", "k9", "k1pni", "k5pni", "kna"]

# datasets information (necessary for defining, in some cases, the neighborhood sizes)
numInst = {"airfoil": 1503, "ccn": 1994, "ccun": 1994, "concrete": 1030, "energyCooling": 768, "energyHeating": 768,
           "keijzer-6": 50, "keijzer-7": 100, "parkinsons": 5875, "towerData": 4999, "vladislavleva-1": 100,
           "wineRed": 1599, "wineWhite": 4898, "yacht": 308,
           "keijzer-1": 21, "keijzer-2": 41, "keijzer-3": 61, "keijzer-4": 101, "keijzer-8": 101, "keijzer-9": 101,
           "vladislavleva-2": 100, "vladislavleva-3": 600, "vladislavleva-4": 1024, "vladislavleva-5": 300,
           "vladislavleva-7": 300, "vladislavleva-8": 50}

numAttrs = {"airfoil": 5, "ccn": 122, "ccun": 124, "concrete": 8, "energyCooling": 8, "energyHeating": 8,
            "keijzer-6": 2, "keijzer-7": 2, "parkinsons": 18, "towerData": 25, "vladislavleva-1": 3, "wineRed": 11,
            "wineWhite": 11, "yacht": 6}

# local paths
root = path.expanduser("~") + "/Dropbox/my_files/research/ISR/"
exper_local_path = root + "experiments/" + system_id + "/"

# server paths
server_root = "~/research/isr/"
original_datasets_server_path = server_root + "datasets/normalized/"
weight_files_server_path = server_root + "datasets/2-08.06.2017/"
exper_server_path = server_root + "experiments/" + system_id + "/"
output_server_path = server_root + "outputs/"

# create and change the permission for the folder in which the output files will be written
if not path.exists(exper_local_path + "outputs/"):
    makedirs(exper_local_path + "outputs/")
chmod(exper_local_path + "outputs/", 0o777)

# change the permission for the jar file
chmod(exper_local_path + jar_name, 0o777)

# generation of the batch script and the configuration files
for neighborhood_size_id in neighborhood_size_ids:
    for scheme, scheme_alias in schemes.items():
        if scheme == "nonlinearity" and neighborhood_size_id != "kna":
            continue

        for dist_metric in dist_metrics:
            if scheme == "nonlinearity" and dist_metric != "2.0":
                continue

            # full identifier of each experiment
            exper_id = "-".join([str(exper_number), exper_date, "orig", scheme_alias, neighborhood_size_id,
                                 dist_metric])

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

            # parameters of the master file
            parent_file.write("evol.num.threads = 8\n")
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

                assert neighborhood_size_id in ["k1", "k2", "k5", "k9", "k1pni", "k5pni", "kna"]

                # find the actual number of neighbors for each dataset
                if neighborhood_size_id == "kna":
                    num_neighbors = numAttrs[dataset]
                elif neighborhood_size_id == "k1pni":
                    num_neighbors = int(0.01 * numInst[dataset])
                elif neighborhood_size_id == "k5pni":
                    num_neighbors = int(0.05 * numInst[dataset])
                else:
                    num_neighbors = 1

                if num_neighbors < 1:
                    num_neighbors = 1
                num_neighbors = str(num_neighbors)

                curr_child_file.write("experiment.weight.file = " + weight_files_server_path + "original/weights/" +
                                      "-".join([scheme, "k" + num_neighbors, dist_metric, dataset, "train-#.csv\n"]))
                curr_child_file.write("experiment.file.prefix = out_files/output-" + dataset + "\n")

                # append the execution command to the batch file
                batch_file.write("java -Xms512m -Xmx8g -jar " + exper_server_path + jar_name + " -p " +
                                 exper_server_path + exper_id + "/" + dataset + ".txt > " + output_server_path +
                                 output_folder + dataset + ".txt\n")

            # server folder in which the output files will be written
            output_local_path = exper_local_path + "outputs/" + output_folder
            if not path.exists(output_local_path + "out_files/"):
                makedirs(output_local_path + "out_files/")
            chmod(output_local_path + "out_files/", 0o777)
            chmod(output_local_path, 0o777)

            exper_number += 1
            output_number += 1

print("Done.")
