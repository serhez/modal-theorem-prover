import java.util.ArrayList;

public class Formula {

    private String formulaString;
    private final ArrayList<Formula> subformulas;
    private Operator operator;
    private int operatorIndex; // In case of a negated operator, the index will indicate the location of the operator, not of the negation

    public Formula(String formulaString) {
        this.formulaString = formulaString;
        this.subformulas = new ArrayList<>();
    }

    // TODO: possibly convert all ands, conditions and biconditions into normal form
    // TODO: move all negations forward (<>~p should be ~<>p)
    // Preprocesses the formulaString and eliminates all vacuous elements
    public void preprocess() {

        // Eliminate all spaces, tabs and new lines
        formulaString = formulaString.replaceAll(" ","");
        formulaString = formulaString.replaceAll("\t","");
        formulaString = formulaString.replaceAll("\n","");

        // Eliminate double negations
        eliminateDoubleNegations();
    }

    private void eliminateDoubleNegations() {
        for (int i = 0; (i+1) < formulaString.length(); i++) {
            if ((formulaString.charAt(i) == '~') && (formulaString.charAt(i) == formulaString.charAt(i+1))) {
                formulaString = formulaString.substring(0, i) + formulaString.substring(i+2, formulaString.length());
                eliminateDoubleNegations();
                break;
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
            if (!validProposition(formulaString)) {
                return false;
            }
            return true;
        }

        for (Formula subformula : subformulas) {
            subformula.preprocess();
            if (!subformula.parse()) {
                return false;
            }
        }

        // Set negated propositions
        if (operator == Operator.NOT) {
            Formula subformula = subformulas.get(0);
            Operator subformulaOperator = subformula.operator;
            if (subformulaOperator == Operator.NONE) {
                operator = Operator.NOT;
                operatorIndex = 0;
            } else if (subformulaOperator == Operator.AND) {
                operator = Operator.NOTAND;
                operatorIndex = subformula.operatorIndex + 1;
            } else if (subformulaOperator == Operator.OR) {
                operator = Operator.NOTOR;
                operatorIndex = subformula.operatorIndex + 1;
            } else if (subformulaOperator == Operator.CONDITION) {
                operator = Operator.NOTCONDITION;
                operatorIndex = subformula.operatorIndex + 1;
            } else if (subformulaOperator == Operator.BICONDITION) {
                operator = Operator.NOTBICONDITION;
                operatorIndex = subformula.operatorIndex + 1;
            } else if (subformulaOperator == Operator.NECESSARILY) {
                operator = Operator.NOTNECESSARILY;
                operatorIndex = subformula.operatorIndex + 1;
            } else if (subformulaOperator == Operator.POSSIBLY) {
                operator = Operator.NOTPOSSIBLY;
                operatorIndex = subformula.operatorIndex + 1;
            }
        }

        return true;
    }

    // Check if a proposition is valid
    private boolean validProposition(String formulaString) {

        // Check for invalid characters and strings
        for (int i = 0; i < formulaString.length(); i++) {
            char c = formulaString.charAt(i);
            if (c == '(' || c == ')' || c == '~' || c == '&' || c == '|' || c == ',' || c == ';') {
                return false;
            } else if (i < formulaString.length()-1 && (c == '[' && formulaString.charAt(i+1) == ']')) {
                return false;
            } else if (i < formulaString.length()-1 && (c == '<' && formulaString.charAt(i+1) == '>')) {
                return false;
            } else if (i < formulaString.length()-1 && (c == '-' && formulaString.charAt(i+1) == '>')) {
                return false;
            } else if (i < formulaString.length()-2 && (c == '<' && formulaString.charAt(i+1) == '-' && formulaString.charAt(i+2) == '>')) {
                return false;
            }
        }

        return true;
    }

    // Determine the core operator and its subformulas (but for negations), as well as returning false if found any invalid structures
    private boolean analise() {

        int end = formulaString.length();

        // ~
        if (end > 0 && formulaString.charAt(0) == '~') {
            Formula subformula = new Formula(formulaString.substring(1, end));
            subformulas.add(subformula);
            operator = Operator.NOT;
            operatorIndex = 0;
        }

        // Binary operators
        else if (end > 3 && formulaString.charAt(0) == '(') {

            // Check for non-matching parenthesis
            if (formulaString.charAt(end-1) != ')') {
                return false;
            }
            if (!findBinaryOperator(this)) {
                return false;
            }

            int operatorLength = 0; // Should always change value
            if (operator == Operator.AND || operator == Operator.OR) {
                operatorLength = 1;
            } else if (operator == Operator.CONDITION) {
                operatorLength = 2;
            } else if (operator == Operator.BICONDITION) {
                operatorLength = 3;
            }
            Formula firstSubformula = new Formula(formulaString.substring(1, operatorIndex));
            Formula secondSubformula = new Formula(formulaString.substring(operatorIndex+operatorLength, end-1));
            subformulas.add(firstSubformula);
            subformulas.add(secondSubformula);
        }

        // []
        else if (end > 2 && formulaString.substring(0,2).equals("[]")) {
            operator = Operator.NECESSARILY;
            operatorIndex = 0;
            subformulas.add(new Formula(formulaString.substring(2, end)));
        }

        // <>
        else if (end > 2 && formulaString.substring(0,2).equals("<>")) {
            operator = Operator.POSSIBLY;
            operatorIndex = 0;
            subformulas.add(new Formula(formulaString.substring(2, end)));
        }

        // Proposition
        else {
            operator = Operator.NONE;
        }

        return true;
    }

    // Finds and sets the operator and operatorIndex of the formula given, and returns false if no operator is found
    private boolean findBinaryOperator(Formula formula) {

        String formulaString = formula.formulaString;
        int countSubformulas = -1; // -1 because we want to ignore the initial parenthesis

        for (int i = 0; i < formulaString.length(); i++) {
            char c = formulaString.charAt(i);
            if (c == '(') {
                countSubformulas++;
            } else if (c == ')') {
                countSubformulas--;
            } else if (c == '&' && countSubformulas == 0) {
                formula.operator = Operator.AND;
                formula.operatorIndex = i;
                return true;
            } else if (c == '|' && countSubformulas == 0) {
                formula.operator = Operator.OR;
                formula.operatorIndex = i;
                return true;
            } else if (i<formulaString.length()-1 && ((c == '-' && formulaString.charAt(i+1) == '>') && countSubformulas == 0)) {
                formula.operator = Operator.CONDITION;
                formula.operatorIndex = i;
                return true;
            }  else if (i<formulaString.length()-2 && ((c == '<' && formulaString.charAt(i+1) == '-' && formulaString.charAt(i+2) == '>') && countSubformulas == 0)) {
                formula.operator = Operator.BICONDITION;
                formula.operatorIndex = i;
                return true;
            }
        }

        return false;
    }

    // Negate this formula
    public void negate() {
        if (formulaString.charAt(0) == '~') {
            formulaString = formulaString.substring(1, formulaString.length());
        } else {
            formulaString = "~" + formulaString;
        }
    }

    public ArrayList<Formula> getSubformulas() {
        return subformulas;
    }

    public String getFormulaString() {
        return formulaString;
    }

    public Operator getOperator() {
        return operator;
    }

    public int getOperatorIndex() {
        return operatorIndex;
    }

}
