## Scope
This project consists in an implementation for the Knapsack Problem in Java.

## Folder structure
- `src`: the source Java files that implement the Knapsack Problem solution;
- `io-files`: CSV files used to provid the input for the Knapsack Solver program and where it writes its output, also in CSV format;
- `wrapper.sh`: a wrapper script for Linux that shows the results in the command-line formatted as a table instead of just writing them on a CSV file.

## Running the program using the Linux wrapper script
1. Open the file `io-files/items.csv` and fill it with the items you want to be considered for the knapsack;
2. Run the `wrapper.sh` script with the following syntax: `./wrapper.sh <input-items-file-path> <maximum-knapsack-weight>`;
3. The results will be displayed on the command-line as a table. Alternatively, you may check them in the raw CSV format at `io-files/knapsack.csv`.

## Running the program using the Java solver program directly
1. Navigate to the `src` directory;
2. Compile all Java files by running `javac *.java`;
3. Run the Solver class with the following syntax: `java Solver.java <input-items-file-path> <output-knapsack-file-path> <maximum-knapsack-weight>`;
4. The results will be written to the `io-files/knapsack.java` file.