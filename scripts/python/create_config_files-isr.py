from os import chmod, makedirs, path

# experiments variables
systems = ["ISR"]
embeddings = ["original"]
selection_level = "no_selection"
schemes = ["nonlinearity"]
dist_metrics = ["1.0", "2.0"]
dataset_type = "train"
datasets = ["airfoil", "ccn", "ccun", "concrete", "energyCooling", "energyHeating", "keijzer-6", "keijzer-7",
            "parkinsons", "towerData", "vladislavleva-1", "wineRed", "wineWhite", "yacht"]

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

numInAttrs = {"airfoil": 5, "ccn": 122, "ccun": 124, "concrete": 8, "energyCooling": 8, "energyHeating": 8,
            "keijzer-6": 1, "keijzer-7": 1, "parkinsons": 18, "towerData": 25, "vladislavleva-1": 2, "wineRed": 11,
            "wineWhite": 11, "yacht": 6}

# paths related to the experiments
root = path.expanduser("~") + "/Dropbox/my_files/research/ISR/"
original_datasets_path = root + "datasets/original/"
embeddings_path = root + "datasets/1-05.06.2017/"  # path to the datasets that have gone through the embeddings methods
output_path = root + "datasets/2-08.06.2017/"  # part of one of the parameters that will be added to each config file
experiments_path = root + "experiments/4-isr-28.06.2017/"  # root path for the batch file and the config files folder
batch_file_path = experiments_path + "run_all.sh"  # Bash script responsible for creating the config files
config_files_folder = experiments_path + "config_files/"  # folder where the config files will be written

if not path.exists(config_files_folder):
    makedirs(config_files_folder)

print("Creating config files at: " + config_files_folder)

# generation of the batch script
batch_file = open(batch_file_path, "w")
chmod(batch_file_path, 0o777)
batch_file.write("#!/bin/bash\n\n")
command_id = 1

# generation of the configuration files
for system in systems:
    for embedding in embeddings:
        input_path = original_datasets_path

        for scheme in schemes:
            for neighborhood_size_id in neighborhood_size_ids:
                if scheme == "nonlinearity" and neighborhood_size_id != "kna":
                    continue

                for dist_metric in dist_metrics:
                    for dataset in datasets:
                        assert neighborhood_size_id in ["k1", "k2", "k5", "k9", "k1pni", "k5pni", "kna"]

                        # find the actual number of neighbors for each dataset
                        if neighborhood_size_id == "kna":
                            num_neighbors = numInAttrs[dataset] * 2
                        elif neighborhood_size_id == "k1pni":
                            num_neighbors = int(0.01 * numInst[dataset])
                        elif neighborhood_size_id == "k5pni":
                            num_neighbors = int(0.05 * numInst[dataset])
                        else:
                            num_neighbors = 1

                        if num_neighbors < 1:
                            num_neighbors = 1
                        num_neighbors = str(num_neighbors)

                        experiment_name = "-".join([embedding, scheme, "k" + num_neighbors, "L" + dist_metric, dataset])

                        parameter_file = open(config_files_folder + experiment_name + ".txt", "w")

                        # parameters
                        parameter_file.write("original.folds.path = " + original_datasets_path + "\n")
                        parameter_file.write("input.path = " + input_path + "\n")
                        parameter_file.write("output.path = " + output_path + embedding + "/\n")
                        parameter_file.write("dataset.name = " + dataset + "-" + dataset_type + "\n")
                        parameter_file.write("scheme = " + scheme + "\n")
                        parameter_file.write("selection.level = " + selection_level + "\n")
                        parameter_file.write("distance.metric = " + dist_metric + "\n")
                        parameter_file.write("number.neighbors = " + num_neighbors + "\n")

                        batch_file.write("echo \"Current file: " + str(command_id) + " - " + experiment_name + "\"\n")
                        batch_file.write("java -ea -jar " + experiments_path + "ISR.jar -p " + experiments_path +
                                         "config_files/" + experiment_name + ".txt\n")
                        command_id += 1

print("\nDone.")
