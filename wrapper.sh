# Check parameters
if [ $# -ne 2 ]; then
    echo "Usage: $0 <input-items-file-path> <maximum-knapsack-weight>"
fi

javac src/*.java
java src/Solver.java $1 io-files/knapsack.csv $2

mlr --icsv --ifs ';' --opprint cat io-files/knapsack.csv