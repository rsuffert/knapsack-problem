package src;
    /**
     * Represents an item that can be inserted in the knapsack.
     */
    public record Item(String description, int weight, int value) {}