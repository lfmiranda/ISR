import numpy as np
import matplotlib.pyplot as plt
import os

datasets = [
    "f7-train-0",
    "salustowicz1d-skipped_pts-train-0"
]

# local paths
base_dir = os.path.expanduser("~/Dropbox/my_files/research/ISR/")
data_folder = base_dir + "datasets/original/"
plots_folder = base_dir + "plots/19-04.12.2017/"

if not os.path.exists(plots_folder):
    os.makedirs(plots_folder)

for dataset in datasets:
    print("Dataset: " + dataset)

    fold = data_folder + dataset + ".csv"

    # read and process the datasets
    data = np.loadtxt(fold, delimiter=",")
    X = data[:, 0]
    Y = data[:, 1]

    plt.figure(figsize=(18, 10))

    plt.rc('text', usetex=True)
    plt.rc('font', family='serif')

    ax = plt.subplot(111)
    ax.spines["top"].set_visible(False)
    ax.spines["right"].set_visible(False)

    plt.tick_params(axis="both", which="both", bottom="on", top="off", left="on", right="off", labelbottom="on",
                    labelleft="on")

    X_max = max(X)
    X_min = min(X)
    Y_max = max(Y)
    Y_min = min(Y)

    X_range = X_max - X_min
    Y_range = Y_max - Y_min

    X_step = X_range / 4
    Y_step = Y_range / 4

    # customise the size of axis ticks
    plt.xticks(np.arange(X_min, X_max + X_step, X_step),
               [str(r"%.1f" % x) for x in np.arange(X_min, X_max + X_step, X_step)],
               fontsize=20)

    plt.yticks(np.arange(Y_min, Y_max + Y_step, Y_step),
               [str(r"%.1f" % y) for y in np.arange(Y_min, Y_max + Y_step, Y_step)],
               fontsize=20)

    # black rings:
    plt.scatter(X, Y, s=70, facecolors="none", edgecolors="black")

    # blue circles:
    # plt.scatter(X, Y, s=50, c="#1F77B4", facecolors="none", alpha=0.4)

    plt.xlabel(r"$x$", fontsize=20)
    plt.ylabel(r"$y$", fontsize=20)

    plt.savefig(plots_folder + dataset + ".pdf", bbox_inches="tight")
    plt.close()
