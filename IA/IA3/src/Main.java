import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import java.util.LinkedList; // teste 

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

    public static String[][] getDataHolder(File toReadFile) {
        int lines = countLines(toReadFile);
        String[][] dataHolder = new String[lines][]; 
        try {
            Scanner fileScanner = new Scanner(toReadFile);
            for (int l = 0; fileScanner.hasNextLine(); l++) {
                dataHolder[l] = fileScanner.nextLine().split(",");
            }
            fileScanner.close();
        }
        catch(FileNotFoundException fileException) {
            System.out.println("An error ocurred while trying to read the file");
            fileException.printStackTrace();
        }
        return dataHolder;
    }
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("A path for a .csv file must be provided"); 
            return;
        }
        File toReadFile = new File(args[0]);
        String[][] dataHolder = getDataHolder(toReadFile);
        Node rootNode = DecisionTree.ID3(dataHolder);
        // System.out.println(rootNode.label);

        LinkedList<Node> myLL = new LinkedList<>();
        myLL.addLast(rootNode);
        int tabsCounter = -1;
        while(!myLL.isEmpty()) {
            Node currentNode = myLL.removeFirst();
            System.out.println("Father: " + currentNode.label);
            System.out.println("Childs: ");
            for (int i = 0; i <= currentNode.childsIndex; i++) {
                Node child = currentNode.childs[i];
                System.out.println(child.label);
                myLL.addLast(child);               
            }
        }
        return;
    }
}