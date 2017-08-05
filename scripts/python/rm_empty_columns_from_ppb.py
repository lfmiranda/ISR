import numpy as np
from os import path

root = path.expanduser("~") + "/Dropbox/my_files/research/ISR/"
original_datasets_path = root + "datasets/original/"

num_attr = 627

# Each element of this array corresponds to one attribute. Attributes for which all values are 0 are marked as invalid.
# Note that this array is shared by all files (all the folds of the training and test sets), which means that an invalid
# attribute in one file can cause a valid attribute to be erased in all other files. This was done this way in order to
# prevent problems related to an inconsistent number of attributes.
invalid_attr = [False] * num_attr

# There will be two nested loops iterating throuhg all the files. The purpose of this first loop is to find out which
# attributes should be marked as invalid.
for fold_id in range(5):
    for dataset_type in ["train", "test"]:
        data = np.loadtxt(original_datasets_path + "ppb-" + dataset_type + "-" + str(fold_id) + ".csv", delimiter=",")
        num_inst = data.shape[0]

        # for each attribute, we are going to check if there is at least one non-zero value.
        for attr_id in range(num_attr):
            has_only_zero_values = True

            for inst_id in range(num_inst):
                if data[inst_id, attr_id] != 0:
                    has_only_zero_values = False
                    break

            if has_only_zero_values:
                invalid_attr[attr_id] = True  # mark the current attribute as invalid

# The purpose of the second nested loop is to write all the values of the valid attributes in as a new dataset called
# "ppb-wth0s".
for fold_id in range(5):
    for dataset_type in ["train", "test"]:
        out_file = open(original_datasets_path + "ppb-wth0s-" + dataset_type + "-" + str(fold_id) + ".csv", "w")
        data = np.loadtxt(original_datasets_path + "ppb-" + dataset_type + "-" + str(fold_id) + ".csv", delimiter=",")
        num_inst = data.shape[0]

        for inst_id in range(num_inst):
            for attr_id in range(num_attr):
                if invalid_attr[attr_id]:
                    continue

                # write the values of the valid attributes in the output file while dropping the trailing zeros.
                value = str(data[inst_id, attr_id])
                out_file.write(value.rstrip('0').rstrip('.') if '.' in value else value)

                if attr_id != num_attr - 1:
                    out_file.write(",")

            out_file.write("\n")

print("Done.")
