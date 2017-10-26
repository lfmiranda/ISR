import matplotlib.cm as cm
import numpy as np
import matplotlib.pyplot as plt
import os
import pylab

from mpl_toolkits.mplot3d import Axes3D

datasets = [
    "ksinc-0",
]

weighting_scheme = "surrounding-xy-k2-L2.0"

# local paths
base_dir = os.path.expanduser("~/Dropbox/my_files/research/ISR/")
data_folder = base_dir + "datasets/original/"
weights_folder = base_dir + "datasets/2-08.06.2017/original/weights/"
plots_folder = base_dir + "plots/7-27.09.2017/"

if not os.path.exists(plots_folder):
    os.makedirs(plots_folder)

for dataset in datasets:
    print("Dataset: " + dataset)

    fold = data_folder + dataset + ".csv"
    weights_file = weights_folder + weighting_scheme + "-" + dataset + ".csv"

    # read and process the datasets
    data = np.loadtxt(fold, delimiter=",")
    X = data[:, 0]
    Y = data[:, 1]
    weights = np.loadtxt(weights_file, delimiter=",")

    plt.figure(figsize=(16, 10))

    ax = plt.subplot(111)
    ax.spines["top"].set_visible(False)
    ax.spines["right"].set_visible(False)

    plt.tick_params(axis="both", which="both", bottom="on", top="off", left="on", right="off", labelbottom="on",
                    labelleft="on")

    weight_min = np.min(weights)
    weight_max = np.max(weights)
    weights = (weights - weight_min) / (weight_max - weight_min)

    ax.scatter(X, Y, color=plt.get_cmap("coolwarm")(weights), alpha=0.4)

    color_map = cm.ScalarMappable(cmap=plt.get_cmap("coolwarm"))
    color_map.set_array(weights)

    plt.colorbar(color_map)

    plt.savefig(plots_folder + weighting_scheme + "-" + dataset + ".pdf", bbox_inches="tight")
    plt.close()
