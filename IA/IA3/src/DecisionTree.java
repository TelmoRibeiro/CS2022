import java.util.Arrays;
import java.util.HashSet;

public class DecisionTree {
    // this function cannot be aplied to null containing arrays  
    public static boolean allEquals(String[][] dataHolder) {
        int j = dataHolder[0].length - 1;
        for (int i = 1; i < dataHolder.length - 1; i++) {
            if (!dataHolder[i][j].equals(dataHolder[i + 1][j])) { return false; }
        }
        return true;
    }

    // this function cannot be aplied to arrays with only headers or that contains null 
    public static String getLabel(String[][] dataHolder) {
        int i = 1;
        int j = dataHolder[0].length - 1;
        return dataHolder[i][j];   
    }

    // it may only be aplied when we still have examples ?!?
    public static boolean emptyAttributes(String[][] dataHolder) {
        // in case ID will always be present and in the first pos and we do not use it as a common att
        return dataHolder[0].length == 2;
    }

    public static int getIndex(String[] array, String key) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null)      { return -i; }
            if (array[i].equals(key))  { return  i; }
        }
        return Integer.MIN_VALUE;
    }

    public static String getMostCommon(String[][] dataHolder) {

        System.out.println("Falg: Possible Bug Zone!"); // test
        /*
        String classArray = new String[dataHolder.length - 1];
        int classCounter = new int[dataHolder.length - 1];
        int j = dataHolder[0].length - 1;
        for (int i = 1; i < dataHolder.length; i++) {
            int index = getIndex(classArray, dataHolder[i][j]);
            if (index >= 0) { classCounter[index]++; }
            if (index <  0) {
                classArray[-index] = dataHolder[i][j];
                classCounter[-index]++;
            }
        }
        int mostCommonIndex = 0;
        for (int i = 0; i < classCounter.length; i++) {
            if (classCounter[i] >= mostCommonIndex) { mostCommonIndex = i; }
        }
        return classArray[mostCommonIndex];
        */
        return null;
    }

    // it may only be aplied when we still have att ?!?
    public static boolean emptyExamples(String[][] dataHolder) {
        // in case headers keep being stored in dataHolder
        return dataHolder.length == 1;
    }

    public static Node ID3(String[][] dataHolder) {
        Node rootNode = null; 
        // this can be an headache in the future
        if (allEquals(dataHolder))       { return rootNode = new Node(getLabel(dataHolder), null); }
        if (emptyAttributes(dataHolder)) { return rootNode = new Node(getMostCommon(dataHolder), null); }
        // algorithm's body
        /*
        if (emptyExamples(dataHolder))   { }
        */
        return rootNode;
    }
}