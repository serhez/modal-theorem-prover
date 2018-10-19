import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TheoremProver {

    public static void main(String[] args) {

        // Core variables
        ArrayList<Theorem> theorems;
        ArrayList<Integer> invalidTheorems;
        String inputString;
        String results = "";

        try {
            inputString = read();
        } catch (IOException e) {
            System.out.println("The input could not be read.");
            return;
        }

        if (inputIsEmpty(inputString)) {
            String message = "The input file cannot be empty.";
            try {
                write(message);
            } catch (IOException e) {
                System.out.println("Could not warn about empty input.");
                return;
            }
            return;
        }

        // Parse
        Parser parser = new Parser(inputString);
        parser.parse();
        theorems = parser.getTheorems();
        invalidTheorems = parser.getInvalidTheorems();

        results += "\n----------PARSING----------\n\n";
        if (invalidTheorems.isEmpty()) {
            results += "All theorems are syntactically and grammatically correct.\n";
        }
        for (int i : invalidTheorems) {
            results += "There are formulas in the theorem number " + (i+1) + " which have not been recognized.\n";
        }

        // Debug
        //printTheorems(validTheorems);

        // Prove
        results += "\n\n----------PROVING----------\n\n";
        ArrayList<Tableau> tableaus = new ArrayList<>();
        for (int i=0; i < theorems.size(); i++) {
            if (!invalidTheorems.contains(i)) {
                Tableau tableau = new Tableau(theorems.get(i));
                tableaus.add(tableau);
                if (tableau.run()) {
                    results += "Theorem " + (i+1) + " is valid.\n";
                } else {
                    results += "Theorem " + (i+1) + " is not valid.\n";
                }
            }
        }

        try {
            write(results);
        } catch (IOException e) {
            System.out.println("The output could not be written.");
            return;
        }

        // Debug
        //printTableaus(tableaus);
    }

    static private String read() throws IOException {
        String inputPath = "input/input.txt";
        return (Files.lines(Paths.get(inputPath), StandardCharsets.UTF_8)).collect(Collectors.joining());
    }

    static private void write(String string) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("output/output.txt"));
        writer.write(string);
        writer.close();
    }

    static private boolean inputIsEmpty(String input) {

        // ""
        if (input.length() == 0) {
            return true;
        }

        // Only semi-colons, commas, spaces, tabs or new lines
        for (int i=0; i<input.length(); i++) {
            if (input.charAt(i) != ' ' && input.charAt(i) != '\t' && input.charAt(i) != '\n' && input.charAt(i) != ';' && input.charAt(i) != ',') {
                return false;
            }
        }

        return true;
    }

    // Debugging methods

    private static void printTableaus(ArrayList<Tableau> tableaus) {
        for (int i = 0; i < tableaus.size(); i++) {
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("*********************************************************************");
            System.out.println("\t\t\t\t\t\t\t TABLEAU " + (i+1));
            System.out.println("*********************************************************************");
            tableaus.get(i).print();
        }
    }

    private static void printTheorems(ArrayList<Theorem> theorems) {
        for (int i = 0; i < theorems.size(); i++) {
            System.out.println();
            System.out.println();
            System.out.println("----------------------------------------------------------------------");
            System.out.println("----------------------------------------------------------------------");
            System.out.println("\t\t\t\t\t\t\t  THEOREM " + (i+1));
            System.out.println("----------------------------------------------------------------------");
            System.out.println("----------------------------------------------------------------------");
            for (Formula formula : theorems.get(i).getFormulas()) {
                printFormula(formula, 0);
            }
        }
    }

    private static void printFormula(Formula formula, int indentation) {
        System.out.println();
        for (int i = 0; i < indentation; i++) {
            System.out.print(" ");
        }
        System.out.println(formula.getString() + "  ;  Operator = " + formula.getOperator() + "  ;  Op. Index = " + formula.getOperatorIndex());
        for (Formula subformula : formula.getSubformulas()) {
            printFormula(subformula, indentation+2);
        }
    }

}
