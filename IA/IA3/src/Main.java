import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import java.util.regex.*;


public class Main {
    public static int countLines(File toReadFile) {
        int counter = 0;
        try {
            Scanner fileScanner = new Scanner(toReadFile);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                counter++;
            }
            fileScanner.close();
        }
        catch(FileNotFoundException fileException) {
            System.out.println("An error ocurred while trying to read the file");
            fileException.printStackTrace();
        }
        return counter;
    }

    public static String[][] getDataArray(File decisionTreeFile) {
        int fileLines = countLines(decisionTreeFile);
        String[][] dataArray = new String[fileLines][]; 
        try {
            Scanner fileScanner = new Scanner(decisionTreeFile);
            for (int l = 0; l < fileLines; l++) {
                dataArray[l] = fileScanner.nextLine().split(",");
            }
            fileScanner.close();
        }
        catch(FileNotFoundException fileException) {
            System.out.println("An error ocurred while trying to read the file");
            fileException.printStackTrace();
        }
        return dataArray;
    }

    public static int getColumn(String attribute, String[][] dataArray) {
        for (int c = 1; c < dataArray[0].length - 1; c++) { // assuming that IDs will not be used as an attribute and Class ofc will not be tested for a classifier
            if (dataArray[0][c].equals(attribute)) { return c; }
        }
        return -1;
    }

    public static String classify(Node rootNode, int r, String[][] dataArray) {
        if (rootNode.childsIndex == -1) { return rootNode.label; }
        int c = getColumn(rootNode.label, dataArray);
        for (int i = 0; i <= rootNode.childsIndex; i++) {
            if (rootNode.branches[i].equals(dataArray[r][c])) { return classify(rootNode.childs[i], r, dataArray); }
        }
        return null;
    }

    public static void main(String[] args) {
        if (args.length == 0) { System.out.println("A path for a .csv file must be provided"); return; }
        File decisionTreeFile = new File(args[0]);
        String[][] dataArray  = getDataArray(decisionTreeFile);
        Node rootNode         = DecisionTree.ID3(dataArray, dataArray);
        rootNode.printDT(0);
        if (args.length > 1) {
            System.out.println();
            File testFile = new File(args[1]);
            String[][] testArray = getDataArray(testFile);
            for (int r = 1; r < testArray.length; r++) {
                System.out.println(classify(rootNode, r, testArray));
            }
        }
        return;
    }
}