from os import chmod, makedirs, path

# experiments variables
system_id = "5-gsgp-03.07.2017"
jar_name = "GSGP-Original.jar"
datasets = ["airfoil", "ccn", "ccun", "concrete", "energyCooling", "energyHeating", "keijzer-6", "keijzer-7",
            "parkinsons", "ppb-wth0s", "towerData", "vladislavleva-1", "wineRed", "wineWhite", "yacht"]
exper_number = 2
exper_date = "06.08.2017"
output_number = 123

# local paths
root = path.expanduser("~") + "/Dropbox/my_files/research/ISR/"
exper_local_path = root + "experiments/" + system_id + "/"

# server paths
server_root = "/home/fernando/research/ISR/"
original_datasets_server_path = server_root + "datasets/normalized/"
exper_server_path = server_root + "experiments/" + system_id + "/"
output_server_path = server_root + "outputs/"

# create and change the permition for the folder in which the output files will be written
if not path.exists(exper_local_path + "outputs/"):
    makedirs(exper_local_path + "outputs/")
chmod(exper_local_path + "outputs/", 0o777)

# change the permission for the jar file
chmod(exper_local_path + jar_name, 0o777)

# full identifier of the experiment
exper_id = "-".join([str(exper_number), exper_date, "normalized"])

# full identifier of the output folder
output_folder = "-".join([str(output_number), system_id, exper_id]) + "/"

# local folder in which the configuration files will be written
config_files_local_path = exper_local_path + exper_id + "/"
if not path.exists(config_files_local_path):
    makedirs(config_files_local_path)

print("Creating config files at: " + config_files_local_path)

# batch script header
batch_file = open(config_files_local_path + "slurm_batch.sh", "w")
chmod(config_files_local_path + "slurm_batch.sh", 0o777)
batch_file.write("#!/bin/bash\n\n")
batch_file.write("#SBATCH --nodelist <node>\n")
batch_file.write("#SBATCH --exclusive\n\n")

# generation of the configuration files
parent_file = open(config_files_local_path + "master.txt", "w")

parent_file.write("experiment.output.dir = " + output_server_path + output_folder + "\n")
parent_file.write("experiment.seed = 123456\n")
parent_file.write("tree.build.terminals = edu.gsgp.nodes.terminals.ERC\n")
parent_file.write("tree.build.functions = edu.gsgp.nodes.functions.AQ,edu.gsgp.nodes.functions.Sub,"
                  "edu.gsgp.nodes.functions.Add,edu.gsgp.nodes.functions.Mul\n")
parent_file.write("pop.fitness = edu.gsgp.population.fitness.FitnessRMSE\n")
parent_file.write("tree.build.builder = RHH\n")
parent_file.write("tree.build.builder.random.tree = GROW\n")
parent_file.write("pop.ind.selector = TOURNAMENT\n")
parent_file.write("experiment.design = holdout\n")
parent_file.write("tree.build.max.depth = 6\n")
parent_file.write("tree.min.depth = 2\n")
parent_file.write("evol.num.generation = 250\n")
parent_file.write("experiment.num.repetition = 50\n")
parent_file.write("pop.size = 1000\n")
parent_file.write("rt.pool.size = 200\n")
parent_file.write("evol.num.threads = 8\n")
parent_file.write("pop.ind.selector.tourn.size = 10\n")
parent_file.write("evol.min.error = 0\n")
parent_file.write("breed.mut.step = 0.1\n")
parent_file.write("breed.mut.step.sd = true\n")
parent_file.write("breed.list = edu.gsgp.population.operator.GSMBreeder*0.5, "
                  "edu.gsgp.population.operator.GSXBreeder*0.5\n")
parent_file.write("pop.initializer = edu.gsgp.population.populator.SimplePopulator\n")
parent_file.write("pop.pipeline = edu.gsgp.population.pipeline.StandardPipe\n")
parent_file.write("pop.initializer.attempts = 10\n")
parent_file.write("breed.spread.prob = 0.5\n")
parent_file.write("breed.spread.alpha = 2.0\n")

for dataset in datasets:
    # create the child configuration file
    curr_child_file = open(config_files_local_path + dataset + ".txt", "w")

    curr_child_file.write("parent = " + exper_server_path + exper_id + "/master.txt\n")
    curr_child_file.write("experiment.data = " + original_datasets_server_path + dataset + "-train-#.csv\n")
    curr_child_file.write("experiment.data.test = " + original_datasets_server_path + dataset + "-test-#.csv\n")
    curr_child_file.write("experiment.file.prefix = output-files/output-" + dataset + "\n")

    # append the execution command to the batch file
    batch_file.write("java -Xms512m -Xmx8g -jar " + exper_server_path + jar_name + " -p " + exper_server_path +
                     exper_id + "/" + dataset + ".txt > " + output_server_path + output_folder + dataset + ".txt\n")

# server folder in which the output files will be written
output_local_path = exper_local_path + "outputs/" + output_folder
if not path.exists(output_local_path + "output-files/"):
    makedirs(output_local_path + "output-files/")
chmod(output_local_path + "output-files/", 0o777)
chmod(output_local_path, 0o777)

print("Done.")