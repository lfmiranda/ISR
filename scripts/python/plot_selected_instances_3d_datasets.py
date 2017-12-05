import numpy as np
import matplotlib.pyplot as plt
import os
import pylab

from mpl_toolkits.mplot3d import Axes3D

datasets = [
    "kotanchek-fake-train-0",
]

selection_levels = [
    # "s05",
    # "s10",
    # "s15",
    # "s20",
    "s25"
]
schemes = ["pro", "sur", "rem"]

# local paths
base_dir = os.path.expanduser("~/Dropbox/my_files/research/ISR/")
data_folder = base_dir + "datasets/is_test/original/"
plots_folder = base_dir + "plots/19-04.12.2017/"

if not os.path.exists(plots_folder):
    os.makedirs(plots_folder)

plt.rc('text', usetex=True)
plt.rc('font', family='serif')

for dataset in datasets:
    for selection_level in selection_levels:
        for scheme in schemes:
            print("Dataset: " + dataset)

            fold = data_folder + selection_level + "/" + scheme + "/euc/" + dataset + ".csv"

            # read and process the datasets
            data = np.loadtxt(fold, delimiter=",")
            X = data[:, 0]
            Y = data[:, 1]
            Z = data[:, 2]

            fig = pylab.figure(figsize=(16, 10))

            ax = Axes3D(fig)

            # get rid of the panes
            ax.w_xaxis.set_pane_color((1.0, 1.0, 1.0, 0.0))
            ax.w_yaxis.set_pane_color((1.0, 1.0, 1.0, 0.0))
            ax.w_zaxis.set_pane_color((1.0, 1.0, 1.0, 0.0))

            # get rid of the grid lines
            ax.grid(False)

            # customise the size of axis ticks
            plt.xticks(range(0, 6, 1), [str(r"%d" % x) for x in range(0, 6, 1)], fontsize=14)
            plt.yticks(range(0, 6, 1), [str(r"%d" % y) for y in range(0, 6, 1)], fontsize=14)
            ax.zaxis.set_ticklabels(np.arange(0, 1, 0.2), [str(r"%.1f" % z) for z in np.arange(0, 1, 0.2)], fontsize=14)

            ax.scatter(X, Y, Z, c="#1F77B4", facecolors="none", alpha=0.4)

            ax.set_xlabel(r"$x1$", fontsize=20)
            ax.set_ylabel(r"$x2$", fontsize=20)
            ax.set_zlabel(r"$y$", fontsize=20)

            plt.savefig(plots_folder + scheme + "-" + dataset + "-" + selection_level + ".pdf", bbox_inches="tight")
            plt.close()
