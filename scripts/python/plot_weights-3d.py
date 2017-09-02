import numpy as np
import matplotlib.pyplot as plt
import os
import pylab

from mpl_toolkits.mplot3d import Axes3D

# experiments variables
datasets = ["kotanchek-test-0"]
schemes = ["proximity-xy", "surrounding-xy", "remoteness-xy", "nonlinearity"]
dist_metrics = ["L2.0"]
nums_neighbors = ["k5"]

# local paths
base_dir = os.path.expanduser("~/Dropbox/my_files/research/ISR/")
data_folder = base_dir + "datasets/original/"
weights_folder = base_dir + "datasets/2-08.06.2017/original/weights/"
plots_folder = base_dir + "plots/5-01.09.2017/original/"

if not os.path.exists(plots_folder):
    os.makedirs(plots_folder)

for dataset in datasets:
    print("Dataset: " + dataset)

    fold = data_folder + dataset + ".csv"

    for scheme in schemes:
        print("  Scheme: " + scheme)

        for num_neighbors in nums_neighbors:
            for dist_metric in dist_metrics:
                # for the nonlinearity scheme only the Euclidean distance was used
                if scheme == "nonlinearity" and dist_metric != "L2.0":
                    continue

                weights = weights_folder + "-".join([scheme, num_neighbors, dist_metric, dataset]) + ".csv"

                print("    Distance metric: " + dist_metric)
                print("    Data file: " + fold)
                print("    Weight file: " + weights)

                # read and process the datasets
                data = np.loadtxt(fold, delimiter=",")
                X = data[:, 0]
                Y = data[:, 1]
                Z = data[:, 2]

                # read and process the weight files
                W = np.loadtxt(weights, delimiter=",")
                labels = ['%.2f' % elem for elem in W.tolist()]
                min_weight = min(W) * 0.95
                max_weight = max(W) * 0.95

                areas = [(w - min_weight) / (max_weight - min_weight) * 400 for w in W]

                plt.figure(figsize=(16, 8))

                fig = pylab.figure(figsize=(16, 10))
                ax = Axes3D(fig)

                # version with black rings: ax.scatter(X, Y, Z, s=areas, facecolors='none', edgecolors='black')
                ax.scatter(X, Y, Z, s=areas, c="#1F77B4", facecolors="none", alpha=0.4)

                plt.xlabel("X", fontsize=12)
                plt.ylabel("Y", fontsize=12)

                plt.title("Weights - " + scheme + ", " + dist_metric + ", " + dataset)
                plt.savefig(plots_folder + "-".join([dataset, scheme, num_neighbors, dist_metric]) + ".pdf",
                            bbox_inches="tight")

                plt.close()
