
import java.util.*;
import java.io.*;

public class Knapsack {

    int[]   values;  // = { 10, 40, 30, 50, 35, 10, 5, 20, 15, 20 };
    int[]   weights; // = {  5,  4,  6, 13,  1,  2, 5,  4, 12,  1 };

    int     maxWeight;
    int[][] table;

    public Knapsack(String fileName, int weight) {
        readFromFile(fileName);

        maxWeight = weight;

        int n = values.length;
        int w = maxWeight;

        // creates the table

        table = new int[n+1][w+1];
        
        // puts -1 in every cell (invalid number)
        for (int i = 0; i < n+1; i++) {
            for (int j = 0; j < w+1; j++) {
                table[i][j] = -1;
            }
        }

        // makes the first row of zeros
        for (int r = 0; r < w + 1; r++) {
            table[0][r] = 0;
        }

        // makes the first column of zeros
        for (int c = 0; c < n + 1; c++) {
            table[c][0] = 0;
        }

    }

    public int calculateTable() {
        int n = values.length;
        int w = maxWeight;

        // Main logic
        for (int item = 1; item <= n; item++) {
            for (int capacity = 1; capacity <= w; capacity++) {

                int maxValWithoutCurr = table[item - 1][capacity];  // This is guaranteed to exist
                int maxValWithCurr = 0;                             // We initialize this value to 0
                
                int weightOfCurr = weights[item - 1];    // We use item -1 to account for the extra row at the top
                if (capacity >= weightOfCurr) {          // We check if the knapsack can fit the current item
                    maxValWithCurr = values[item - 1];   // maxValWithCurr is at least the value of the current item
                    
                    int remainingCapacity = capacity - weightOfCurr;       // remainingCapacity must be at least 0
                    maxValWithCurr += table[item - 1][remainingCapacity];  // Add the max val obtainable with the
                                                                           // remaining capacity
                }
                
                table[item][capacity] = Math.max(maxValWithoutCurr, maxValWithCurr); // Pick the larger of the two
                //printTable();
            }
        }

        return table[n][w]; // Final answer
    }

    public void readFromFile(String fileName) {

        // creates an array of strings

        List<String> lines = new ArrayList<String>();
        try {
            Scanner scanner = new Scanner(new File(fileName));
            scanner.useDelimiter("\n"); // stopping at every new line

            while(scanner.hasNext()) { // checks if there is another line
                String str = scanner.next();
                if (!str.isEmpty()) {
                    lines.add(str); 
                }
            }
           scanner.close(); // close the scanner
        } catch (IOException e) { // exception from try
            System.out.printf("Invalid input file name:: %s", e);
            System.exit(0); // if there is an error in the file manipulation it exits the program
        }

        if (lines.size() > 0) {
            values  = new int[lines.size()];
            weights = new int[lines.size()];
        }

        int i = 0;
        for (String line : lines) {
            //System.out.println(line);

            // fills the arrays values and weights
            try {
                Scanner lineScanner = new Scanner(line);
                lineScanner.useDelimiter(","); // don't include commas

                if(lineScanner.hasNext()) {
                    values[i] = Integer.parseInt(lineScanner.next().trim()); // trim removes spaces and convert into an integer

                }

                if(lineScanner.hasNext()) {
                    weights[i++] = Integer.parseInt(lineScanner.next().trim());
                }

                lineScanner.close();
            } catch (RuntimeException e) {
                System.out.printf("Invalid input file format:: %s", e);
                System.exit(0);
            }
        }
    }

    // printing arrays to the screen

    public void printInputs() {
        int n = values.length;
        int w = weights.length;

        System.out.println("-------------------------------------------------------------------------------------------");
        System.out.print("  Items::\t");
        for (int i = 1; i < n+1; i++) {
            System.out.print(Long.toString(i));
            System.out.print('\t');
        }
        System.out.println();

        System.out.print(" Values::\t");
        for (int i = 0; i < n; i++) {
            System.out.print(values[i]);
            System.out.print('\t');
        }
        System.out.println();

        System.out.print("Weights::\t");
        for (int i = 0; i < w; i++) {
            System.out.print(weights[i]);
            System.out.print('\t');
        }
        System.out.println();
        System.out.println("-------------------------------------------------------------------------------------------");
        System.out.printf("Max weight::\t%d\n\n", maxWeight);
        //System.out.println(Arrays.toString(values));
        //System.out.println(Arrays.toString(weights));
    }

    public void printTable() {
        int n = values.length;
        int w = maxWeight;

        for (int i = 0; i < n+1; i++) {
            for (int j = 0; j < w+1; j++) {
                if (table[i][j] != -1) { // print everything except -1
                    System.out.print(table[i][j]);
                }
                System.out.print('\t');
            }
            System.out.println();
        }
        System.out.println("-----------------------------------------------------------------------------------");
    }

    public int singleItemForValue(int value) {
        int n = values.length;
        int w = maxWeight;

        for (int item = 1; item < n+1; item++) {
            for (int capacity = 1; capacity < w+1; capacity++) {
                if (value == table[item][capacity]) {
                    return item;
                }
            }
        }
        return 0;
    }

    public void itemsForValue(int value) {
        int n = values.length;
        int w = maxWeight;

        int[] vals = new int[n+1];
        for (int i = 0; i < n+1; i++) {
            vals[i] = 0;
        }

        int curValue = value;
        while (curValue > 0) {
            int item = singleItemForValue(curValue);
            if (item > 0) {
                vals[item] = 1;
                curValue -= values[item-1];
            }
        }

        System.out.print("List of items::\t");
        boolean printed = false;
        for (int i = 0; i < n+1; i++) {
            if (vals[i] == 1) {
                if (printed == true) 
                    System.out.print(", ");
                System.out.print(i);
                printed = true;
            }
        }
        System.out.println();
    }

    public static void main(String args[]) {
        // Usage: java Knapsack.java fileName maxWeight
        
        // process the command line
        String fileName = args.length > 0 ? args[0] : "knapsack.csv";
        int weight = args.length > 1 ? Integer.parseInt(args[1]) : 20;

        // construct an object called Knapsack
        Knapsack mysack = new Knapsack(fileName, weight);
        int maxValue = mysack.calculateTable();

        mysack.printInputs();
        //mysack.printTable();
        System.out.printf("Max score::\t%d\n", maxValue);
        mysack.itemsForValue(maxValue);
        //System.out.println(Arrays.deepToString(table));  // Visualization of the table
    }
}

