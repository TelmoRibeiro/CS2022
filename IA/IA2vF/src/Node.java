import java.util.LinkedList;

public class Node {
    char[][]         gameGrid;
    int[]            freeRow;
    char             turn;
    int              utility;
    int              movesCounter;
    int              remainingDepth;
    LinkedList<Node> childsLinkedList;

    double           winsCounter;
    double           playsCounter;

    Node(char[][] gameGrid, int[] freeRow, char turn, int movesCounter, int remainingDepth) {
        this.gameGrid         = gameGrid;
        this.freeRow          = freeRow;
        this.turn             = turn;
        this.utility          = this.sumUtility();
        this.movesCounter     = movesCounter;
        this.remainingDepth   = remainingDepth;
        this.childsLinkedList = new LinkedList<Node>();
        
        this.winsCounter      = 0.0;
        this.playsCounter     = 0.0;
    }

    public int evaluateUtility(int XCounter, int OCounter) {
        if (OCounter == 4 && XCounter == 0) { return -512; }
        if (OCounter == 3 && XCounter == 0) { return  -50; }
        if (OCounter == 2 && XCounter == 0) { return  -10; }
        if (OCounter == 1 && XCounter == 0) { return   -1; }
        if (OCounter == 0 && XCounter == 1) { return   +1; }
        if (OCounter == 0 && XCounter == 2) { return  +10; }
        if (OCounter == 0 && XCounter == 3) { return  +50; }
        if (OCounter == 0 && XCounter == 4) { return +512; }
        return 0;
    }

    public int sumUtility() {
        int utilitySum = 0;
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 7; c++) {
                if (r + 3 < 6) {
                    int XCounter = 0; 
                    int OCounter = 0;
                    for (int i = 0; i < 4; i++) {
                        if (this.gameGrid[r + i][c] == 'X') { XCounter++; }
                        if (this.gameGrid[r + i][c] == 'O') { OCounter++; }
                    }
                    int segmentUtility = evaluateUtility(XCounter, OCounter);
                    if (segmentUtility == +512 || segmentUtility == -512) { return segmentUtility; }
                    utilitySum += segmentUtility;
                }
                if (c + 3 < 7) {
                    int XCounter = 0; 
                    int OCounter = 0;
                    for (int i = 0; i < 4; i++) {
                        if (this.gameGrid[r][c + i] == 'X') { XCounter++; }
                        if (this.gameGrid[r][c + i] == 'O') { OCounter++; }
                    }
                    int segmentUtility = evaluateUtility(XCounter, OCounter);
                    if (segmentUtility == +512 || segmentUtility == -512) { return segmentUtility; }
                    utilitySum += segmentUtility;
                }
                if (r + 3 < 6 && c + 3 < 7) {
                    int XCounter = 0; 
                    int OCounter = 0;
                    for (int i = 0; i < 4; i++) {
                        if (this.gameGrid[r + i][c + i] == 'X') { XCounter++; }
                        if (this.gameGrid[r + i][c + i] == 'O') { OCounter++; }
                    }
                    int segmentUtility = evaluateUtility(XCounter, OCounter);
                    if (segmentUtility == +512 || segmentUtility == -512) { return segmentUtility; }
                    utilitySum += segmentUtility;
                }
                if (r - 3 >= 0 && c + 3 < 7 ) {
                    int XCounter = 0; 
                    int OCounter = 0;
                    for (int i = 0; i < 4; i++) {
                        if (this.gameGrid[r - i][c + i] == 'X') { XCounter++; }
                        if (this.gameGrid[r - i][c + i] == 'O') { OCounter++; }
                    }
                    int segmentUtility = evaluateUtility(XCounter, OCounter);
                    if (segmentUtility == +512 || segmentUtility == -512) { return segmentUtility; }
                    utilitySum += segmentUtility;
                }
            }
        }
        if (this.turn == 'X') { utilitySum += +16; }
        if (this.turn == 'O') { utilitySum += -16; }
        return utilitySum;
    }

    public boolean isTerminalNode() {
        return this.remainingDepth == 0 || this.utility == +512 || this.utility == -512 || this.movesCounter == 42;
    }

    public char swapTurn() {
        if (this.turn == 'O') { return 'X'; }
        else                  { return 'O'; }
    }

    public void getChildsLinkedList() {
        for (int c = 0; c < 7; c++) {
            if (this.freeRow[c] >= 0) {
                char[][] childGameGrid = new char[6][7];
                for (int r = 0; r < 6; r++) {
                    childGameGrid[r] = this.gameGrid[r].clone();
                }
                char childTurn                    = this.swapTurn();
                childGameGrid[this.freeRow[c]][c] = childTurn;
                int[] childFreeRow                = this.freeRow.clone();
                childFreeRow[c]--;
                Node childNode = new Node(childGameGrid, childFreeRow, childTurn, this.movesCounter + 1, this.remainingDepth - 1);
                this.childsLinkedList.addLast(childNode);
            }
        }
        return;
    }

    public Node dropPiece(int c) {
        char[][] newGameGrid = new char[6][7];
        for (int r = 0; r < 6; r++) {
            newGameGrid[r] = this.gameGrid[r].clone();
        }
        char newTurn                    = this.swapTurn();
        newGameGrid[this.freeRow[c]][c] = newTurn;
        int[] newFreeRow                = this.freeRow.clone();
        newFreeRow[c]--;
        Node newNode = new Node(newGameGrid, newFreeRow, newTurn, this.movesCounter + 1, this.remainingDepth - 1);
        return newNode;
    }

    public void printGameGrid() {
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 7; c++) {
                System.out.print(" " + this.gameGrid[r][c] + " " + "\t");
            }
            System.out.println();
        }
        for (int c = 0; c < 7; c++) { System.out.print("[" + c + "]" + "\t"); }
        System.out.println();
        return;
    }

    public void updateStats(char turn, double value) {
        if (value == 0.5)                            { this.winsCounter += value; }
        else if (this.turn == turn && value == +1.0) { this.winsCounter += value; }
        else if (this.turn != turn && value == -1.0) { this.winsCounter -= value; }
        this.playsCounter++;
        return; 
    }
}