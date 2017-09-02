import numpy as np
from os import path

# experiments variables
system_id = "6-gsgp-wf-03.07.2017"
neighborhood_sizes = ["2", "4", "6", "9"]
schemes = ["pro_xy", "sur_xy", "rem_xy"]
dist_metrics = ["L0.1", "L0.5", "L1.0", "L2.0"]
datasets = ["airfoil", "ccn", "ccun", "concrete", "energyCooling", "energyHeating", "keijzer-6", "keijzer-7",
            "parkinsons", "ppb-wth0s", "towerData", "vladislavleva-1", "wineRed", "wineWhite", "yacht"]

# paths
root = path.expanduser("~") + "/Dropbox/my_files/research/ISR/"
results_path = root + "results/4-28.08.2017/"

# read the data
values = np.loadtxt(results_path + "raw_values-wth_ids.csv", delimiter=",")

# map that links experiments ids to their output RMSE values
mapped_values = {}

# map all the values
i = 0
for neighborhood_size in neighborhood_sizes:
    for scheme in schemes:
        for dist_metric in dist_metrics:
            j = 0
            for dataset in datasets:
                for dataset_type in ["tr", "ts"]:
                    value_id = "-".join([neighborhood_size, scheme, dist_metric, dataset, dataset_type])
                    mapped_values[value_id] = values[i][j]
                    j += 1
            i += 1

out_file = open(results_path + "values.csv", "w")

# write the mapped values in the output file
for dataset in datasets:
    for neighborhood_size in neighborhood_sizes:
        for scheme in ["pro_xy", "sur_xy", "rem_xy"]:
            for dist_metric in dist_metrics:
                for dataset_type in ["tr", "ts"]:
                    value_id = "-".join([neighborhood_size, scheme, dist_metric, dataset, dataset_type])
                    out_file.write(str(mapped_values[value_id]) + ",")
        for _ in range(8):
            out_file.write("\n")
