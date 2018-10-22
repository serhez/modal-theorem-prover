import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
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

        String formulas = "";
        FormulaGenerator formulaGenerator = new FormulaGenerator();

        // Write an input file of 100 random formulas of a maximum length of 50 characters each
        for (int i=0; i < n; i++) {
            String formula = "";
            // Avoid propositions and negated propositions as full formulas
            while (formula.length() < 3) {
                formula = formulaGenerator.generate(maxLength);
            }
            formulas += (formula + ";\n");
        }

        try {
            write(formulas);
        } catch (IOException e) {
            System.out.println("The output cannot be written.");
            return null;
        }

        return formulas;
    }


    // Generates an input for the Theorem Prover, as well as converting that input to Molle syntax and returning it
    public ArrayList<String> generateInputFormulas() {

        String string = generateInputFile();

        ArrayList<String> formulas = new ArrayList(Arrays.asList(string.split(";", 0)));

        return formulas;
    }

    private void write(String string) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("input/input.txt"));
        writer.write(string);
        writer.close();
    }

    // Translates the input string to Molle syntax and writes it to a file called "inputMolle.txt"
    private String translateToMolle(String string) {

        string = string.replaceAll("p", "P");
        string = string.replaceAll("q", "Q");
        string = string.replaceAll("r", "R");
        string = string.replaceAll("-", "=");

        return string;
    }
}
