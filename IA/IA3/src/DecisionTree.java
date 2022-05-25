import java.util.HashSet;

public class DecisionTree {
    public static Node ID3(String[][] dataArray, String[][] fatherDataArray) {
        Node rootNode = new Node(dataArray, fatherDataArray.length);
        if (rootNode.allEquals(rootNode.columns - 1)) {
            rootNode.label = rootNode.getAllEqualsLabel(rootNode.columns - 1);
            return rootNode;
        }
        if (rootNode.emptyAttributes()) {
            rootNode.label = rootNode.getMostCommon(rootNode.columns - 1);
            return rootNode;
        }
        int cC = rootNode.getClassifiersColumn();
        rootNode.label = rootNode.dataArray[0][cC];
        HashSet<String> values = rootNode.getValues(rootNode.label, fatherDataArray);
        for (String value: values) {
            rootNode.childsIndex++;
            String[][] childDataArray = rootNode.getChildDataArray(cC, value);
            if (Node.emptyExamples(childDataArray)) {
                Node childNode = new Node(childDataArray, fatherDataArray.length);
                childNode.label = rootNode.getMostCommon(rootNode.columns - 1);
                rootNode.branches[rootNode.childsIndex] = value;
                rootNode.childs[rootNode.childsIndex]   = childNode;
            }
            else {
                rootNode.branches[rootNode.childsIndex] = value;
                rootNode.childs[rootNode.childsIndex]   = ID3(childDataArray, dataArray);
            }
        }
        return rootNode;
    }
}