import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements a solution to the Knapsack problem using dynamic programming and also a greedy approximation.
 */
public class Knapsack {
    /**
     * Returns the optimal list of items that can be added to a knapsack of a given maximum weight using dynamic programming. Executes in O(number of items * maxWeight) (pseudo-polynomial).
     * @param items the possible items to be inserted in the knapsack.
     * @param maxWeight the maximum weight of the knapsack.
     * @return the optimal list of items that can be added to the knapsack.
     */
    public static List<Item> solveByDynamicProgramming(List<Item> items, int maxWeight) {
        // dummy item at first position
        items = new LinkedList<>(items);
        items.add(0, null);

        return calculateItems(items, maxWeight, buildMemoMatrix(items, maxWeight));
    }

    /**
     * Builds the memoization matrix for a given list of items and the maximum weight of the knapsack.
     * @param items the possible items to be inserted in the knapsack.
     * @param maxWeight the maximum weight of the knapsack.
     * @return the memoization matrix for the given instance of the problem.
     */
    private static int[][] buildMemoMatrix(List<Item> items, int maxWeight) {
        int[][] M = new int[items.size()][maxWeight+1]; // items already includes the dummy item, so no need to do + 1

        for (int x=1; x<M.length; x++) {
            for (int W=0; W<M[x].length; W++) {
                // java already initializes matrix with zeros, so no need to do it again
                if (items.get(x).weight() > W) // violation of weight restriction
                    M[x][W] = M[x-1][W];
                else // consider case where item is added and where it is not
                    M[x][W] = Math.max(items.get(x).value() + M[x-1][W-items.get(x).weight()], M[x-1][W]);
            }
        }

        return M;
    }

    /**
     * Given the memoization matrix, calculates the items that have been inserted to the knapsack.
     * @param items the possible items to be inserted in the knapsack.
     * @param maxWeight the maximum weight of the knapsack.
     * @param memoMatrix the memoization matrix.
     * @return the optimal list of items inserted in the knapsack.
     */
    private static List<Item> calculateItems(List<Item> items, int maxWeight, int[][] memoMatrix) {
        List<Item> solution = new LinkedList<>();
        calculateItemsRecursion(items.size()-1, maxWeight, items, memoMatrix, solution);
        return solution;
    }

    /**
     * Recursively calculates the items inserted in the knapsack.
     * @param currentItemIdx the index of the current item being inspected.
     * @param maxWeight the maximum weight of the knapsack.
     * @param items the possible items to be inserted in the knapsack.
     * @param memoMatrix the memoization matrix.
     * @param solution the list to store the solution.
     */
    private static void calculateItemsRecursion(int currentItemIdx, int maxWeight, List<Item> items, int[][] memoMatrix, List<Item> solution) {
        if (currentItemIdx == 0)
            return;
        else if (memoMatrix[currentItemIdx][maxWeight] > memoMatrix[currentItemIdx-1][maxWeight]) {
            solution.add(items.get(currentItemIdx));
            calculateItemsRecursion(currentItemIdx-1, maxWeight-items.get(currentItemIdx).weight(), items, memoMatrix, solution);
        }
        else
            calculateItemsRecursion(currentItemIdx-1, maxWeight, items, memoMatrix, solution);
    }

    /**
     * Provides a greedy approximation for the problem. Executed in O(n) (linear).
     * @param items the possible items to be inserted in the knapsack.
     * @param maxWeight the maximum weight of the knapsack.
     * @return a list containing the greedy approximation solution for the given instance of the problem.
     */
    public static List<Item> solveByGreedyApproximation(List<Item> items, int maxWeight) {
        List<Item> knapsack = new LinkedList<>();
        List<Item> sorted = items.stream()
                            .sorted(Comparator.comparing(Item::weight))
                            .collect(Collectors.toList());

        int remainingWeight = maxWeight;
        for (Item i : sorted) {
            if (i.weight() <= remainingWeight) {
                knapsack.add(i);
                maxWeight -= i.weight();
            }
        }

        return knapsack;
    }
}