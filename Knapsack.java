
import java.util.*;
import java.io.*;

public class Knapsack {

    int[]   values;     // containt the list of item values
    int[]   weights;    // containt the list of item weights

    int     numItems;   // containt the number of items
    int     maxWeight;  // containt the max desired weight

    int[][] table;      // contains the internal table with all calculated values


    public Knapsack(String fileName, int weight) {

        readFromFile(fileName);

        numItems  = values.length;
        maxWeight = weight;

        // creates the table
        table = new int[numItems+1][maxWeight+1];

        // puts -1 in every cell (invalid number)
        for (int i = 0; i < numItems+1; i++) {
            for (int j = 0; j < maxWeight+1; j++) {
                table[i][j] = -1;
            }
        }

        // makes the first row of zeros
        for (int r = 0; r < maxWeight+1; r++) {
            table[0][r] = 0;
        }

        // makes the first column of zeros
        for (int c = 0; c < numItems+1; c++) {
            table[c][0] = 0;
        }
    }

    public int calculateTable() {

        // Main logic
        for (int item = 1; item <= numItems; item++) {
            for (int capacity = 1; capacity <= maxWeight; capacity++) {

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

        return table[numItems][maxWeight]; // Final answer: the max value for the given weight
    }

    public int singleItemForValue(int value) {

        for (int item = 1; item < numItems+1; item++) {
            for (int capacity = 1; capacity < maxWeight+1; capacity++) {
                if (value == table[item][capacity]) {
                    return item;
                }
            }
        }

        return 0;
    }

    public void itemsForValue(int value) {

        boolean[] vals = new boolean[numItems+1];
        for (int i = 0; i < numItems+1; i++) {
            vals[i] = false;
        }

        int curValue = value;
        while (curValue > 0) {
            int item = singleItemForValue(curValue);
            if (item > 0) {
                vals[item] = true;
                curValue -= values[item-1];
            }
        }

        System.out.print("List of items::\t");
        boolean printed = false;
        for (int i = 0; i < numItems+1; i++) {
            if (vals[i]) {
                if (printed == true) 
                    System.out.print(", ");
                System.out.print(i);
                printed = true;
            }
        }
        System.out.println();
    }

    public void readFromFile(String fileName) {
        // creates an array of strings
        List<String> lines = new ArrayList<String>();

        try {
            Scanner scanner = new Scanner(new File(fileName));
            scanner.useDelimiter("\n"); // stopping at every new line

            while(scanner.hasNext()) {  // checks if there is another line
                String str = scanner.next();
                if (!str.isEmpty()) {
                    lines.add(str);
                }
            }
           scanner.close();        // close the scanner
        } catch (IOException e) {  // exception from try
            System.out.printf("Invalid input file name:: %s", e);
            System.exit(0);        // if there is an error in accessing the file we exit the entire program
        }

        if (lines.size() > 0) {
            values  = new int[lines.size()];
            weights = new int[lines.size()];
        }

        int i = 0;
        for (String line : lines) {

            // fills the arrays values and weights
            try {
                Scanner lineScanner = new Scanner(line);
                lineScanner.useDelimiter(","); // don't include the separator commas

                if(lineScanner.hasNext()) {
                    values[i] = Integer.parseInt(lineScanner.next().trim()); // remove spaces and convert into an integer

                }

                if(lineScanner.hasNext()) {
                    weights[i++] = Integer.parseInt(lineScanner.next().trim());
                }

                lineScanner.close();
            } catch (RuntimeException e) {
                System.out.printf("Invalid input file format:: %s", e);
                System.exit(0);     // if there is an error in scanning the file we exit the entire program
            }
        }
    }

    public void printInputs() {
        String marker = "-------------------------------------------------------------------------------------------";

        System.out.printf("%s\n  Items::\t", marker);
        for (int i = 1; i < numItems+1; i++) {
            System.out.printf("%d\t", i);
        }

        System.out.printf("\n%s\n Values::\t", marker);
        for (int i = 0; i < numItems; i++) {
            System.out.printf("%d\t", values[i]);
        }

        System.out.print("\nWeights::\t");
        for (int i = 0; i < weights.length; i++) {
            System.out.printf("%d\t", weights[i]);
        }

        System.out.printf("\n%s\nMax weight::\t%d\n\n", marker, maxWeight);
    }

    public void printTable() {
        String marker = "-------------------------------------------------------------------------------------------";

        for (int i = 0; i < numItems+1; i++) {
            for (int j = 0; j < maxWeight+1; j++) {
                if (table[i][j] != -1) {
                    System.out.print(table[i][j]);
                }
                System.out.print('\t');
            }
            System.out.println();
        }
        System.out.println(marker);
    }

    public static void main(String args[]) {
        //
        // The program takes two parameters - a CSV file containing the values and weights AND the max desired weight
        // Usage: java Knapsack.java <fileName> <maxWeight>
        //
        
        // process the command line. Make sure there are 2 parameters, otherwise fail and exit
        if (args.length != 2) {
            System.out.println("Usage:   java Knapsack.java <fileName> <maxWeight>");
            System.out.println("Example: java Knapsack.java ./knapsack.csv 7");
            System.exit(0);
        }

        String fileName = args[0];
        int maxWeight = 0;

        // process the command line. Make sure the second parameter is a number, otherwise fail and exit
        try {
            maxWeight = Integer.parseInt(args[1]);
        } catch (RuntimeException e) {
            System.out.printf("Second parameter must be a number:: %s", e);
            System.exit(0);
        }

        // We are done with the command line and are now ready to start the program

        // construct an object instance of the class Knapsack
        Knapsack mysack = new Knapsack(fileName, maxWeight);
        int maxValue = mysack.calculateTable();

        mysack.printInputs();
        //mysack.printTable();

        System.out.printf("Max score::\t%d\n", maxValue);
        mysack.itemsForValue(maxValue);
    }
}

