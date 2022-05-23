import java.util.HashSet;

public class DecisionTree {
    public static boolean emptyExamples(String[][] dataHolder) {
        return dataHolder.length == 1;
    }

    public static Node ID3(String[][] dataHolder) {
        Node rootNode = new Node(dataHolder);
        if (rootNode.allEquals()) {
            rootNode.getAllEqualsLabel();
            return rootNode; 
        }
        if (rootNode.emptyAttributes()) {
            rootNode.getMostCommon(); 
            return rootNode; 
        }
        int classifiersC = rootNode.getClassifiersColumn();
        rootNode.label = rootNode.dataHolder[0][classifiersC];
        HashSet<String> values = new HashSet<>();
        for (int r = 1; r < rootNode.rows; r++) {
            String value = rootNode.dataHolder[r][classifiersC];
            if (values.contains(value)) { continue; }
            values.add(value);
            String[][] childDataHolder = rootNode.getChildDataHolder(classifiersC, value);
            rootNode.childsIndex++;
            if (emptyExamples(childDataHolder)) { System.out.println("Work In Progress!"); rootNode.childsIndex--; return null; } // teste
            else { rootNode.childs[rootNode.childsIndex] = ID3(childDataHolder); }
        }
        return rootNode;
    }
}