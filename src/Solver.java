package src;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Solver {
    public static void printSolution(List<Item> solution) {
        System.out.println("- Selected items:");
        solution.stream()
                .forEach(item -> System.out.println("\t" + item));
        System.out.printf("- Total weight: %d\n", solution.stream()
                                                          .mapToInt(Item::weight)
                                                          .sum());
        System.out.printf("- Total value: %d\n", solution.stream()
                                                          .mapToInt(Item::value)
                                                          .sum());
    }

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
        String optimalSelectedItems = optimalSolution.stream()
                                                     .map(Item::description)
                                                     .collect(Collectors.joining(", "));
        int optimalTotalWeight = optimalSolution.stream()
                                                .mapToInt(Item::weight)
                                                .sum();
        int optimalTotalValue = optimalSolution.stream()
                                               .mapToInt(Item::value)
                                               .sum();

        // calculate the infos to write to the CSV file for the approximated solution
        String approximationSelectedItems = greedyApproximation.stream()
                                                               .map(Item::description)
                                                               .collect(Collectors.joining(", "));
        int approximationTotalWeight = greedyApproximation.stream()
                                                    .mapToInt(Item::weight)
                                                    .sum();
        int approximationTotalValue = greedyApproximation.stream()
                                                   .mapToInt(Item::value)
                                                   .sum();
        double approximationRelativeError = (double) Math.abs(optimalTotalValue-approximationTotalValue)/Math.abs(optimalTotalValue)*100;

        // write calculated info to the CSV file
        try (FileWriter writer = new FileWriter(outputFilePath)) {
            writer.write("SOLUTION;SELECTED ITEMS;TOTAL WEIGHT;TOTAL VALUES;RELATIVE ERROR\n");
            writer.write(String.format("OPTIMAL;%s;%d;%d;0%%\n", optimalSelectedItems, optimalTotalWeight, optimalTotalValue));
            writer.write(String.format("APPROXIMATED;%s;%d;%d;%.2f%%", approximationSelectedItems, approximationTotalWeight, approximationTotalValue, approximationRelativeError));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}