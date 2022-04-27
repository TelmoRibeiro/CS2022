import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static boolean isGameOver(Node evaluatingNode) {
        return evaluatingNode.utility == +512 || evaluatingNode.utility == -512 || evaluatingNode.movesCounter == 42;
    }

    public static void main(String[] args) {
        Scanner inputScanner = new Scanner(System.in);
        char[][] gameGrid = new char[6][7];
        for (int r = 0; r < 6; r++) {
            Arrays.fill(gameGrid[r], '-');
        }
        int[] freeRow = new int[7];
        Arrays.fill(freeRow, 5);
        
        System.out.println("Escolha o algoritmo: | 1 para minimax | 2 para alfabeta | 3 para MCTS |");
        int algorithmOption = inputScanner.nextInt(); 
        while (algorithmOption < 1 || algorithmOption > 3) {
            System.out.println("Escolha o algoritmo: | 1 para minimax | 2 para alfabeta | 3 para MCTS |");
            algorithmOption = inputScanner.nextInt();
        }
        System.out.println("Escolha o primeiro jogador: | 1 para humano | 2 para IA |");
        int firstOption = inputScanner.nextInt();
        while (firstOption < 1 || firstOption > 2) {
            System.out.println("Escolha o primeiro jogador: | 1 para humano | 2 para IA |");
            firstOption = inputScanner.nextInt();
        }
        if (firstOption == 1) {
            char turn = 'X'; // lastTurn == 'X' so first turn must be 'O'
            Node currentNode = new Node(gameGrid, freeRow, turn, 0, 8);
            while (!isGameOver(currentNode)) {
                System.out.println("Please insert a column:"); 
                int c = inputScanner.nextInt();
                while ( c < 0 || c > 6 || currentNode.freeRow[c] == -1) {
                    System.out.println("Please insert a column:"); 
                    c = inputScanner.nextInt();
                }
                currentNode = currentNode.dropPiece(c);
                System.out.println("Your Move:");
                currentNode.printGameGrid();
                if (isGameOver(currentNode)) { 
                    System.out.print("Player Won!");
                    return;
                }

                currentNode.remainingDepth = 8;
                if (algorithmOption == 1) { currentNode = Solve.miniMax(currentNode); }
                if (algorithmOption == 2) { currentNode = Solve.alphaBeta(currentNode); }
                if (algorithmOption == 3) { currentNode = Solve.MCTS(currentNode); }
                System.out.println("AI Move:");
                currentNode.printGameGrid();
            }
            System.out.println("AI Won!");
            return;
        }
        if (firstOption == 2) {
            char turn = 'O'; // lastTurn == 'O' so first turn must be 'X'
            Node currentNode = new Node(gameGrid, freeRow, turn, 0, 8);
            while (!isGameOver(currentNode)) {
                currentNode.remainingDepth = 8;
                if (algorithmOption == 1) { currentNode = Solve.miniMax(currentNode); }
                if (algorithmOption == 2) { currentNode = Solve.alphaBeta(currentNode); }
                if (algorithmOption == 3) { currentNode = Solve.MCTS(currentNode); }
                System.out.println("AI Move:");
                currentNode.printGameGrid();
                if (isGameOver(currentNode)) {
                    System.out.println("AI Won!");
                    return; 
                }
                System.out.println("Please insert a column:"); 
                int c = inputScanner.nextInt();
                while ( c < 0 || c > 6 || currentNode.freeRow[c] == -1) {
                    System.out.println("Please insert a column:"); 
                    c = inputScanner.nextInt();
                }
                currentNode = currentNode.dropPiece(c);
                System.out.println("Your Move:");
                currentNode.printGameGrid();
            }
            System.out.println("Player Won!");
            return;
        } 
    }
}