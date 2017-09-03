import numpy as np
from os import path

# experiments variables
system_id = "6-gsgp-wf-03.07.2017"
schemes = ["pro_xy", "sur_xy", "rem_xy"]
dist_metrics = ["L1.0", "L2.0"]
datasets = ["airfoil", "ccn", "ccun", "concrete", "energyCooling", "energyHeating", "keijzer-6", "keijzer-7",
            "parkinsons", "towerData", "vladislavleva-1", "wineRed", "wineWhite", "yacht"]

# kna stands for k = number of attributes.
# k1pni and k5pni stands for k = 1% and 5% of the number of instances, respectively.
neighborhood_size_ids = ["kna", "k1", "k1pni", "k5pni"]

# paths
root = path.expanduser("~") + "/Dropbox/my_files/research/ISR/"
results_path = root + "results/5-31.08.2017/"

# read the data
values = np.loadtxt(results_path + "raw_values.csv", delimiter=",")

# map that links experiments ids to their output RMSE values
mapped_values = {}

# map all the values
i = 0
for neighborhood_size_id in neighborhood_size_ids:
    for scheme in schemes:
        for dist_metric in dist_metrics:
            j = 0
            for dataset in datasets:
                for dataset_type in ["tr", "ts"]:
                    value_id = "-".join([neighborhood_size_id, scheme, dist_metric, dataset, dataset_type])
                    mapped_values[value_id] = values[i][j]
                    j += 1
            i += 1

out_file = open(results_path + "values.csv", "w")

# write the mapped values in the output file
for dataset in datasets:
    for neighborhood_size_id in neighborhood_size_ids:
        for scheme in ["pro_xy", "sur_xy", "rem_xy"]:
            for dist_metric in dist_metrics:
                for dataset_type in ["tr", "ts"]:
                    value_id = "-".join([neighborhood_size_id, scheme, dist_metric, dataset, dataset_type])
                    out_file.write(str(mapped_values[value_id]) + ",")
        for _ in range(8):
            out_file.write("\n")
