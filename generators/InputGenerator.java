import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class InputGenerator {

    public final int maxLength;  // Maximum length of a formula
    public final int n;          // Number of formulas

    public InputGenerator(int n, int maxLength) {
        this.n = n;
        this.maxLength = maxLength;
    }

    public String generateInputFile() {

        String inputString = generateInput();

        try {
            write(inputString);
        } catch (IOException e) {
            System.out.println("The input formulas could not be written.");
            return null;
        }

        return inputString;
    }

    public void generateInputFileAndMolleFile() {

        String inputString = generateInputFile();
        String molleInputString = translateInputToMolle(inputString);

        try {
            writeToMolle(molleInputString);
        } catch (IOException e) {
            System.out.println("The input formulas could not be written.");
            return;
        }
    }

    // Generates an input for the Theorem Prover
    private String generateInput() {

        String inputString = "";
        FormulaGenerator formulaGenerator = new FormulaGenerator();

        for (int i=0; i < n; i++) {
            String formula = "";
            // Avoid propositions and negated propositions as full formulas
            while (formula.length() < 3) {
                formula = formulaGenerator.generate(maxLength);
            }
            inputString += (formula + ";\n");
        }

        return inputString;
    }

    public ArrayList<String> generateFormulas() {
        String inputString = generateInput();
        ArrayList<String> formulas = new ArrayList(Arrays.asList(inputString.split(";\n", 0)));
        return formulas;
    }

    private void write(String string) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("input/input.txt"));
        writer.write(string);
        writer.close();
    }

    private void writeToMolle(String string) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("input/inputMolle.txt"));
        writer.write(string);
        writer.close();
    }

    private String translateInputToMolle(String inputString) {

        ArrayList<String> formulas = new ArrayList(Arrays.asList(inputString.split(";\n", 0)));
        String molleInputString = "";
        for (String formula : formulas) {
            molleInputString += translateFormulaToMolle(formula) + "\n";
        }

        return molleInputString;
    }

    // Translates the input string to Molle syntax and writes it to a file called "inputMolle.txt"
    public String translateFormulaToMolle(String string) {

        string = string.replaceAll("p", "P");
        string = string.replaceAll("q", "Q");
        string = string.replaceAll("r", "R");
        string = string.replaceAll("-", "=");

        return string;
    }
}
