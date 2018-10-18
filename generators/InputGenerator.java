import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class InputGenerator {

    public static void main(String[] args) {

        String formulas = "";
        FormulaGenerator formulaGenerator = new FormulaGenerator();

        // Write an input file of 100 random formulas of a maximum length of 50 characters each
        for (int i=0; i < 100; i++) {
            String formula = "";
            // Avoid propositions and negated propositions as full formulas
            while (formula.length() < 3) {
                formula = formulaGenerator.generate(50);
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
        BufferedWriter writer = new BufferedWriter(new FileWriter("input/input.txt", true));
        writer.write(string);
        writer.close();
    }
}
