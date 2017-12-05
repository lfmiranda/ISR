import numpy as np
import matplotlib.pyplot as plt
import os

datasets = [
    "keijzer-8-train-0",
]

weighting_functions = [
    "proximity-xy",
]

dist_metrics = [
    "L2.0",
]

numsNeighbors = [
    "k2",
]

selection_levels = [
    "s25.0",
]

# experiment ids as the cartesian product of all parameters
exp_ids = tuple(
    "-".join([weighting_function, dist_metric, numNeighbors]) + "/" + selection_level
    for weighting_function in weighting_functions
    for dist_metric in dist_metrics
    for numNeighbors in numsNeighbors
    for selection_level in selection_levels
)

# local paths
base_dir = os.path.expanduser("~/Dropbox/my_files/research/ISR/")
full_data_folder = base_dir + "datasets/original/"
compressed_data_folder = base_dir + "datasets/4-21.11.2017/original/"
plots_folder = base_dir + "plots/18-21.11.2017/"

for exp_id in exp_ids:
    for dataset in datasets:
        print("Experiment id: " + exp_id)

        full_fold_path = full_data_folder + "/" + dataset + ".csv"
        compressed_fold_path = compressed_data_folder + exp_id + "/" + dataset + ".csv"

        # read and process the datasets
        full_data = np.loadtxt(full_fold_path, delimiter=",")
        full_fold_X = full_data[:, 0]
        full_fold_Y = full_data[:, 1]

        compressed_data = np.loadtxt(compressed_fold_path, delimiter=",")
        compressed_fold_X = compressed_data[:, 0]
        compressed_fold_Y = compressed_data[:, 1]

        plt.figure(figsize=(16, 10))

        ax = plt.subplot(111)
        ax.spines["top"].set_visible(False)
        ax.spines["right"].set_visible(False)

        plt.tick_params(axis="both", which="both", bottom="on", top="off", left="on", right="off", labelbottom="on",
                        labelleft="on")

        plt.scatter(full_fold_X, full_fold_Y, s=25, c="#1F77B4", facecolors="none", alpha=0.4)
        plt.scatter(compressed_fold_X, compressed_fold_Y, s=50, c="#1F77B4", facecolors="none", alpha=1)

        if not os.path.exists(plots_folder + exp_id):
            os.makedirs(plots_folder + exp_id)

        plt.savefig(plots_folder + exp_id + "/" + dataset + ".pdf", bbox_inches="tight")
        plt.close()
