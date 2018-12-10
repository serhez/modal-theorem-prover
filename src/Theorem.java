import java.util.Arrays;
import java.util.LinkedList;

public class Theorem {

    private final String theoremString;
    private final LinkedList<Formula> formulas;

    public Theorem(String theoremString) {
        this.theoremString = theoremString;
        this.formulas = new LinkedList<>();
    }

    // Populate the "formulas" variable with Formula objects extracted from the theoremString, and return false if any formula is unrecognised
    public boolean parse() {

        boolean validInput = true; // Allows the program to keep looking for unrecognised formulas once one has been found, for ease of debugging

        LinkedList<String> formulaStrings = new LinkedList(Arrays.asList(theoremString.split(",", 0)));
        for (int i = 0; i < formulaStrings.size(); i++) {
            Formula currentFormula = new Formula(formulaStrings.get(i));
            if (i == formulaStrings.size()-1) {
                currentFormula.negate();  // Negate the last formula of the theorem
            }
            currentFormula.preprocess();
            if (!currentFormula.parse()) {
                validInput = false;
            }
            formulas.add(currentFormula);
        }

        return validInput;
    }

    public LinkedList<Formula> getFormulas() {
        return formulas;
    }
}
