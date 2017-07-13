from os import chmod, makedirs, path

# experiments variables
systems = ["GSGP-Original"]
datasets = ["airfoil", "ccn", "ccun", "concrete", "energyCooling", "energyHeating", "keijzer-6", "keijzer-7",
            "parkinsons", "ppb", "towerData", "vladislavleva-1", "wineRed", "wineWhite", "yacht"]
experiment_id = 1

# paths related to the experiments
root = path.expanduser("~") + "/Dropbox/my_files/research/ISR/"
experiment_path = "experiments/5-gsgp-03.07.2017/"

server_root = "/home/speed/luisfmiranda/research/isr/"
original_datasets_server_path = server_root + "datasets/original/"
experiment_path_server = server_root + experiment_path
output_path_server = server_root + "outputs/"

# local folder in which the configuration files will be written
config_files_folder = root + experiment_path + str(experiment_id) + "-config_files/"

if not path.exists(config_files_folder):
    makedirs(config_files_folder)

print("Creating config files at: " + config_files_folder)

# batch script header
batch_file = open(config_files_folder + "slurm_batch.sh", "w")
chmod(config_files_folder + "slurm_batch.sh", 0o777)
batch_file.write("#!/bin/bash\n\n")
batch_file.write("#SBATCH --nodelist <node>\n")
batch_file.write("#SBATCH --exclusive\n\n")

# generation of the configuration files
parent_file = open(config_files_folder + "master.txt", "w")

parent_file.write("experiment.output.dir = " + output_path_server + "\n")
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
parent_file.write("evol.num.threads = 2\n")
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
    curr_child_file = open(config_files_folder + dataset + ".txt", "w")

    curr_child_file.write("parent = " + experiment_path_server + str(experiment_id) + "-config_files/master.txt\n")
    curr_child_file.write("experiment.data = " + original_datasets_server_path + dataset + "-train-#.csv\n")
    curr_child_file.write("experiment.data.test = " + original_datasets_server_path + dataset + "-test-#.csv\n")
    curr_child_file.write("experiment.file.prefix = output-" + dataset + "\n")

    batch_file.write("java -Xms512m -Xmx8g -jar " + "../GSGP-Original.jar -p " + experiment_path_server +
                     str(experiment_id) + "-config_files/" + dataset + ".txt > " + output_path_server + dataset +
                     ".txt\n")

print("Done.")
