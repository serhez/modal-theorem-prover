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

    public void proveInputFile() throws IncompatibleFrameConditionsException {

        // Core variables
        ArrayList<Theorem> theorems;
        ArrayList<Integer> unrecognisedTheorems;
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
                write(message, "output/output.txt");
            } catch (IOException e) {
                System.out.print("Could not write warning to output file: ");
                System.out.println(e.getMessage());
                System.out.println("Running directory is " + System.getProperty("user.dir"));                return;
            }
            return;
        }

        // Parse
        Parser parser = new Parser();
        parser.parseInput(inputString);
        theorems = parser.getTheorems();
        unrecognisedTheorems = parser.getUnrecognisedTheorems();
        system = parser.getSystem();

        results += "\n--------  PARSING\n\n";
        if (unrecognisedTheorems.isEmpty()) {
            results += "All theorems are syntactically and grammatically correct.\n";
        }
        for (int i : unrecognisedTheorems) {
            results += "There are formulas in the theorem number " + (i+1) + " which have not been recognized.\n";
        }

        int n = theorems.size();
        Tableau tableau;

        // Prove
        results += "\n\n--------  PROVING\n\n";
        for (int i=0; i < n; i++) {
            if (!unrecognisedTheorems.contains(i)) {
                tableau = new Tableau(theorems.get(i), system, debugging);
                if (tableau.run()) {
                    results += "Theorem " + (i+1) + " is valid.\n";
                } else {
                    results += "Theorem " + (i+1) + " is not valid.\n";
                }
                System.out.println("Proven " + (i+1) + " of " + n + " theorems");
            }
        }

        try {
            write(results, "output/output.txt");
        } catch (IOException e) {
            System.out.print("The output could not be written: ");
            System.out.println(e.getMessage());
            System.out.println("Running directory is " + System.getProperty("user.dir"));
            return;
        }
    }

    // Returns a list of integers, where 1 represents validity and 0 represents invalidity
    // This method can be used to study validity rates and the time-space performance of the prover
    public ArrayList<Integer> proveRandomFormulas(int n, int size, int maxPropositions, ModalSystem system) throws InvalidNumberOfPropositionsException, UnrecognizableFormulaException, IncompatibleFrameConditionsException {
        ArrayList<Integer> results = new ArrayList<>();
        InputGenerator generator = new InputGenerator();
        ArrayList<String> formulas = generator.generateFormulas(n, size, maxPropositions);

        // Write formulas to the debugging file
        StringBuilder builder = new StringBuilder();
        for (String formula : formulas)
        {
            builder.append(formula);
            builder.append("\n");
        }
        try {
            write(builder.toString(), "Validity-Analysis/output/debugging.txt");
        } catch (IOException e) {
            System.out.print("The formulas could not be written to the debugging file: ");
            System.out.println(e.getMessage());
            System.out.println("Running directory is " + System.getProperty("user.dir"));
        }

        System.out.println("\n-- " + n + " formulas have been generated --\n");
        for (int i = 0; i < n; i++) {
            if (proveFormula(formulas.get(i), system)) {
                results.add(1);
            } else {
                results.add(0);
            }
            System.out.println("Proven " + (i+1) + " of " + n + " formulas");
        }
        return results;
    }

    public boolean proveFormula(String formulaString, ModalSystem system) throws UnrecognizableFormulaException, IncompatibleFrameConditionsException {
        Parser parser = new Parser();
        if (!parser.parseFormula(formulaString)) {
            throw new UnrecognizableFormulaException(formulaString);
        }

        Tableau tableau = new Tableau(parser.getTheorems().get(0), system, debugging);
        if (tableau.run()) {
            tableau = null; // To free memory // TODO: check this works with VisualVM; if not, delete
            System.gc();
            return true;    // Valid
        } else {
            tableau = null; // To free memory
            System.gc();
            return false;   // Invalid
        }
    }

    private String readInputFile() throws IOException {
        String inputPath = "input/input.txt";
        return (Files.lines(Paths.get(inputPath), StandardCharsets.UTF_8)).collect(Collectors.joining());
    }

    private void write(String string, String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
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
}
