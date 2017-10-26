import numpy as np
import matplotlib.pyplot as plt
import os
import pylab

from mpl_toolkits.mplot3d import Axes3D

datasets = [
    "kotanchek-test-0",
]

weighting_scheme = "remoteness-xy-k8-L2.0"

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
    Z = data[:, 2]
    weights = np.loadtxt(weights_file, delimiter=",")

    order = weights.argsort()
    ranks = order.argsort()

    fig = pylab.figure(figsize=(16, 10))

    ax = Axes3D(fig)

    # get rid of the panes
    ax.w_xaxis.set_pane_color((1.0, 1.0, 1.0, 0.0))
    ax.w_yaxis.set_pane_color((1.0, 1.0, 1.0, 0.0))
    ax.w_zaxis.set_pane_color((1.0, 1.0, 1.0, 0.0))

    # get rid of the grid lines
    ax.grid(False)

    # customise the size of axis ticks
    plt.xticks(range(0, 6, 1), [str(x) for x in range(0, 6, 1)], fontsize=14)
    plt.yticks(range(0, 6, 1), [str(x) for x in range(0, 6, 1)], fontsize=14)
    ax.zaxis.set_ticklabels(np.arange(0, 1, 0.2), [str(x) for x in np.arange(0, 1, 0.2)], fontsize=14)

    weight_min = np.min(ranks)
    weight_max = np.max(ranks)
    norm_ranks = (ranks - weight_min) / (weight_max - weight_min)

    ax.scatter(X, Y, Z, color=plt.get_cmap("coolwarm")(norm_ranks))

    plt.savefig(plots_folder + weighting_scheme + "-" + dataset + ".pdf", bbox_inches="tight")
    plt.close()
