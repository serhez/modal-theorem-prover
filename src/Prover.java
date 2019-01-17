import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Prover {

    boolean debuggingMode;   // Prints to the console the state of the frames at each step of the tableau
    boolean protectedMode;   // Stops tableaux if they reach a limit number of frames, worlds or transitions

    public Prover(boolean debuggingMode, boolean protectedMode) {
        this.debuggingMode = debuggingMode;
        this.protectedMode = protectedMode;
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
                System.out.println("Running directory is " + System.getProperty("user.dir"));
                return;
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
                tableau = new Tableau(this, theorems.get(i), system);
                Boolean validity = tableau.run();
                if (validity == null) {  // The tableau aborted in protected mode
                    results += "The proving process for theorem " + (i+1) + " was aborted in protected mode.\n";
                } else if (validity.booleanValue()) {
                    results += "Theorem " + (i+1) + " is valid.\n";
                } else {
                    results += "Theorem " + (i+1) + " is not valid.\n";
                }
                System.out.println("Done " + (i+1) + " of " + n + " theorems");
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
    // It employs the protected mode of the prover, due to generally being used with large formula sizes
    public ArrayList<Integer> proveRandomFormulas(int n, int size, int maxPropositions, ModalSystem system, boolean debugging) throws InvalidNumberOfPropositionsException, UnrecognizableFormulaException, IncompatibleFrameConditionsException {

        ArrayList<Integer> results = new ArrayList<>();
        InputGenerator generator = new InputGenerator();
        ArrayList<String> formulas = generator.generateFormulas(n, size, maxPropositions);
        System.out.println("\n-- " + n + " formulas have been generated --\n");

        int abortedFormulas = 0;

        if (debugging) {
            // Write formulas to the debugging file
            StringBuilder builder = new StringBuilder();
            for (String formula : formulas) {
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
        }

        for (int i = 0; i < n; i++) {
            Boolean validity = proveFormula(formulas.get(i), system);
            if (validity == null) {
                abortedFormulas++;
                System.out.println("Aborted formula #" + (i+1));
                continue;
            } else if (validity.booleanValue()) {
                results.add(1);
            } else {
                results.add(0);
            }
            System.out.println("Analysed " + (i+1) + " of " + n + " formulas");
        }

        System.out.println("\n\n---------- RESULTS ----------\n");
        System.out.println("# formulas aborted = " + abortedFormulas);
        System.out.println("# formulas proven = " + (n - abortedFormulas));
        System.out.println("% formulas proven = " + ((((double)(n - abortedFormulas))/n)*100) + "%");
        System.out.println("\n-----------------------------\n");
        return results;
    }

    public Boolean proveFormula(String formulaString, ModalSystem system) throws UnrecognizableFormulaException, IncompatibleFrameConditionsException {
        Parser parser = new Parser();
        if (!parser.parseFormula(formulaString)) {
            throw new UnrecognizableFormulaException(formulaString);
        }

        Tableau tableau = new Tableau(this, parser.getTheorems().get(0), system);
        Boolean validity = tableau.run();
        // TODO: "return tableau.run();" instead. This was only done to be able to do system.gc()
        if (validity == null) {
            return null;
        } else if (validity.booleanValue()) {
            tableau = null; // To free memory // TODO: check this works with VisualVM; if not, delete
            System.gc();
            return Boolean.TRUE;    // Valid
        } else {
            tableau = null; // To free memory
            System.gc();
            return Boolean.FALSE;   // Invalid
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

    public boolean isDebugging() {
        return debuggingMode;
    }

    public boolean isProtected() {
        return protectedMode;
    }
}
