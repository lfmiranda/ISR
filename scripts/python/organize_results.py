import numpy as np
from os import path

# experiments variables
system_id = "6-gsgp-wf-03.07.2017"
embeddings = ["orig", "imap", "mds", "pca", "tsne"]
schemes = ["pro_x", "pro_xy", "sur_x", "sur_xy", "rem_x", "rem_xy"]
dist_metrics = ["L0.1", "L0.5", "L1.0", "L2.0"]
datasets = ["airfoil", "ccn", "ccun", "concrete", "energyCooling", "energyHeating", "keijzer-6", "keijzer-7",
            "parkinsons", "ppb-wth0s", "towerData", "vladislavleva-1", "wineRed", "wineWhite", "yacht"]
synthetic_datasets = ["keijzer-6", "keijzer-7", "vladislavleva-1"]
exper_number = 1
exper_date = "08.08.2017"
output_number = 1

# paths
root = path.expanduser("~") + "/Dropbox/my_files/research/ISR/"
results_path = root + "results/3-20.08.2017/"

# read the data
values = np.loadtxt(results_path + "raw_values.csv", delimiter=",")

mapped_values = {}

i = 0
for embedding in embeddings:
    for scheme in schemes:
        for dist_metric in dist_metrics:
            j = 0
            for dataset in datasets:
                for dataset_type in ["tr", "ts"]:
                    value_id = "-".join([embedding, scheme, dist_metric, dataset, dataset_type])
                    mapped_values[value_id] = values[i][j]
                    j += 1
            i += 1

out_file = open(results_path + "values-xy.csv", "w")

for dataset in datasets:
    for embedding in embeddings:
        for scheme in ["pro_xy", "sur_xy", "rem_xy"]:
            for dist_metric in dist_metrics:
                for dataset_type in ["tr", "ts"]:
                    value_id = "-".join([embedding, scheme, dist_metric, dataset, dataset_type])
                    out_file.write(str(mapped_values[value_id]) + ",")
        for _ in range(8):
            out_file.write("\n")

out_file = open(results_path + "values-x.csv", "w")

for dataset in datasets:
    for embedding in embeddings:
        for scheme in ["pro_x", "sur_x", "rem_x"]:
            for dist_metric in dist_metrics:
                for dataset_type in ["tr", "ts"]:
                    value_id = "-".join([embedding, scheme, dist_metric, dataset, dataset_type])
                    out_file.write(str(mapped_values[value_id]) + ",")
        for _ in range(8):
            out_file.write("\n")
