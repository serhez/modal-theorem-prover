import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Prover {

    private boolean debuggingMode;    // Prints to the console the state of the frames at each step of the tableau
    private boolean protectedMode;    // Stops tableaux if timeout limit is surpassed
    private int timeout = 3000;       // 3 seconds by default (only used in protected mode)

    public Prover() {
        this.debuggingMode = false;
        this.protectedMode = false;
    }

    public class Results {
        private ArrayList<Integer> results;
        private ArrayList<Long> times;
        private double abortionRate;
        private double seconds;

        public Results(ArrayList<Integer> results, ArrayList<Long> times, double abortionRate) {
            this.results = results;
            this.times = times;
            this.abortionRate = abortionRate;
            this.seconds = (double)timeout/1000;
        }

        public ArrayList<Integer> getResults() {
            return results;
        }
        public ArrayList<Long> getTimes() {
            return times;
        }
        public double getAbortionRate() {
            return abortionRate;
        }
        public double getTimeoutLimit() {
            return seconds;
        }
    }

    public void proveInputFile() throws IncompatibleFrameConditionsException {

        // Core variables
        ArrayList<FormulaArray> formulaArrays;
        ArrayList<Integer> unrecognisedTheorems;
        ModalLogic logic;
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
        formulaArrays = parser.getFormulaArrays();
        unrecognisedTheorems = parser.getUnrecognisedTheorems();
        logic = parser.getLogic();

        results += "\n--------  PARSING\n\n";
        if (unrecognisedTheorems.isEmpty()) {
            results += "All formulas are syntactically and grammatically correct.\n";
        }
        for (int i : unrecognisedTheorems) {
            results += "There are formulas in the list of formulas number " + (i+1) + " which have not been recognized.\n";
        }

        int n = formulaArrays.size();
        Tableau tableau;

        // Prove
        results += "\n\n--------  PROVING\n\n";
        for (int i=0; i < n; i++) {
            if (!unrecognisedTheorems.contains(i)) {
                tableau = new Tableau(this, formulaArrays.get(i), logic);
                Boolean validity = tableau.run();
                if (validity == null) {  // The tableau aborted in protected mode
                    results += "The proving process for theorem " + (i+1) + " was aborted in protected mode.\n";
                } else if (validity.booleanValue()) {
                    results += "Formula " + (i+1) + " is valid.\n";
                } else {
                    results += "Formula " + (i+1) + " is not valid.\n";
                }
                System.out.println("Done " + (i+1) + " of " + n + " formulas");
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
    public Results proveRandomFormulas(int n, int size, int maxPropositions, ModalLogic logic, boolean debugging) throws InvalidNumberOfPropositionsException, UnrecognizableFormulaException, IncompatibleFrameConditionsException {

        long start, end;
        ArrayList<Long> times = new ArrayList<>();
        ArrayList<Integer> proven = new ArrayList<>();
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
            start = System.currentTimeMillis();
            Boolean validity = proveFormula(formulas.get(i), logic);
            end = System.currentTimeMillis();
            if (validity == null) {
                abortedFormulas++;
//                System.out.println("Aborted formula #" + (i+1));  // Uncomment for feedback; comment for better performance
                continue;
            }
            times.add(end-start);
            if (validity.booleanValue()) {
                proven.add(1);
            } else {
                proven.add(0);
            }
//            System.out.println("Analysed " + (i+1) + " of " + n + " formulas");  // Uncomment for feedback; comment for better performance
        }

        double abortionRate = ((1-(((double)(n - abortedFormulas))/n))*100);
        System.out.println("\n\n---------- RESULTS ----------\n");
        System.out.println("# formulas aborted = " + abortedFormulas);
        System.out.println("# formulas proven = " + (n - abortedFormulas));
        System.out.println("% formulas aborted = " + abortionRate + "%");
        System.out.println("\n-----------------------------\n");

        Results results = new Results(proven, times, abortionRate);
        return results;
    }

    public Boolean proveFormula(String formulaString, ModalLogic logic) throws UnrecognizableFormulaException, IncompatibleFrameConditionsException {
        Parser parser = new Parser();
        if (!parser.parseFormula(formulaString)) {
            throw new UnrecognizableFormulaException(formulaString);
        }

        Tableau tableau = new Tableau(this, parser.getFormulaArrays().get(0), logic);
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
    public int getTimeout() {
        return timeout;
    }
    public void enableProtectedMode(int timeout) throws NegativeTimeoutException {
        if (timeout < 0) {
            throw new NegativeTimeoutException();
        }
        protectedMode = true;
        this.timeout = timeout;
    }
    public void enableDebuggingMode() {
        debuggingMode = true;
    }
    public void disableProtectedMode() {
        protectedMode = false;
    }
    public void disableDebuggingMode() {
        debuggingMode = true;
    }
}
