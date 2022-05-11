public class Node {
    String label;
    Node[] childs; // maybe a LL?
    
    Node(String label, Node[] childs) {
        this.label = label;
        this.childs = childs;
    }

    // function not yet defined and may never be used
    public void printNode() {
        System.out.println("Function not defined");
        return;
    }
}