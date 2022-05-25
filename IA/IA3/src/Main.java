import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
    
    public static void main(String[] args) {
        if (args.length == 0) { System.out.println("A path for a .csv file must be provided"); return; }
        // deal with args[1]
        File decisionTreeFile = new File(args[0]);
        String[][] dataArray  = getDataArray(decisionTreeFile);
        Node rootNode         = DecisionTree.ID3(dataArray, dataArray);
        rootNode.printDT(0);
        return;
    }
}