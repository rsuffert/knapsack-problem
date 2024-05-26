# Check parameters
if [ $# -ne 2 ]; then
    echo "Usage: $0 <input-items-file-path> <maximum-knapsack-weight>"
    exit 1
fi

# Check if Miller (mlr) is installed
if ! command -v mlr &> /dev/null; then
    echo "Missing dependency: Miller (mlr). Install it with sudo apt install miller."
    exit 1
fi

# Run the Java solver program
javac src/*.java
java src/Solver.java $1 io-files/knapsack.csv $2 2> error.log

# Check if the Java program executed successfully
if [ -s error.log ]; then
    echo "The Java program ended with the error below. Aborting..."
    cat error.log
    exit 1
fi

# Remove the output error log file
rm error.log

# Print the CSV file as a table
mlr --icsv --ifs ';' --opprint cat io-files/knapsack.csv
