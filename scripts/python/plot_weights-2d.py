import numpy as np
import math
import matplotlib.pyplot as plt
import os

# experiments variables
datasets = ["ksinc-0"]
schemes = ["proximity-x", "proximity-xy", "surrounding-x", "surrounding-xy", "remoteness-x", "remoteness-xy"]
dist_metrics = ["L0.1", "L0.5", "L1.0", "L2.0"]

# local paths
base_dir = os.path.expanduser("~/Dropbox/my_files/research/ISR/")
data_folder = base_dir + "datasets/original/"
weights_folder = base_dir + "datasets/2-08.06.2017/original/weights/"
plots_folder = base_dir + "plots/3-22.07.2017/original/"

if not os.path.exists(plots_folder):
    os.makedirs(plots_folder)

for dataset in datasets:
    print("Dataset: " + dataset)

    fold = data_folder + dataset + ".csv"

    for scheme in schemes:
        print("  Scheme: " + scheme)

        for dist_metric in dist_metrics:
            weights = weights_folder + "-".join([scheme, dist_metric, dataset]) + ".csv"

            print("    Distance metric: " + dist_metric)
            print("    Data file: " + fold)
            print("    Weight file: " + weights)

            # read and process the datasets
            data = np.loadtxt(fold, delimiter=",")
            X = data[:, 0]
            Y = data[:, 1]

            # read and process the wheight files
            W = np.loadtxt(weights, delimiter=",")
            labels = ['%.2f' % elem for elem in W.tolist()]
            min_weight = min(W) * 0.95
            max_weight = max(W) * 0.95

            areas = [(w - min_weight + 0.05) / (max_weight - min_weight) * 400 for w in W]

            plt.figure(figsize=(16, 8))

            ax = plt.subplot(111)
            ax.spines["top"].set_visible(False)
            ax.spines["right"].set_visible(False)

            plt.scatter(X, Y, s=areas, label="1", c="#1F77B4", facecolors="none", alpha=0.4)

            # plot the wheights of each instance
            for label, x, y, area in zip(labels, data[:, 0], data[:, 1], areas):
                plt.annotate(
                    str(label).replace("0.", "."),
                    color="#1F77B4",
                    xy=(x, y), xytext=(0, 5 + math.sqrt(area)),
                    textcoords="offset points", ha="center", va="center", fontsize=6,
                    arrowprops=dict(arrowstyle='-', color="#1F77B4", connectionstyle='arc3,rad=0'))

            plt.title("Weights - " + scheme + ", " + dist_metric + ", " + dataset)
            plt.xlabel("Input", fontsize=12)
            plt.ylabel("Output", fontsize=12)
            plt.savefig(plots_folder + dataset + "-" + scheme + "-" + dist_metric + ".pdf", bbox_inches="tight")
            plt.close()
