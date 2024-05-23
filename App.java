import java.util.LinkedList;
import java.util.List;

public class App {
        public static void main(String[] args) {
        List<Item> items = new LinkedList<>();
        items.add(new Item(2, 3));
        items.add(new Item(3, 4));
        items.add(new Item(4, 5));
        items.add(new Item(5, 8));

        int maxWeight = 10;

        List<Item> solution = Knapsack.solveByDynamicProgramming(items, maxWeight);

        System.out.println("Selected items:");
        for (Item item : solution) {
            System.out.println(item);
        }
    }
}
