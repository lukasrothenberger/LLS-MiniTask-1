# How to run
## Prerequisites
### ABC executable
In `Settings.ABC.getABCExecutables`, create a new entry pointing to you local abc executable.
### Graphviz
Exporting Graphs to PNGs is supported and relies on Graphviz. If you like to use this feature, please make sure that your local `PATH` contains the directory containing the `dot` executable.
If `dot` is not available, representations in the DOT-format are created. Their contents can be displayed by a DOT viewer of choice.

## Execution
Import the Project as a regular Java project. The `main` function is located in `src.main.Main.java`. In this file, the following settings are available:
- `int[] input_files_list`: list of integers, pointing out which files from `data/aiger-set/ascii` to use as input
- `int effort`: amount of iterations to be done
- `int SubstitutionAfterUnsuccessfulIterations`: threshold for local minimum detection
- `boolean createStatisticsCSV`: if set, write statistics to CSV files.
- `int repeatStatisticsGenerationCount`: repeat process n times for statistics generation
- `boolean exportPNGEndOfIteration`_ if set, create a PNG export of the current Graph at the end of each iteration. Overwrites the previous image.

All outputs generated will be written in the created `output` folder.
