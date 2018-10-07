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
            Formula currentFormula;
            if (i == formulaStrings.size() - 1) {
                currentFormula = new Formula(formulaStrings.get(i));
                currentFormula.negate();
            } else {
                currentFormula = new Formula(formulaStrings.get(i));
            }
            currentFormula.preprocess();
            if (!currentFormula.parse()) {
                System.out.println("The formula " + currentFormula.getFormulaString() + " has not been recognized");
                validInput = false;
            }
            formulas.add(currentFormula);
        }

        return validInput;
    }

    public ArrayList<Formula> getFormulas() {
        return formulas;
    }
}
