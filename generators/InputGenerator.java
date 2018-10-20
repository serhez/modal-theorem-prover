import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class InputGenerator {

    public static final int MAX_LENGTH = 50;  // Maximum length of a formula
    public static final int N = 100;          // Number of formulas

    public static void main(String[] args) {

        String formulas = "";
        FormulaGenerator formulaGenerator = new FormulaGenerator();

        // Write an input file of 100 random formulas of a maximum length of 50 characters each
        for (int i=0; i < N; i++) {
            String formula = "";
            // Avoid propositions and negated propositions as full formulas
            while (formula.length() < 3) {
                formula = formulaGenerator.generate(MAX_LENGTH);
            }
            formulas += (formula + ";\n");
        }

        try {
            write(formulas);
        } catch (IOException e) {
            System.out.println("The output cannot be written.");
            return;
        }
    }

    static private void write(String string) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("input/input.txt"));
        writer.write(string);
        writer.close();
    }
}
