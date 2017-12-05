import matplotlib.cm as cm
import matplotlib.pyplot as plt
import numpy as np
import os

from sklearn import decomposition, manifold

base_dir = os.path.expanduser("~/Dropbox/my_files/research/ISR/")
data_folder = base_dir + "datasets/original/"
outputs_folder = base_dir + "datasets/1-05.06.2017/"
plots_folder = base_dir + "plots/19-04.12.2017/"

# The original "ppb" dataset cannot be used, as it contains attributes for which all values are equals to zero (which
# causes a "division by zero" error during the normalization step).
datasets = [
    # "airfoil",
    # "ccn",
    # "concrete",
    # "ccun",
    # "energyCooling",
    "energyHeating",
    # "parkinsons",
    # "ppb-wth0s",
    # "towerData",
    # "wineRed",
    # "wineWhite",
    # "yacht",
]
num_neighbors = 5


def main():
    np.random.seed(7)  # used to initialize the centers in the MDS method

    isomap = manifold.Isomap(num_neighbors, n_components=2)
    mds = manifold.MDS(n_components=2, n_init=4, max_iter=300)
    pca = decomposition.TruncatedSVD(n_components=2)
    tsne = manifold.TSNE(n_components=2, init="pca", random_state=0)

    models = {"isomap": isomap, "mds": mds, "pca": pca, "tsne": tsne}

    for dataset in datasets:
        print("Dataset: " + dataset)
        for fold_id in range(5):
            print("  Fold: " + str(fold_id))
            input_file = dataset + "-train-" + str(fold_id)

            data = np.loadtxt(data_folder + input_file + ".csv", delimiter=",")

            num_cols = data.shape[1]
            X = data[:, range(num_cols - 1)]  # matrix with input attributes
            Y = data[:, num_cols - 1]  # vector with output attributes

            # normalization
            mean = X.mean(axis=0)
            sd = X.std(axis=0)
            X -= mean[np.newaxis, :]
            X /= sd[np.newaxis, :]

            save_embedding = save(input_file, Y)
            plot_embedding = plot(input_file, Y)

            for method, model in models.items():
                print("    Embedding: " + method)
                X_emb = model.fit_transform(X)
                save_embedding(method, X_emb)
                plot_embedding(method, X_emb)

    print("Done")


def save(input_file, Y):
    def save_embedding(method, X):
        curr_folder = outputs_folder + method

        if not os.path.exists(curr_folder):
            os.makedirs(curr_folder)

        writer = open(curr_folder + "/" + input_file + ".csv", "w")

        for i in range(X.shape[0]):
            for j in range(len(X[i])):
                writer.write(str(X[i][j]) + ",")

            writer.write(str(Y[i]) + "\n")

    return save_embedding


def plot(input_file, Y):
    def plot_embedding(method, X):
        curr_folder = plots_folder + method

        if not os.path.exists(curr_folder):
            os.makedirs(curr_folder)

        y_min = np.min(Y, 0)
        y_max = np.max(Y, 0)
        Y_norm = (Y - y_min) / (y_max - y_min)

        plt.figure(figsize=(16, 10))

        plt.rc('text', usetex=True)
        plt.rc('font', family='serif')

        ax = plt.subplot(111)
        ax.spines["top"].set_visible(False)
        ax.spines["right"].set_visible(False)
        ax.get_xaxis().tick_bottom()
        ax.get_yaxis().tick_left()

        x1_max = max(X[:, 0])
        x1_min = min(X[:, 0])
        x2_max = max(X[:, 1])
        x2_min = min(X[:, 1])

        x1_range = x1_max - x1_min
        x2_range = x2_max - x2_min

        x1_step = x1_range / 4
        x2_step = x2_range / 4

        # customise the size of axis ticks
        plt.xticks(np.arange(x1_min, x1_max + x1_step, x1_step),
                   [str(r"%.1f" % x) for x in np.arange(x1_min, x1_max + x1_step, x1_step)], fontsize=20)

        plt.yticks(np.arange(x2_min, x2_max + x2_step, x2_step),
                   [str(r"%.1f" % x) for x in np.arange(x2_min, x2_max + x2_step, x2_step)], fontsize=20)

        for i in range(X.shape[0]):
            plt.scatter(X[i, 0], X[i, 1], color=plt.get_cmap("coolwarm")(Y_norm[i]))

        color_map = cm.ScalarMappable(cmap=plt.get_cmap("coolwarm"))
        color_map.set_array(Y)

        cb = plt.colorbar(color_map)
        cb.ax.tick_params(labelsize=20)
        cb.ax.set_title(r"$y$", fontsize=20)

        plt.xlabel(r"$component\ 1$", fontsize=20)
        plt.ylabel(r"$component\ 2$", fontsize=20)

        plt.savefig(curr_folder + "/" + input_file + ".pdf", bbox_inches="tight")
        plt.close()

    return plot_embedding


if __name__ == "__main__":
    main()
