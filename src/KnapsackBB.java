import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Solves the Knapsack Problem using a branch-and-bound approach.
 */
public class KnapsackBB {

    /**
     * Represents a node of the state-space tree.
     */
    public static class Node {
        public int itemIdx;
        public String itemName;
        public int totalValue;
        public int totalWeight;
        public double upperBound;
        public Node parent;

        public Node(int itemIdx, String itemName, int totalValue, int totalWeight, Node parent) {
            this.itemIdx = itemIdx;
            this.itemName = itemName;
            this.totalValue = totalValue;
            this.totalWeight = totalWeight;
            this.upperBound = 0;
            this.parent = parent;
        }
    }

    /**
     * Represents an item that can be added to the knapsack.
     */
    public static record Item (String name, int weight, int value) {}

    /**
     * Prints the solution to the Knapsack problem calculates using branch-and-bound.
     * @param n the number of items to be considered to be added to the knapsack.
     * @param wi the weights of the items.
     * @param vi the values of the items.
     * @param W the total capacity of the knapsack.
     * @throws IllegalArgumentException if {@code n} is non-positive, or if {@code wi} or {@code vi} do not have a length equal to {@code n}.
     */
    public static void solveP2(int n, int[] wi, int[] vi, int W) throws IllegalArgumentException {
        // validations
        if (n <= 0) throw new IllegalArgumentException("The number of items to be considered to be added to the knapsack must be positive");
        if (wi == null || vi == null || wi.length != n || vi.length != n) throw new IllegalArgumentException("The weigths and values arrays must be of length 'n'");

        // build items list
        List<Item> items = new ArrayList<>(n);
        for (int i=0; i<n; i++)
            items.add(new Item(i+"", wi[i], vi[i]));

        
        // calculate the solution
        Node solutionNode = calculate(items, W);
        List<String> solution = buildSolution(solutionNode);

        // output the results
        System.out.printf("SOLUTION: %s\n", Arrays.toString(solution.toArray()));
        System.out.printf("\tWeight: %d\n", solutionNode.totalWeight);
        System.out.printf("\tValue: %d\n", solutionNode.totalValue);
    }

    /**
     * Solves the knapsack problem using branch-and-bound.
     * @param items the items to be considered to be added to the knapsack.
     * @param capacity the total capacity of the knapsack.
     * @return the leaf node of the binary space-stat tree that represents the solution to the problem.
     * @throws IllegalArgumentException if {@code items} is null or empty, or if {@code capacity} is negative.
     */
    public static Node calculate(List<Item> items, int capacity) throws IllegalArgumentException {
        // validations
        if (items == null || items.size() == 0) throw new IllegalArgumentException("The list of the items must be non-null and of positive size");
        if (capacity < 0) throw new IllegalArgumentException("The capacity of the knapsack must not be negative");

        // sort items list be value-to-weight-ratios in descending order
        Collections.sort(items, (a, b) -> Double.compare((double) a.value/a.weight, (double) b.value/b.weight));

        // create root node and add to pq
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> Double.compare(a.upperBound, b.upperBound));
        Node rootNode = new Node(-1, "", 0, 0, null);
        rootNode.upperBound = calculateUpperBound(rootNode, items, capacity);
        pq.add(rootNode);

        // explore the tree
        Node currentBestAnswer = new Node(-1, "", 0, 0, null);
        while (!pq.isEmpty()) {
            Node nodeToExpand = pq.poll();

            // check if it is a leaf node (solution)
            int nextItemIdx = nodeToExpand.itemIdx+1;
            if (nextItemIdx >= items.size()) {
                if (nodeToExpand.totalValue > currentBestAnswer.totalValue) {
                    currentBestAnswer = nodeToExpand;
                    continue;
                }
            }

            // skip node if it is not promising
            if (nodeToExpand.upperBound <= currentBestAnswer.totalValue) continue;

            Item nextItem = items.get(nextItemIdx);

            // generate child with the next item
            Node nodeWithNextItem = new Node(nextItemIdx, nextItem.name, nodeToExpand.totalValue+nextItem.value, nodeToExpand.totalWeight+nextItem.weight, nodeToExpand);
            if (nodeWithNextItem.totalWeight <= capacity) { // if going through this path does not violate the weight restriction
                nodeWithNextItem.upperBound = calculateUpperBound(nodeWithNextItem, items, capacity);
                if (nodeWithNextItem.upperBound > currentBestAnswer.totalValue) // if going through this path can result in a higher value
                    pq.add(nodeWithNextItem); // queue it for analysis
            }

            // generate child without the next item
            Node nodeWithoutNextItem = new Node(nextItemIdx, nextItem.name, nodeToExpand.totalValue, nodeToExpand.totalWeight, nodeToExpand);
            nodeWithoutNextItem.upperBound = calculateUpperBound(nodeWithoutNextItem, items, capacity);
            if (nodeWithoutNextItem.upperBound > currentBestAnswer.totalValue) // if going through this path can result in a higher value
                pq.add(nodeWithoutNextItem); // queue it for analysis
        }

        return currentBestAnswer;
    }

    /**
     * Backtracks from the solution leaf node to the root node of the tree keeping track of the items added to the solution.
     * @param base the base leaf node that represents the solution to the problem.
     * @return a list containing the names of the items that make up the solution.
     * @throws IllegalArgumentException if {@code base} node is {@code null}.
     */
    public static List<String> buildSolution(Node base) throws IllegalArgumentException {
        if (base == null) throw new IllegalArgumentException("Base node must not be null");
        List<String> solution = new LinkedList<>();
        while (base.parent != null) {
            if (base.totalValue > base.parent.totalValue) solution.add(base.itemName); // then base belongs in the solution because it increases the total value
            base = base.parent; // move on to analyze its parent
        }
        return solution;
    }

    /**
     * Calculates the upper bound (that is, the maximum value that can be added to the knapsack) from a given node.
     * @param node the base node.
     * @param items all the items to be added to the knapsack, assuming this list is sorted in descending order by the value-to-weight ratios of the items.
     * @param capacity the total capacity of the knapsack.
     * @return the upper bound of {@code node}.
     */
    private static double calculateUpperBound(Node node, List<Item> items, int capacity) {
        int nextItemIdx = node.itemIdx+1;
        if (nextItemIdx >= items.size()) return node.totalValue;
        Item nextItem = items.get(nextItemIdx);
        return node.totalValue + (capacity-node.totalWeight) * ((double) nextItem.value/nextItem.weight);
    }

    public static void main(String[] args) {
        // Test case 1
        int n = 4;
        int[] weights = {2, 3, 4, 5};
        int[] values = {3, 4, 5, 6};
        int capacity = 5;
        KnapsackBB.solveP2(n, weights, values, capacity);
    }
}