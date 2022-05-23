import java.util.HashMap;

// dataHolder can't have nulls or be only headers but must have headers
// getMostCommonValue should only be called if there are still examples
// the definition of entropy is not right, I am not using the class and, also, I may also need to use log10

public class Node {
    String      label;
    String[][]  dataHolder;
    int         rows;
    int         columns;
    Node[]      childs;
    String[]    branches;
    int         childsIndex;

    Node(String[][] dataHolder) {
        this.label       = null;
        this.dataHolder  = dataHolder;
        this.rows        = dataHolder.length;
        this.columns     = dataHolder[0].length;
        this.childs      = new Node[this.rows];
        this.branches    = new String[this.rows];
        this.childsIndex = -1;
    }
    
    // O(rows)
    public boolean allEquals() {
        int c = this.columns - 1;
        for (int r = 1; r < this.rows - 1; r++) {
            if (!this.dataHolder[r][c].equals(this.dataHolder[r + 1][c])) { return false; }
        }
        return true;
    }
    
    // O(1)
    public void getAllEqualsLabel() {
        int r = 1;
        int c = this.columns - 1;
        this.label = this.dataHolder[r][c];
        return;
    }

    // O(1)
    public boolean emptyAttributes() {
        return this.columns == 2; // in case ID will always be present and in the first pos and we do not use it as a common att - we may ask teacher if that is the case
    }

    // O(rows) - if HasMap.contains/put/get are O(1)
    public void getMostCommon() {
        String mostCommonKey = null;
        int mostCommonValue  = 0; 
        HashMap<String, Integer> attributesMap = new HashMap<>();
        int c = this.columns - 1;
        for (int r = 1; r < this.rows; r++) {
            String key = this.dataHolder[r][c];
            int value  = 0;
            if (!attributesMap.containsKey(key)) { value = 1; }
            else                                 { value = attributesMap.get(key) + 1; }
            attributesMap.put(key, value);
            if (value > mostCommonValue) {
                mostCommonValue = value;
                mostCommonKey   = key;
            }
        }
        this.label = mostCommonKey;
        return;
    }

    public HashMap<String, Double> protoA(int c) {
        HashMap<String, Double> map = new HashMap<>();
        for (int r = 1; r < this.rows; r++) {
            String key   = this.dataHolder[r][c];
            double value = 0.0;
            if (!map.containsKey(key)) { value = 1.0; }
            else                       { value = map.get(key) + 1.0; }
            map.put(key, value);
        }
        return map;
    }

    public HashMap<String, Double> protoB(int c, String supKey) {
        HashMap<String, Double> map = new HashMap<>();
        int nC = this.columns - 1;
        for (int r = 1; r < this.rows; r++) {
            if (this.dataHolder[r][c].equals(supKey)) {
                String key  = this.dataHolder[r][nC];
                double value = 0.0;
                if (!map.containsKey(key)) { value = 1.0; }
                else                       { value = map.get(key) + 1.0; }
                map.put(key, value);
            }
        }
        return map;
    }

    public double protoC(int c) {
        double avgEntropy = 0.0;
        HashMap<String, Double> mapA = this.protoA(c);
        for (HashMap.Entry<String, Double> entryA: mapA.entrySet()) {
            double entropy = 0.0;
            HashMap<String, Double> mapB = protoB(c, entryA.getKey());
            for (HashMap.Entry<String, Double> entryB: mapB.entrySet()) {
                double probability = entryB.getValue()/entryA.getValue();
                entropy -= probability * Math.log10(probability) / Math.log10(2);
            }
            avgEntropy += entropy * entryA.getValue() / (double)(this.rows - 1);
        }
        return avgEntropy;
    }
    
    public int getClassifiersColumn() {
        int classifiersColumn     = -1;
        double classifiersEntropy = Double.MAX_VALUE;
        for (int c = 1; c < this.columns - 1; c++) { // c = 0 is for IDs
            double entropy = this.protoC(c);
            if (entropy < classifiersEntropy) {
                classifiersEntropy = entropy;
                classifiersColumn  = c;
            }
        }
        return classifiersColumn;
    }

    // O(rows)
    public int countValue(int c, String value) {
        int counter = 0;
        for (int r = 1; r < this.rows; r++) { // r = 0 is for headers
            if (this.dataHolder[r][c].equals(value)) { counter++; }
        }
        return counter;
    }

    // O(rows)(1 + O(columns)) - O(rows) comes from countValue(_, _)
    public String[][] getChildDataHolder(int c, String value) {
        int childRows    = this.countValue(c, value) + 1;
        int childColumns = this.columns - 1;
        String[][] childDataHolder = new String[childRows][childColumns];
        int cR = 0;
        int cC = 0;   
        for (int fR = 0; fR < this.rows; fR++) {
            if (fR == 0 || this.dataHolder[fR][c].equals(value)) {
                for (int fC = 0; fC < this.columns; fC++) {    
                    if (fC != c) { 
                        childDataHolder[cR][cC] = this.dataHolder[fR][fC];
                        cC++;
                    }
                }
                cC=0;
                cR++; 
            }
        }
        return childDataHolder;
    }





    // just used for testing may be erased in the end
    public void printNode() {
        System.out.println("Label: " + this.label);
        System.out.println("Data Holder: ");
        for (int i = 0; i < this.dataHolder.length; i++) {
            for (int j = 0; j < this.dataHolder[i].length; j++) {
                System.out.print(dataHolder[i][j] + "\t");
            }
            System.out.println();
        }
        return;
    }
}