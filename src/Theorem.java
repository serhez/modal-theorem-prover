import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Theorem {

    private final String theoremString;
    private final ArrayList<Formula> formulas;

    public Theorem(String theoremString) {
        this.theoremString = theoremString;
        this.formulas = new ArrayList<>();
    }

    // Populate the "formulas" variable with Formula objects extracted from the theoremString, and return false if any formula is invalid
    public boolean parse() {

        boolean validInput = true; // This variable allows the program to keep looking for invalid formulas once one has been found, for ease of debugging

        ArrayList<String> formulaStrings = new ArrayList(Arrays.asList(theoremString.split(",", 0)));
        for (int i = 0; i < formulaStrings.size(); i++) {
            Formula currentFormula = new Formula(formulaStrings.get(i));
            if (i == formulaStrings.size()-1) {
                currentFormula.negate();  // Negate the last formula of the theorem
            }
            currentFormula.preprocess();
            if (!currentFormula.parse()) {
                String message = "The formula " + currentFormula.getString() + " has not been recognized";
                try {
                    write(message);
                } catch (IOException e) {
                    System.out.println("Could not warn about unrecognized formulas.");
                }
                validInput = false;
            }
            formulas.add(currentFormula);
        }

        return validInput;
    }

    private void write(String string) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("output/output.txt"));
        writer.write(string);
        writer.close();
    }

    public ArrayList<Formula> getFormulas() {
        return formulas;
    }
}
