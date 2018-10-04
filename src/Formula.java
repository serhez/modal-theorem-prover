import java.util.ArrayList;
import java.util.Arrays;

public class Formula {

    private String formulaString;
    private final boolean isAxiom;  // Subformulas are declared as axioms, since they do not need to be
    private final ArrayList<Formula> subformulas;
    private Operator operator;
    private boolean isValid;  // Initialised by the analise() method


    public Formula(String formulaString, boolean isAxiom) {
        this.formulaString = formulaString;
        this.isAxiom = isAxiom;
        this.subformulas = new ArrayList<Formula>();
        preprocess();
        analise();
    }

    // Preprocesses the formulaString and eliminates all vacuous elements
    public void preprocess() {

        int end = formulaString.length();

        // Negate the formula if it is not an axiom
        if (!isAxiom) {
            negate();
        }

        // Eliminate all spaces, tabs and new lines
        formulaString.replaceAll(" ","");
        formulaString.replaceAll("\n","");
        formulaString.replaceAll("\t","");

        // Eliminate double negations
        for (int i = 0; i < formulaString.length(); i++) {
            if (formulaString.length() > i+1) {
                break;
            }
            if ((formulaString.charAt(i) == '~') && (formulaString.charAt(i) == formulaString.charAt(i+1))) {
                formulaString = formulaString.substring(0, i) + formulaString.substring(i+1, end);
                formulaString = formulaString.substring(0, i) + formulaString.substring(i+1, end);
            }
        }
    }

    public void analise() {

        int end = formulaString.length();

        // Find the main operator of the formula

        // []
        if (formulaString.substring(0,2) == "[]") {
            operator = Operator.NECESSARILY;
            subformulas.add(new Formula(formulaString.substring(2, end), true));
        }

        // <>
        else if (formulaString.substring(0,2) == "<>") {
            operator = Operator.POSSIBLY;
        }

        isValid = true;
    }

    // Negate this formula
    public void negate() {

        int end = formulaString.length();

        /*
        A reduction of repetition here (in the statements modifying formulaString) would increase code compactness
        and readability, but would incorporate more logic checks, slowing down the program. Hence I have compromised on
        the first to benefit the latter.
         */
        if (operator == Operator.NONE) {
            formulaString = "~" + formulaString;
            operator = Operator.NOT;
        } else if (operator == Operator.AND) {
            formulaString = "~" + formulaString;
            operator = Operator.NOTAND;
        } else if (operator == Operator.OR) {
            formulaString = "~" + formulaString;
            operator = Operator.NOTOR;
        } else if (operator == Operator.CONDITION) {
            formulaString = "~" + formulaString;
            operator = Operator.NOTCONDITION;
        } else if (operator == Operator.BICONDITION) {
            formulaString = "~" + formulaString;
            operator = Operator.NOTBICONDITION;
        } else if (operator == Operator.NECESSARILY) {
            formulaString = "~" + formulaString;
            operator = Operator.NOTNECESSARILY;
        } else if (operator == Operator.POSSIBLY) {
            formulaString = "~" + formulaString;
            operator = Operator.NOTPOSSIBLY;
        } else if (operator == Operator.NOT) {
            formulaString = formulaString.substring(1, end);
            operator = Operator.NONE;
        } else if (operator == Operator.NOTAND) {
            formulaString = formulaString.substring(1, end);
            operator = Operator.AND;
        } else if (operator == Operator.NOTOR) {
            formulaString = formulaString.substring(1, end);
            operator = Operator.OR;
        } else if (operator == Operator.NOTCONDITION) {
            formulaString = formulaString.substring(1, end);
            operator = Operator.CONDITION;
        } else if (operator == Operator.NOTBICONDITION) {
            formulaString = formulaString.substring(1, end);
            operator = Operator.BICONDITION;
        } else if (operator == Operator.NOTNECESSARILY) {
            formulaString = formulaString.substring(1, end);
            operator = Operator.NECESSARILY;
        } else if (operator == Operator.NOTPOSSIBLY) {
            formulaString = formulaString.substring(1, end);
            operator = Operator.POSSIBLY;
        }
    }

    public boolean isAxiom() {
        return isAxiom;
    }

    public boolean isValid() {
        return isValid;
    }

    public ArrayList<Formula> getSubformulas() {
        return subformulas;
    }


}
