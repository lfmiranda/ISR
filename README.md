# Instance Selection for Regression

Instances may concentrate in some particular regions of the input space, leading the regression method to overspecialize the model to map these regrions, while areas with fewer instances are not given the necessary attention. In these cases, removing instances from these dense regions can improve the induced model by leading the regression method to consider the whole input space with similar importance.

Another case where a smaller training set could be better than a larger one occurs when the computational time spent during the learning process has crucial importance. In this case, making the training set smaller by removing some instances can reduce the computational effort employed to induce the regression model.
Instance Selection (IS) is a recurring topic in classification literature. However, when compared to the variety of instance selection techniques for classification tasks, the number of IS methods for regression problems is relatively small.

Some of the folders described bellow were added to the ".gitigore" file, eventhough they contained useful files. That is because they were too big to be uploaded.

### Main folders and files:

#### Datasets
* original
* normalized
* 1-05.06.2017: datasets created after applying the embedding methods.
* 2-08.06.2017: weight files.

#### Experiments (each root folder corresponds to one system)
* 4-isr-28.06.2017: instances' weighting algorithm. 
* 5-gsgp-03.07.2017: canonical version of the GSGP algorithm.
* 6-gsgp-wf-03.07.2017: GSGP, with fitness function modified in order to take into account intances' weights.

#### Outputs

Output files. Not included in the repository, since they are fairly big. The corresponding RMSE values are listed in the tables inside the "Results" folder.

#### Plots
* 1-05.06.2017
* 2-28.06.2017
* 3-22.07.2017
* 4-08.08.2017
* 5-01.09.2017

#### Project
* doc
* lib: required external libraries.
* parameters: parameters file samples.
* src
* test: unit tests.

#### Results
* 1-23.07.2017
* 2-07.08.2017
* 3-20.08.2017
* 4-28.08.2017

#### Scripts
* BashScript
    * sort_and_cut.sh
* Python
    * create_config_files-gsgp-original.py
    * create_config_files-gsgp-weighted_fitness.py
    * create_config_files-isr.py
    * create_embeddings.py
    * normalize_datasets.py
    * organize_results.py
    * plot_weights-2d.py
    * plot_weights-3d.py
    * rm_empty_columns_from_ppb.py
