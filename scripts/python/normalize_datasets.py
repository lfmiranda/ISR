import numpy as np
from os import makedirs, path

# paths related to the datasets
root = path.expanduser("~") + "/Dropbox/my_files/research/ISR/"
original_datasets_path = root + "datasets/original/"
normalized_datasets_path = root + "datasets/normalized/"

# The "ppb" dataset contains attributes for which all instances have value 0, which means that these attributes have a
# range value that also equals to zero, which in turn causes problems during the normalization. In order to work around
# this problem, these attributes were removed and the dataset was saved as "ppb-wth0s"
# (script "rm_empty_columns_from_ppb.py"). That means that there are two versions of the dataset. All methods that take
# the normalized dataset as input or perform a normalization step must use the modified version.
datasets = ["airfoil", "ccn", "ccun", "concrete", "energyCooling", "energyHeating", "keijzer-6", "keijzer-7",
            "parkinsons", "ppb-wth0s", "towerData", "vladislavleva-1", "wineRed", "wineWhite", "yacht"]


def main():
    if not path.exists(normalized_datasets_path):
        makedirs(normalized_datasets_path)

    for dataset in datasets:
        print("Dataset: " + dataset)

        for fold_id in range(5):
            print("  Fold id: " + str(fold_id))

            tr_file_name = dataset + "-train-" + str(fold_id) + ".csv"
            ts_file_name = dataset + "-test-" + str(fold_id) + ".csv"

            # break the loop if the number of folds is smaller than five
            if not path.isfile(original_datasets_path + tr_file_name):
                break

            # load the data
            tr_data = np.loadtxt(original_datasets_path + tr_file_name, delimiter=",")
            ts_data = np.loadtxt(original_datasets_path + ts_file_name, delimiter=",")

            num_inst_tr = tr_data.shape[0]
            num_inst_ts = ts_data.shape[0]
            num_attr = tr_data.shape[1]

            # Go through each attribute, first checking the minimum value and the range and then overwritting all the
            # values for their respective normalized values.
            for attr_id in range(num_attr):
                # training and test values of the current attribute
                tr_attr_values = tr_data[:, attr_id]
                ts_attr_values = ts_data[:, attr_id]

                # Important: both training and test sets will be normalized based on the minimum value and the range of
                # the training set only, since, as a general rule, this kind of information is unknown for the test set
                # at this time (a pre-processing step).
                attr_min = tr_attr_values.min()
                attr_range = tr_attr_values.max() - attr_min

                normalize_values(tr_data, tr_attr_values, attr_min, attr_range, attr_id, num_inst_tr)
                normalize_values(ts_data, ts_attr_values, attr_min, attr_range, attr_id, num_inst_ts)

            save_values(tr_file_name, tr_data, num_inst_tr, num_attr)
            save_values(ts_file_name, ts_data, num_inst_ts, num_attr)


# Normalize the values of a specific attribute.
def normalize_values(data, attr_values, attr_min, attr_range, attr_id, numInst):
    for i in range(numInst):
        value = attr_values[i]
        data[i, attr_id] = (value - attr_min) / attr_range


# Drop the trailing zeros and write the values in the output file.
def save_values(file_name, data, num_rows, num_cols):
    out_file = open(normalized_datasets_path + file_name, "w")

    for i in range(num_rows):
        for j in range(num_cols):
            value = (str(data[i, j]))
            out_file.write(value.rstrip('0').rstrip('.') if '.' in value else value)

            if j != num_cols - 1:
                out_file.write(",")

        out_file.write("\n")


if __name__ == "__main__":
    main()
    print("Done.")
