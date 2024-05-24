import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class App {
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
        if (args.length != 2) {
            System.err.println(String.format("Expected 2 arguments, but received %d", args.length));
            System.exit(-1);
        }

        // extract command-line arguments
        String itemsFilePath = args[0];
        int maxWeight = Integer.parseInt(args[1]);

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

        // calculate the solutions and print them
        System.out.println("===================== OPTIMAL SOLUTION =====================");
        List<Item> optimalSolution = Knapsack.solveByDynamicProgramming(items, maxWeight);
        printSolution(optimalSolution);
        System.out.println("===================== GREEDY APPROXIMATION =====================");
        List<Item> greedyApproximation = Knapsack.solveByGreedyApproximation(items, maxWeight);
        printSolution(greedyApproximation);
        System.out.println("===================== COMPARISON =====================");
        int optimalValue = optimalSolution.stream()
                                          .mapToInt(Item::value)
                                          .sum();
        int approximatedValue = greedyApproximation.stream()
                                                   .mapToInt(Item::value)
                                                   .sum();
        System.out.printf("Relative error: %.2f%%\n", (double) Math.abs(optimalValue-approximatedValue)/Math.abs(optimalValue)*100);
    }
}