import numpy as np
import math
import matplotlib.pyplot as plt
import os

# experiments variables
datasets = ["f7-train-0"]
schemes = [
    "proximity-xy",
    "surrounding-xy",
    "remoteness-xy",
    "nonlinearity",
]
dist_metrics = ["L2.0"]
nums_neighbors = ["k2"]

# local paths
base_dir = os.path.expanduser("~/Dropbox/my_files/research/ISR/")
data_folder = base_dir + "datasets/original/"
weights_folder = base_dir + "datasets/3-24.10.2017/original/"
plots_folder = base_dir + "plots/19-04.12.2017/"

if not os.path.exists(plots_folder):
    os.makedirs(plots_folder)

for dataset in datasets:
    print("Dataset: " + dataset)

    fold = data_folder + dataset + ".csv"

    for scheme in schemes:
        print("  Scheme: " + scheme)

        for num_neighbors in nums_neighbors:
            if scheme == "nonlinearity":
                nonlinearitySchemeAlreadyExecuted = False

            for dist_metric in dist_metrics:
                expId = "-".join([scheme, dist_metric, num_neighbors])
                weights = weights_folder + expId + "/weights/" + dataset + ".csv"

                print("    Distance metric: " + dist_metric)
                print("    Data file: " + fold)
                print("    Weight file: " + weights)

                # read and process the datasets
                data = np.loadtxt(fold, delimiter=",")
                X = data[:, 0]
                Y = data[:, 1]

                # read and process weight files
                W = np.loadtxt(weights, delimiter=",")
                labels = ['%.2f' % elem for elem in W.tolist()]
                min_weight = min(W) * 0.95
                max_weight = max(W) * 0.95

                areas = [(w - min_weight) / (max_weight - min_weight) * 400 for w in W]

                plt.figure(figsize=(18, 8))

                plt.rc('text', usetex=True)
                plt.rc('font', family='serif')

                ax = plt.subplot(111)
                ax.spines["top"].set_visible(False)
                ax.spines["right"].set_visible(False)

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
                plt.scatter(X, Y, s=areas, facecolors="none", edgecolors="black")

                plt.xlabel(r"x", fontsize=20)
                plt.ylabel(r"y", fontsize=20)

                plt.savefig(plots_folder + "-".join([dataset, scheme, num_neighbors, dist_metric]) + ".pdf",
                            bbox_inches="tight")

                plt.close()
