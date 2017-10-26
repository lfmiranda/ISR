import numpy as np

from math import cos, e, pow, sin
from os import makedirs, path

# local paths
root = path.expanduser("~") + "/Dropbox/my_files/research/ISR/"
data_path = root + "datasets/original/"


def salustowicz1d(x):
    return pow(x, 3) * pow(e, -x) * cos(x) * sin(x) * (pow(sin(x), 2) * cos(x) - 1)


def salustowicz2d(x, y):
    return (y - 5) * salustowicz1d(x)


def kotanchek(x, y):
    return pow(e, -pow((x - 1), 2)) / (1.2 + pow((y - 2.5), 2))


def create_datasets1(f, num_attrs, dataset_type, a, b, num_points):
    num_points_dim = int(pow(num_points, 1 / num_attrs))
    step_size = (b - a) / (num_points_dim - 1)

    x = a
    x_values = []
    y_values = []
    z_values = []

    for _ in range(num_points_dim):
        if num_attrs == 1:
            x_values.append(x)
            y_values.append(f(x))
        else:
            y = a
            for _ in range(num_points_dim):
                z_values.append(f(x, y))

                x_values.append(x)
                y_values.append(y)
                y += step_size

        x += step_size

    dataset_file = open(data_path + f.__name__ + "-" + dataset_type + "-0.csv", "w")

    if num_attrs == 1:
        for x, y in zip(x_values, y_values):
            dataset_file.write(str(x) + "," + str(y) + "\n")
    else:
        for x, y, z in zip(x_values, y_values, z_values):
            dataset_file.write(str(x) + "," + str(y) + "," + str(z) + "\n")


def create_datasets2(f, dataset_type):
    np.random.seed(123456)
    sample1 = np.random.uniform(0.2, 3.8, 10).tolist()
    sample2 = np.random.uniform(0.2, 3.8, 10).tolist()

    dataset_file = open(data_path + f.__name__ + "-" + dataset_type + "-0.csv", "w")

    for x in sample1:
        for y in sample2:
            dataset_file.write(str(x) + "," + str(y) + "," + str(f(x, y)) + "\n")


def main():
    if not path.exists(data_path):
        makedirs(data_path)

    create_datasets1(salustowicz1d, 1, "train", 0, 10, 101)
    create_datasets1(salustowicz1d, 1, "test", -0.5, 10.5, 1001)

    create_datasets2(kotanchek, "train")
    create_datasets1(kotanchek, 2, "test", 0, 4.8, 4225)

if __name__ == '__main__':
    main()
