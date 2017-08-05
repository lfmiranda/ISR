from os import chmod, makedirs, path

# experiments variables
systems = ["ISR"]
embeddings = ["original", "isomap", "mds", "pca", "tsne"]
selection_level = "no_selection"
schemes = ["proximity-x", "proximity-xy", "surrounding-x", "surrounding-xy", "remoteness-x", "remoteness-xy"]
dist_metrics = ["0.1", "0.5", "1.0", "2.0"]
number_neighbors = "5"
dataset_type = "train"
# The "ppb" dataset had to be modified in order to apply the embedding methods on it. That means that there are two
# versions of the dataset ("ppb" and "ppb-wth0s"). Because the ISR performs a normalization step, the modified version
# must be used.
datasets = ["airfoil", "ccn", "ccun", "concrete", "energyCooling", "energyHeating", "keijzer-6", "keijzer-7",
            "parkinsons", "ppb-wth0s", "towerData", "vladislavleva-1", "wineRed", "wineWhite", "yacht"]
synthetic_datasets = ["keijzer-6", "keijzer-7", "vladislavleva-1"]
experiment_id = 1

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

# generation of the configuration files
for system in systems:
    for embedding in embeddings:
        # set the input path
        if embedding == "original":
            input_path = original_datasets_path
        else:
            input_path = embeddings_path + embedding + "/"

        for scheme in schemes:
            for dist_metric in dist_metrics:
                for dataset in datasets:
                    # The embedding methods were not applied to synthetic datasets, thus no configuration file should be
                    # created for this type of dataset.
                    if (dataset in synthetic_datasets) and (embedding != "original"):
                        continue

                    experiment_name = "-".join([str(experiment_id), embedding, scheme, "L" + dist_metric, dataset])
                    parameter_file = open(config_files_folder + experiment_name + ".txt", "w")

                    parameter_file.write("original.folds.path = " + original_datasets_path + "\n")
                    parameter_file.write("input.path = " + input_path + "\n")
                    parameter_file.write("output.path = " + output_path + embedding + "/\n")
                    parameter_file.write("dataset.name = " + dataset + "-" + dataset_type + "\n")
                    parameter_file.write("scheme = " + scheme + "\n")
                    parameter_file.write("selection.level = " + selection_level + "\n")
                    parameter_file.write("distance.metric = " + dist_metric + "\n")
                    parameter_file.write("number.neighbors = " + number_neighbors + "\n")

                    batch_file.write("echo \"Current file: " + experiment_name + "\"\n")
                    batch_file.write("java -jar " + experiments_path + "ISR.jar -p " + experiments_path +
                                     "config_files/" + experiment_name + ".txt\n")

                    experiment_id += 1

print("\nDone.")
