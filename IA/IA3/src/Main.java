import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

/*
in: um ficheiro .csv
out: uma decision tree

? os headers serao novamente necessarios ?
ra = dc, onde ra e o numero de ramos que saiem de um dado atributo e dc e o numero de classes diferentes do atributo
para executar a funcao da entropia e necessario calcular as classes diferentes
para calcular as classes diferentes podemos criar uma funcao
ao inves de pensar em classes como booleans, ranges... pensar como strings. Strings Diferentes = Classes Diferentes, Strings Iguais = Classes Iguais

um node tera:
String label        -> nome do atributo que lhe deu origem
node[] childs       -> array de nos que sao filhos do mesmo
a arvore vai sendo percorrida ate chegar a node em que childs == null nesse caso nome deve ser igual ao valor (ex: Yes)

iremos correr o ID3 sobre a tabela previamente criada

testar se a parte da falta de exemplos esta correta!

*/

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
        /*
        System.out.println("dataHolder: ");
        for (int i = 0; i < dataHolder.length; i++) {
            for (int j = 0; j < dataHolder[i].length; j++) {
                System.out.print(dataHolder[i][j] + "\t");
            }
            System.out.println();
        }
        */
        Node rootNode = DecisionTree.ID3(dataHolder);
        System.out.println(rootNode.label);
        return;
    }
}