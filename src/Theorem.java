import java.util.ArrayList;
import java.util.Arrays;

public class Theorem {

    private final String theoremString;
    private final ArrayList<Formula> formulas;

    public Theorem(String theoremString) {
        this.theoremString = theoremString;
        this.formulas = new ArrayList<Formula>();
        parseFormulas();
    }

    // Populate the "formulas" variable with Formula objects extracted from the theoremString
    public void parseFormulas() {

        ArrayList<String> formulaStrings = new ArrayList(Arrays.asList(theoremString.split(",", 0)));
        for (int i = 0; i < formulaStrings.size(); i++) {
            if (i == formulaStrings.size() - 1) {
                formulas.add(new Formula(formulaStrings.get(i), false));
            } else {
                formulas.add(new Formula(formulaStrings.get(i), true));
            }
        }
    }

    public ArrayList<Formula> getFormulas() {
        return formulas;
    }
}
