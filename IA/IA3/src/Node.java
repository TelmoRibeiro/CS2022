import java.util.HashMap;
import java.util.HashSet;

// maybe add a flag "boolean isLeaf = false;"
public class Node {
    String      label;
    String[][]  dataArray;
    int         rows;
    int         columns;
    Node[]      childs;
    String[]    branches;
    int         childsIndex;

    Node(String[][] dataArray, int size) {
        this.label       = null;
        this.dataArray   = dataArray;
        this.rows        = dataArray.length;
        this.columns     = dataArray[0].length;
        this.childs      = new Node[size];
        this.branches    = new String[size];
        this.childsIndex = -1;
    }
    
    // O(rows)
    public boolean allEquals(int c) {
        for (int r = 1; r < this.rows - 1; r++) { // r = 0 -> headers
            if (!this.dataArray[r][c].equals(this.dataArray[r + 1][c])) { return false; }
        }
        return true;
    }
    
    // O(1)
    public String getAllEqualsLabel(int c) {
        return this.dataArray[1][c];
    }

    // O(1)
    public boolean emptyAttributes() {
        return this.columns == 2; // assuming that IDs will not be used as an attribute
    }

    // O(rows)
    public String getMostCommon(int c) {
        String mostCommonKey = null;
        int mostCommonValue  = 0; 
        HashMap<String, Integer> attributesMap = new HashMap<>();
        for (int r = 1; r < this.rows; r++) { // r = 0 -> headers
            String key = this.dataArray[r][c];
            int value  = 0;
            if (!attributesMap.containsKey(key)) { value = 1; }
            else                                 { value = attributesMap.get(key) + 1; }
            attributesMap.put(key, value);
            if (value >= mostCommonValue) { // swap back to >
                mostCommonValue = value;
                mostCommonKey   = key;
            }
        }
        return mostCommonKey;
    }

    // O(rows)
    public HashMap<String, Double> getMapB(int c, String supKey) {
        HashMap<String, Double> map = new HashMap<>();
        int nC = this.columns - 1;
        for (int r = 1; r < this.rows; r++) { // r = 0 -> headers
            if (this.dataArray[r][c].equals(supKey)) {
                String key  = this.dataArray[r][nC];
                double value = 0.0;
                if (!map.containsKey(key)) { value = 1.0; }
                else                       { value = map.get(key) + 1.0; }
                map.put(key, value);
            }
        }
        return map;
    }

    // O(rows)
    public HashMap<String, Double> getMapA(int c) {
        HashMap<String, Double> map = new HashMap<>();
        for (int r = 1; r < this.rows; r++) { // r = 0 -> headers
            String key   = this.dataArray[r][c];
            double value = 0.0;
            if (!map.containsKey(key)) { value = 1.0; }
            else                       { value = map.get(key) + 1.0; }
            map.put(key, value);
        }
        return map;
    }

    // O(row)^2
    public double getEntropy(int c) {
        double avgEntropy = 0.0;
        HashMap<String, Double> mapA = this.getMapA(c);
        for (HashMap.Entry<String, Double> entryA: mapA.entrySet()) {
            double entropy = 0.0;
            HashMap<String, Double> mapB = getMapB(c, entryA.getKey());
            for (HashMap.Entry<String, Double> entryB: mapB.entrySet()) {
                double probability = entryB.getValue() / entryA.getValue();
                entropy -= probability * Math.log10(probability) / Math.log10(2);
            }
            avgEntropy += entropy * entryA.getValue() / (double)(this.rows - 1);
        }
        return avgEntropy;
    }
    
    // O(columns)O(row)^2
    public int getClassifiersColumn() {
        int classifierColumn     = -1;
        double classifierEntropy = Double.MAX_VALUE;
        for (int c = 1; c < this.columns - 1; c++) { // assuming that IDs will not be used as an attribute and Class ofc will not be tested for a classifier
            double entropy = this.getEntropy(c);
            if (entropy < classifierEntropy) {
                classifierEntropy = entropy;
                classifierColumn  = c;
            }
        }
        return classifierColumn;
    }

    // O(columns)
    public int getColumn(String attribute, String[][] dataArray) {
        for (int c = 1; c < dataArray[0].length - 1; c++) { // assuming that IDs will not be used as an attribute and Class ofc will not be tested for a classifier
            if (dataArray[0][c].equals(attribute)) { return c; }
        }
        return -1;
    }

    // O(rows)0(columns)
    public HashSet<String> getValues(String attribute, String[][] dataArray) {
        int c = getColumn(attribute, dataArray);
        HashSet<String> values = new HashSet<>();
        for (int r = 1; r < dataArray.length; r++) { // r = 0 -> headers
            values.add(dataArray[r][c]);
        }
        return values;
    }

    // O(rows)
    public int countValue(int c, String value) {
        int counter = 0;
        for (int r = 1; r < this.rows; r++) { // r = 0 -> headers
            if (this.dataArray[r][c].equals(value)) { counter++; }
        }
        return counter;
    }

    // O(row)O(column)
    public String[][] getChildDataArray(int c, String value) {
        int childRows    = this.countValue(c, value) + 1;
        int childColumns = this.columns - 1;
        String[][] childDataArray = new String[childRows][childColumns];
        int cR = 0;
        int cC = 0;   
        for (int fR = 0; fR < this.rows; fR++) {
            if (fR == 0 || this.dataArray[fR][c].equals(value)) {
                for (int fC = 0; fC < this.columns; fC++) {    
                    if (fC != c) { 
                        childDataArray[cR][cC] = this.dataArray[fR][fC];
                        cC++;
                    }
                }
                cC=0;
                cR++; 
            }
        }
        return childDataArray;
    }

    public static boolean emptyExamples(String[][] dataArray) {
        return dataArray.length == 1;
    }

    public void printTabs(int counter) {
        for (int i = 0; i < counter; i++) {
            System.out.print("\t");
        }
        return;
    }   

    public void printDT(int tabsCounter) {
        if (this.childsIndex == -1) {
            System.out.print(" ");
            System.out.print(this.label + " ");
            System.out.print("(" + (this.rows - 1) +")");
            System.out.println();
            return;
        }
        if (tabsCounter > 0) { System.out.println(); }
        printTabs(tabsCounter);
        System.out.println("<" + this.label + ">");
        for (int i = 0; i <= this.childsIndex; i++) {
            printTabs(tabsCounter + 1);
            System.out.print(this.branches[i] + ":");
            this.childs[i].printDT(tabsCounter + 2);
        }
        return;
    }
}