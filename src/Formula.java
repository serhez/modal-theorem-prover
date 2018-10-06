import java.util.ArrayList;

public class Formula {

    private String formulaString;
    private final boolean isAxiom;
    private final ArrayList<Formula> subformulas;
    private Operator operator;

    public Formula(String formulaString, boolean isAxiom) {
        this.formulaString = formulaString;
        this.isAxiom = isAxiom; // TODO: I DON'T LIKE THIS
        this.subformulas = new ArrayList<Formula>();
        preprocess();
        parse();
    }

    // TODO: possibly convert all ands, conditions and biconditions into normal form
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

    // Analise the formula and return false if itself or any of its subformulas is not valid
    public boolean parse() {

        // Find the main operator of the formula and report any invalid syntactical structure on the current formula
        if (!analise()) {
            return false;
        }

        // In the case that this formula is a proposition
        if (operator == Operator.NONE) {
            if (!validProposition()) {
                return false;
            }
        }

        // In the case this formula is a negated proposition
        if (operator == Operator.NOT) {
            if (!validProposition(subformulas.get(0).formulaString)) {
                return false;
            }
        }

        for (Formula subformula : subformulas) {
            if (!subformula.parse()) {
                return false;
            }
        }

        return true;
    }

    // Check if a proposition is valid
    private boolean validProposition(String formula) {
        for (int i = 0; i < formula.length(); i++) {

        }
        return true;
    }

    // Determine the core operator and its subformulas, as well as returning false if found any invalid structures
    private boolean analise() {

        int end = formulaString.length();

        // []
        if (formulaString.substring(0,2) == "[]") {
            operator = Operator.NECESSARILY;
            subformulas.add(new Formula(formulaString.substring(2, end), true));
        }

        // <>
        else if (formulaString.substring(0,2) == "<>") {
            operator = Operator.POSSIBLY;
            subformulas.add(new Formula(formulaString.substring(2, end), true));
        }

        // Binary operators
        else if (formulaString.charAt(0) == '(') {
            // Check for non-matching parenthesis
            if (formulaString.charAt(end-1) != ')') {
                return false;
            }
            operator = findBinaryOperator(formulaString);
            if (operator == null) {
                return false;
            }
        }

        // ~
        else if (formulaString.charAt(0) == '~') {
            Formula negatedFormula = new Formula(formulaString.substring(1, end), true);
            negatedFormula.operator = findBinaryOperator(negatedFormula.formulaString);
            if (negatedOperator == Operator.NONE) {
                negatedFormula.operator = Operator.NONE;
            } else if (negatedOperator == Operator.NOTAND) {

            } else if (negatedOperator == Operator.NOTOR) {

            } else if (negatedOperator == Operator.NOTCONDITION) {

            } else if (negatedOperator == Operator.NOTBICONDITION) {

            } else if (negatedOperator == Operator.NOTNECESSARILY) {

            } else if (negatedOperator == Operator.NOTPOSSIBLY) {

            }
            subformulas.add(negatedFormula);
        }

        return true;
    }

    // Negate this formula
    public void negate() {

        int end = formulaString.length();

        /*
        A reduction of repetition in the statements modifying formulaString would increase code compactness and
        readability, but would incorporate more logic checks, slowing down the program. Hence I have compromised
        on the first to benefit the latter.
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

    public ArrayList<Formula> getSubformulas() {
        return subformulas;
    }


}
