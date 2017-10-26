from collections import OrderedDict
import matplotlib.pyplot as plt
import numpy as np
import os

method = "gsgp"
datasets = ["airfoil", "ccn", "ccun", "concrete", "energyCooling", "energyHeating", "keijzer-5", "keijzer-6",
            "parkinsons", "ppb", "towerData", "vladislavleva-1", "wineRed", "wineWhite", "yacht"]
synthetic_datasets = ["keijzer-5", "keijzer-6", "vladislavleva-1"]
embeddings = ["original", "isomap", "mds", "pca", "tsne"]
schemes = ["pro", "sur", "rem"]
selection_levels = ["0", "1", "5", "10", "15", "20", "25"]
make_values_relative_to_0 = False
normalize_values = False
leave_airfoil_out = False
leave_ppb_out = False
leave_keijzers_out = False

if method == "gp":
    print("(!) Synthetic datasets removed from datasets list.")
    for synthetic_dataset in synthetic_datasets:
        datasets.remove(synthetic_dataset)

base_dir = os.path.expanduser("~/Dropbox/my_files/research/instance_selection/")
new_base_dir = os.path.expanduser("~/Dropbox/my_files/research/ISR/")
output_path = new_base_dir + "plots/10-07.10.2017/" + method + "/"


def main():
    data_map = {}  # this dictionary will map each experiment with its RMSE value

    """
    Fill the data_map variable
    """
    for dataset_type in ["tr", "ts"]:
        data_path = base_dir + "results/4-11.05.2017/" + method + "-" + dataset_type + "-raw_values.csv"
        data = np.loadtxt(data_path, delimiter=',')

        curr_row = 0
        for dataset in datasets:
            for embedding in embeddings:
                curr_col = 0
                for scheme in schemes:
                    for selection_level in selection_levels:
                        curr_exp = "-".join([dataset_type, dataset, embedding, scheme, selection_level])
                        data_map[curr_exp] = data[curr_row][curr_col]
                        curr_col += 1
                curr_row += 1

    """
    Set general plots settings
    """
    plt.figure(figsize=(11, 6))  # plot size

    # remove the plot frame lines
    ax = plt.subplot(111)
    ax.spines["right"].set_visible(False)
    ax.spines["top"].set_visible(False)

    # remove the tick marks
    plt.tick_params(axis='both', which='both', bottom='off', top='off', labelbottom='on', left='off', right='off',
                    labelleft='on')

    """
    For each embedding method and selection scheme, create a plot where each line represents a dataset
    """
    if not os.path.exists(output_path + "by_strategy/"):
        os.makedirs(output_path + "by_strategy/")

    colors = ["#1F77B4", "#AEC7E8", "#FF7F0E", "#FFBB78", "#2CA02C",
              "#98DF8A", "#D62728", "#FF9896", "#9467BD", "#C5B0D5",
              "#8C564B", "#C49C94", "#E377C2", "#7F7F7F", "#BCBD22"]
    for dataset_type in ["tr", "ts"]:
        for embedding in embeddings:
            for scheme in schemes:
                for dataset in datasets:
                    color = colors[datasets.index(dataset)]
                    plot_rmse(dataset_type, dataset, embedding, scheme, data_map, color, dataset)

                ncol = 5 if method == "gsgp" else 4
                plt.legend(loc='upper left', ncol=ncol, fancybox=True, fontsize=10, frameon=False)
                curr_plot = "-".join([dataset_type, embedding, scheme])
                set_plot_properties("by_strategy/" + curr_plot, curr_plot)

    """
    For each dataset, create a plot where each line represents a selection scheme combined with a embedding method
    """
    if not os.path.exists(output_path + "by_dataset/"):
        os.makedirs(output_path + "by_dataset/")

    colors = ["#7F7F7F", "#D62728", "#1F77B4", "#2CA02C", "#BCBD22"]
    for dataset_type in ["tr", "ts"]:
        for dataset in datasets:
            for embedding in embeddings:
                for scheme in schemes:
                    color = colors[embeddings.index(embedding)]
                    plot_rmse(dataset_type, dataset, embedding, scheme, data_map, color, embedding)

            handles, labels = plt.gca().get_legend_handles_labels()
            by_label = OrderedDict(zip(labels, handles))
            plt.legend(by_label.values(), by_label.keys(), loc='upper left', ncol=5, fontsize=12, frameon=False)
            curr_plot = "-".join([dataset_type, dataset])
            set_plot_properties("by_dataset/" + curr_plot, curr_plot)

    """
    Create a plot with with all the lines
    """
    if leave_airfoil_out:
        print("(!) Dataset \"airfoil\" left out on the combined plot.")
    if leave_ppb_out:
        print("(!) Dataset \"ppp\" left out on the combined plot.")
    if leave_keijzers_out:
        print("(!) Datasets \"keijzer-5\" and \"keijzer-6\" left out on the combined plot.")

    if not os.path.exists(output_path + "combined/"):
        os.makedirs(output_path + "combined/")

    colors = ["#1F77B4", "#AEC7E8", "#FF7F0E", "#FFBB78", "#2CA02C",
              "#98DF8A", "#D62728", "#FF9896", "#9467BD", "#C5B0D5",
              "#8C564B", "#C49C94", "#E377C2", "#7F7F7F", "#BCBD22"]
    for dataset_type in ["tr", "ts"]:
        for embedding in embeddings:
            for scheme in schemes:
                for dataset in datasets:
                    if leave_airfoil_out and dataset == "airfoil":
                        continue
                    if leave_ppb_out and dataset == "ppb":
                        continue
                    if leave_keijzers_out and (dataset == "keijzer-5" or dataset == "keijzer-6"):
                        continue

                    color = colors[datasets.index(dataset)]
                    plot_rmse(dataset_type, dataset, embedding, scheme, data_map, color, dataset)

        handles, labels = plt.gca().get_legend_handles_labels()
        by_label = OrderedDict(zip(labels, handles))
        ncol = 5 if method == "gsgp" else 4
        plt.legend(by_label.values(), by_label.keys(), loc='upper left', ncol=ncol, fancybox=True, fontsize=10,
                   frameon=False)
        if leave_airfoil_out and leave_ppb_out:
            set_plot_properties("combined/" + dataset_type + "-without_airfoil_and_ppb",
                                dataset_type + "-without_airfoil_and_ppb")
        elif leave_airfoil_out:
            set_plot_properties("combined/" + dataset_type + "-without_airfoil", dataset_type + "-without_airfoil")
        elif leave_ppb_out:
            set_plot_properties("combined/" + dataset_type + "-without_ppb", dataset_type + "-without_ppb")
        elif leave_keijzers_out:
            set_plot_properties("combined/" + dataset_type + "-without_keijzers", dataset_type + "-without_keijzers")
        else:
            set_plot_properties("combined/" + dataset_type, dataset_type)


