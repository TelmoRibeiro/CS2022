import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

public class Solve {
    static long expandedNodes = 0;

    public static int maxValue(Node evaluatingNode) {
        if (evaluatingNode.isTerminalNode()) { return evaluatingNode.utility; }
        int value = Integer.MIN_VALUE;
        evaluatingNode.getChildsLinkedList();
        expandedNodes += evaluatingNode.childsLinkedList.size(); //counter
        while(!evaluatingNode.childsLinkedList.isEmpty()) {
            Node child = evaluatingNode.childsLinkedList.removeFirst();
            value      = Math.max(value, minValue(child));
        }
        return value;
    }

    public static int minValue(Node evaluatingNode) {
        if (evaluatingNode.isTerminalNode()) { return evaluatingNode.utility; }
        int value = Integer.MAX_VALUE;
        evaluatingNode.getChildsLinkedList();
        expandedNodes += evaluatingNode.childsLinkedList.size(); //counter
        while(!evaluatingNode.childsLinkedList.isEmpty()) {
            Node child = evaluatingNode.childsLinkedList.removeFirst(); 
            value      = Math.min(value, maxValue(child));
        }
        return value;
    }

    public static Node miniMax(Node evaluatingNode) {
        expandedNodes = 0; // counter;
        int value = Integer.MIN_VALUE;
        Node bestNode = null;
        evaluatingNode.getChildsLinkedList();
        expandedNodes += evaluatingNode.childsLinkedList.size(); //counter
        while(!evaluatingNode.childsLinkedList.isEmpty()) {
            Node child     = evaluatingNode.childsLinkedList.removeFirst();
            int childValue = minValue(child);
            if (childValue >= value) { // trocar por >
                value    = childValue;
                bestNode = child;
            }
        }
        System.out.println("ExpandedNodes: " + expandedNodes); //counter
        return bestNode;
    }





    public static int abMaxValue(Node evaluatingNode, int alpha, int beta) {
        if (evaluatingNode.isTerminalNode()) { return evaluatingNode.utility; }
        int value = Integer.MIN_VALUE;
        evaluatingNode.getChildsLinkedList();
        expandedNodes += evaluatingNode.childsLinkedList.size(); //counter
        while(!evaluatingNode.childsLinkedList.isEmpty()) {
            Node child = evaluatingNode.childsLinkedList.removeFirst();
            value = Math.max(value, abMinValue(child, alpha, beta));
            if (value >= beta) { return value; }
            alpha = Math.max(value, alpha);
        }
        return value;
    }

    public static int abMinValue(Node evaluatingNode, int alpha, int beta) {
        if (evaluatingNode.isTerminalNode()) { return evaluatingNode.utility; }
        int value = Integer.MAX_VALUE;
        evaluatingNode.getChildsLinkedList();
        expandedNodes += evaluatingNode.childsLinkedList.size(); //counter
        while(!evaluatingNode.childsLinkedList.isEmpty()) {
            Node child = evaluatingNode.childsLinkedList.removeFirst();
            value = Math.min(value, abMaxValue(child, alpha, beta));
            if (value <= alpha) { return value; }
            beta = Math.min(value, beta);
        }
        return value;
    }

    public static Node alphaBeta(Node evaluatingNode) {
        expandedNodes = 0; // counter;
        int value = Integer.MIN_VALUE;
        Node bestNode = null;
        evaluatingNode.getChildsLinkedList();
        expandedNodes += evaluatingNode.childsLinkedList.size(); //counter
        while(!evaluatingNode.childsLinkedList.isEmpty()) {
            Node child     = evaluatingNode.childsLinkedList.removeFirst();
            int childValue = abMinValue(child, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (childValue >= value) {
                value    = childValue;
                bestNode = child;
            }
        }
        System.out.println("ExpandedNodes: " + expandedNodes); //counter
        return bestNode;
    }




    
    public static double rollOut(Node evaluatingNode) {
        Random randomSource = new Random();
        while (evaluatingNode.utility != +512 && evaluatingNode.utility != -512 && evaluatingNode.movesCounter != 42) {
            evaluatingNode.getChildsLinkedList();
            expandedNodes += evaluatingNode.childsLinkedList.size(); //counter
            Node pickedNode = evaluatingNode.childsLinkedList.get(randomSource.nextInt(evaluatingNode.childsLinkedList.size()));
            evaluatingNode.childsLinkedList.clear();
            evaluatingNode = pickedNode;
        }
        if (evaluatingNode.utility == +512) { return +1.0; }
        if (evaluatingNode.utility == -512) { return -1.0; }
        return 0.5;
    }

    public static Node expand(Node evaluatingNode) {
        Random randomSource = new Random();
        evaluatingNode.getChildsLinkedList();
        expandedNodes += evaluatingNode.childsLinkedList.size(); //counter
        if (evaluatingNode.childsLinkedList.isEmpty()) { return evaluatingNode; }
        return evaluatingNode.childsLinkedList.get(randomSource.nextInt(evaluatingNode.childsLinkedList.size()));     
    }

    public static double UCB1(Node fatherNode, Node evaluatingNode) {
        if (evaluatingNode.playsCounter == 0) {
            return evaluatingNode.winsCounter/Float.MIN_VALUE +
            Math.sqrt(2.0) *
            Math.sqrt(2.0  * Math.log(fatherNode.playsCounter/Float.MIN_VALUE));
        }
        return evaluatingNode.winsCounter/evaluatingNode.playsCounter +
        Math.sqrt(2.0) *
        Math.sqrt(2.0  * Math.log(fatherNode.playsCounter/evaluatingNode.playsCounter));
    }

    public static Node select(Node evaluatingNode, LinkedList<Node> childsLinkedList) {
        LinkedList<Node> cLL = (LinkedList<Node>)(evaluatingNode.childsLinkedList).clone();
        Node   successorNode  = null;
        double successorValue = Integer.MIN_VALUE; 
        while(!cLL.isEmpty()) {
            Node   childNode  = cLL.removeFirst();
            double childValue = UCB1(evaluatingNode, childNode);
            if (childValue >= successorValue) {
                successorValue = childValue;
                successorNode  = childNode;
            } 
        }
        return successorNode;
    } 

    public static boolean isLeaf(Node evaluatingNode) {
        return evaluatingNode.childsLinkedList.isEmpty();
    }

    public static Node MCTS(Node rootNode) {
        expandedNodes = 0; // counter;
        for (int i = 0; i < 1400000; i++) {
            System.out.println(i);

            LinkedList<Node> visited  = new LinkedList<>();
            Node          currentNode = rootNode;
            visited.addLast(currentNode);
            while (!isLeaf(currentNode)) {
                currentNode = select(currentNode, currentNode.childsLinkedList);                
                visited.addLast(currentNode);
            }
            Node childNode = expand(currentNode);
            visited.addLast(childNode);
            double value = rollOut(childNode);
            for (Node visitedNode : visited) {
                visitedNode.updateStats(childNode.turn, value);
            }  
        }
        Node   bestNode  = null;
        double bestValue = 0.0;
        LinkedList<Node> cLL = (LinkedList<Node>)(rootNode.childsLinkedList).clone();         
        while(!cLL.isEmpty()) {
            Node testNode = cLL.removeFirst();
            if (testNode.playsCounter >= bestValue) {
                bestValue = testNode.playsCounter;
                bestNode  = testNode;
            }
        }
        System.out.println("ExpandedNodes: " + expandedNodes); //counter
        return bestNode;
    }
}