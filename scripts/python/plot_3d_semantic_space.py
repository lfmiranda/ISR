import numpy as np
import matplotlib.pyplot as plt
import os
import pylab

from mpl_toolkits.mplot3d import Axes3D

datasets = [
    "mock_dataset",
]

# local paths
base_dir = os.path.expanduser("~/Dropbox/my_files/research/ISR/")
data_folder = os.path.expanduser("~/Desktop/")
plots_folder = base_dir + "plots/17-13.11.2017/"

if not os.path.exists(plots_folder):
    os.makedirs(plots_folder)

for dataset in datasets:
    print("Dataset: " + dataset)

    fold = data_folder + dataset + ".csv"

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

    plt.xticks(np.arange(1, 2.1, 0.2), [str(x) for x in np.arange(1, 2.1, 0.2)], fontsize=16)
    plt.yticks(np.arange(3, 4.1, 0.2), [str(y) for y in np.arange(3, 4.1, 0.2)], fontsize=16)

    ax.zaxis.set_ticks([4, 4.4, 4.8, 5.2, 5.6, 6])
    ax.zaxis.set_ticklabels(['   4.0', '   4.4', '   4.8', '   5.2', '   5.6', '   6.0'],
                            ['   4.0', '   4.4', '   4.8', '   5.2', '   5.6', '   6.0'],
                            fontsize=16)

    ax.scatter(1, 4, 6, s=200, c="#1F77B4", facecolors="none", alpha=0.4)
    ax.scatter(2, 3, 4, s=200, c="#D62728", facecolors="none", alpha=0.4)

    ax.text(2, 2, 5, 'dim1', fontsize=16)
    ax.text(2.4, 3, 4.45, 'dim2', fontsize=16)
    ax.text(2.15, 4.15, 4.9, 'dim3', fontsize=16)

    plt.savefig(plots_folder + dataset + ".pdf", bbox_inches="tight")
    plt.close()
