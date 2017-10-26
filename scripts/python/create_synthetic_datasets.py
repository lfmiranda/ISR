import numpy as np

from math import cos, e, pow, sin, log, pi
from os import makedirs, path

# local paths
root = path.expanduser("~") + "/Dropbox/my_files/research/ISR/"
data_path = root + "datasets/original/"


def f1(x):
    return -(-log(x) + (-cos(x)) + (sin((pow(-x, 2) / 2) + x)))


def f2(x):
    return sin(1+x+pow(x,2)+pow(x,3)+pow(x,4)+pow(x,5))


def f3(x):
    return (pow(sin(pi * x), 2) + sin(pi * x)) / (pi * x)


def f4(x):
    return (pow(sin(pi * x), 4) + sin(pi * x)) / (pi * x)


def f5(x):
    return (pow(sin(pi * x), 6) + sin(pi * x)) / (pi * x)


def f6(x):
    return (pow(sin(pi * x), 8) + sin(pi * x)) / (pi * x)


def f7(x):
    return (pow(sin(pi * x), 10) + sin(pi * x)) / (pi * x)


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

    create_datasets1(f1, 1, "train", 0.1, 7.5, 101)
    create_datasets1(f2, 1, "train", -1.5, 1.5, 241)
    create_datasets1(f3, 1, "train", -3, 3, 101)
    create_datasets1(f4, 1, "train", -3, 3, 101)
    create_datasets1(f5, 1, "train", -3, 3, 101)
    create_datasets1(f6, 1, "train", -3, 3, 101)
    create_datasets1(f7, 1, "train", -1.5, 4.5, 151)


if __name__ == '__main__':
    main()
