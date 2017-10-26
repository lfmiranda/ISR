import numpy as np
import matplotlib.pyplot as plt
import os
import pylab

from mpl_toolkits.mplot3d import Axes3D

datasets = [
    "salustowicz1d-skipped_pts-train-0",
]

selection_levels = ["s05", "s10", "s15", "s20", "s25"]
schemes = ["pro", "sur", "rem"]

# local paths
base_dir = os.path.expanduser("~/Dropbox/my_files/research/ISR/")
data_folder = base_dir + "datasets/is_test/original/"
plots_folder = base_dir + "plots/8-02.10.2017/"

if not os.path.exists(plots_folder):
    os.makedirs(plots_folder)

for dataset in datasets:
    for selection_level in selection_levels:
        for scheme in schemes:
            print("Dataset: " + dataset)

            fold = data_folder + selection_level + "/" + scheme + "/euc/" + dataset + ".csv"

            # read and process the datasets
            data = np.loadtxt(fold, delimiter=",")
            X = data[:, 0]
            Y = data[:, 1]

            plt.figure(figsize=(16, 10))

            ax = plt.subplot(111)
            ax.spines["top"].set_visible(False)
            ax.spines["right"].set_visible(False)

            plt.tick_params(axis="both", which="both", bottom="on", top="off", left="on", right="off", labelbottom="on",
                            labelleft="on")

            plt.scatter(X, Y, s=50, c="#1F77B4", facecolors="none", alpha=0.4)

            plt.savefig(plots_folder + scheme + "-" + dataset + "-" + selection_level + ".pdf", bbox_inches="tight")
            plt.close()
