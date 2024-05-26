package src;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Solver {
    public static void main(String[] args) {
        // check number of arguments
        if (args.length != 3) {
            System.err.println(String.format("Expected 3 arguments, but received %d", args.length));
            System.exit(-1);
        }

        // extract command-line arguments
        String itemsFilePath = args[0];
        String outputFilePath = args[1];
        int maxWeight = Integer.parseInt(args[2]);

        // build items list from CSV file
        List<Item> items = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(itemsFilePath))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\\s*,\\s*");
                items.add(new Item(fields[0], Integer.parseInt(fields[1]), Integer.parseInt(fields[2])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // calculate the solutions
        List<Item> optimalSolution = Knapsack.solveByDynamicProgramming(items, maxWeight);
        List<Item> greedyApproximation = Knapsack.solveByGreedyApproximation(items, maxWeight);

        // calculate the infos to write to the CSV file for the optimal solution
        StringJoiner optimalSelectedItemsJoiner = new StringJoiner(", ");
        int optimalTotalWeight = 0;
        int optimalTotalValue = 0;
        for (Item i : optimalSolution) {
            optimalTotalWeight += i.weight();
            optimalTotalValue += i.value();
            optimalSelectedItemsJoiner.add(i.description());
        }

        // calculate the infos to write to the CSV file for the approximated solution
        StringJoiner approximationSelectedItemsJoiner = new StringJoiner(", ");
        int approximationTotalWeight = 0;
        int approximationTotalValue = 0;
        for (Item i : greedyApproximation) {
            approximationTotalWeight += i.weight();
            approximationTotalValue += i.value();
            approximationSelectedItemsJoiner.add(i.description());
        }
        double approximationRelativeError = 0.0;
	    if (optimalTotalValue != 0)
            approximationRelativeError = (double) Math.abs(optimalTotalValue-approximationTotalValue)/Math.abs(optimalTotalValue)*100;

        // write calculated info to the CSV file
        try (FileWriter writer = new FileWriter(outputFilePath)) {
            writer.write("SOLUTION;ITEMS;WEIGHT;VALUE;ERROR\n");
            writer.write(String.format("OPTIMAL;%s;%d;%d;0%%\n", optimalSelectedItemsJoiner.toString(), optimalTotalWeight, optimalTotalValue));
            writer.write(String.format("APPROXIMATED;%s;%d;%d;%.2f%%", approximationSelectedItemsJoiner.toString(), approximationTotalWeight, 
                                                                       approximationTotalValue, approximationRelativeError));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}