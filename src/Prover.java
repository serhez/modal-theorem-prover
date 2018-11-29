import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Prover {

    boolean debugging;

    public Prover(boolean debugging) {
        this.debugging = debugging;
    }

    public void proveInputFile() {

        // Core variables
        ArrayList<Theorem> theorems;
        ArrayList<Integer> invalidTheorems;
        ModalSystem system;
        String inputString;
        String results = "";

        try {
            inputString = readInputFile();
        } catch (IOException e) {
            System.out.println("The input could not be read.");
            return;
        }

        if (inputIsEmpty(inputString)) {
            String message = "The input file cannot be empty.";
            try {
                writeOutputFile(message);
            } catch (IOException e) {
                System.out.println("Could not warn about empty input.");
                return;
            }
            return;
        }

        // Parse
        Parser parser = new Parser();
        parser.parseInput(inputString);
        theorems = parser.getTheorems();
        invalidTheorems = parser.getInvalidTheorems();
        system = parser.getSystem();

        results += "\n--------  PARSING\n\n";
        if (invalidTheorems.isEmpty()) {
            results += "All theorems are syntactically and grammatically correct.\n";
        }
        for (int i : invalidTheorems) {
            results += "There are formulas in the theorem number " + (i+1) + " which have not been recognized.\n";
        }

        // Prove
        results += "\n\n--------  PROVING\n\n";
        ArrayList<Tableau> tableaux = new ArrayList<>();
        for (int i=0; i < theorems.size(); i++) {
            if (!invalidTheorems.contains(i)) {
                Tableau tableau = new Tableau(theorems.get(i), system, debugging);
                tableaux.add(tableau);
                if (tableau.run()) {
                    results += "Theorem " + (i+1) + " is valid.\n";
                } else {
                    results += "Theorem " + (i+1) + " is not valid.\n";
                }
            }
        }

        try {
            writeOutputFile(results);
        } catch (IOException e) {
            System.out.println("The output could not be written.");
            return;
        }
    }

    // Returns a list of integers, where 1 represents validity and 0 represents invalidity
    // This method can be used to study validity rates and the time and space performance of the prover
    public ArrayList<Integer> proveRandomFormulas(int n, int size, int maxPropositions, ModalSystem system) throws InvalidNumberOfPropositionsException, UnrecognizableFormulaException {
        ArrayList<Integer> results = new ArrayList<>();
        InputGenerator generator = new InputGenerator();
        ArrayList<String> formulas = generator.generateFormulas(n, size, maxPropositions);
        for (String formula : formulas) {
            if (proveFormula(formula, system)) {
                results.add(1);
            } else {
                results.add(0);
            }
        }
        return results;
    }

    public boolean proveFormula(String formulaString, ModalSystem system) throws UnrecognizableFormulaException {
        Parser parser = new Parser();
        if (!parser.parseFormula(formulaString)) {
            throw new UnrecognizableFormulaException(formulaString);
        }

        Tableau tableau = new Tableau(parser.getTheorems().get(0), system, debugging);
        if (tableau.run()) {
            return true;   // Valid
        } else {
            return false;  // Invalid
        }
    }

    private String readInputFile() throws IOException {
        String inputPath = "input/input.txt";
        return (Files.lines(Paths.get(inputPath), StandardCharsets.UTF_8)).collect(Collectors.joining());
    }

    public String readOutputFile() throws IOException {
        String outputPath = "output/output.txt";
        return (Files.lines(Paths.get(outputPath), StandardCharsets.UTF_8)).collect(Collectors.joining());
    }

    private void writeOutputFile(String string) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("output/output.txt"));
        writer.write(string);
        writer.close();
    }

    private boolean inputIsEmpty(String input) {

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

    private void printTableaux(ArrayList<Tableau> tableaux) {
        for (int i = 0; i < tableaux.size(); i++) {
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("*********************************************************************");
            System.out.println("\t\t\t\t\t\t\t TABLEAU " + (i+1));
            System.out.println("*********************************************************************");
            tableaux.get(i).print();
        }
    }

    private void printTheorems(ArrayList<Theorem> theorems) {
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

    private void printFormula(Formula formula, int indentation) {
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