def plot_rmse(dataset_type, dataset, embedding, scheme, data_map, line_color, label):
    """
    The synthetic datasets have number of input attributes <= 2, hence the embedding methods were not
    applied to them.
    """
    if embedding != "original" and dataset in synthetic_datasets:
        return

    # read values for the current plot
    rmse_values = []
    for selection_level in selection_levels:
        curr_exp = "-".join([dataset_type, dataset, embedding, scheme, selection_level])
        rmse_values.append(data_map[curr_exp])

    # make values relative to 0%
    rmse_values_rel = [0.0]
    for i in range(1, len(rmse_values)):
        rmse_values_rel.append(100 / rmse_values[0] * (rmse_values[i] - rmse_values[0]))

    # plot values
    if make_values_relative_to_0:
        plt.plot(selection_levels, rmse_values_rel, c=line_color, alpha=0.4, label=label)
    else:
        plt.plot(selection_levels, rmse_values, c=line_color, alpha=0.4, label=label)
        plt.plot(range(0, 26), [rmse_values[0]] * 26, '--', lw=1, c="black", alpha=1)


def set_plot_properties(curr_plot, title):
    # tick a line across the center of the plot to help tracing results
    if make_values_relative_to_0:
        plt.plot(range(0, 26), [0] * 26, '--', lw=1, c="black", alpha=1)

    # set title and the label of the axes
    plt.title(title, loc="center")
    plt.xlabel("% instances removed")

    if make_values_relative_to_0:
        plt.ylabel("\u0394 RMSE (relative to 0%)")
    else:
        plt.ylabel("\u0394 RMSE")

    # save the plot
    plt.savefig(output_path + curr_plot + ".pdf", bbox_inches="tight")
    plt.cla()


if __name__ == '__main__':
    main()
    print("Done.")
