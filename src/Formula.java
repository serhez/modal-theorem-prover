import java.util.ArrayList;
import java.util.HashSet;

public class Formula {

    private String string;
    private final ArrayList<Formula> subformulas;
    private Operator operator;
    private int operatorIndex;                  // In case of a negated operator, the index will indicate the location of the operator, not of the negation
    private HashSet<Integer> worldsExpandedTo;  // Only used to loop check [] formulas
    private boolean ticked;

    public Formula(String formulaString) {
        this.string = formulaString;
        this.subformulas = new ArrayList<>();
        this.ticked = false;
        this.worldsExpandedTo = new HashSet<>();
    }

    // Preprocesses the formulaString and eliminates all vacuous elements; in the future, it may have more elements, such as reducing to normal forms to increase efficiency
    public void preprocess() {
        eliminateDoubleNegations();
    }

    private void eliminateDoubleNegations() {
        for (int i = 0; (i+1) < string.length(); i++) {
            if ((string.charAt(i) == '~') && (string.charAt(i) == string.charAt(i+1))) {
                string = string.substring(0, i) + string.substring(i+2, string.length());
                eliminateDoubleNegations();
                break;
            }
        }
    }

    // Analise the formula and return false if itself or any of its subformulas is not recognized
    public boolean parse() {

        // Find the main operator of the formula and report any unrecognised syntactical structure on the current formula
        if (!analise()) {
            return false;
        }

        // In the case that this formula is a proposition
        if (operator == Operator.NONE) {
            if (!recognisedProposition(string)) {
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

        return true;
    }

    // Check if a proposition is recognised
    private boolean recognisedProposition(String formulaString) {

        // Check for unrecognised characters and strings
        for (int i = 0; i < formulaString.length(); i++) {
            char c = formulaString.charAt(i);
            if (c == '(' || c == ')' || c == '~' || c == '&' || c == '|' || c == ',' || c == ';' || c == ':') {
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

    // Determine the core operator and its subformulas (but for negations), as well as returning false if found any unrecognised structures
    private boolean analise() {

        int end = string.length();

        if (end > 0 && string.charAt(0) == '~') {
            // ~[]
            if (end > 3 && string.substring(1,3).equals("[]")) {
                operator = Operator.NOTNECESSARILY;
                Formula subformula = new Formula(string.substring(3, end));
                subformula.negate();
                subformulas.add(subformula);
            }
            // ~<>
            else if (end > 3 && string.substring(1,3).equals("<>")) {
                operator = Operator.NOTPOSSIBLY;
                Formula subformula = new Formula(string.substring(3, end));
                subformula.negate();
                subformulas.add(subformula);
            }
            // ~
            else if (!findBinaryOperator(this)) {
                Formula subformula = new Formula(string.substring(1, end));
                subformulas.add(subformula);
                operator = Operator.NOT;
                operatorIndex = 0;
            } else {
                // ~&
                if (operator == Operator.AND) {
                    operator = Operator.NOTAND;
                    Formula firstSubformula = new Formula(string.substring(2, operatorIndex));
                    Formula secondSubformula = new Formula(string.substring(operatorIndex+1, end-1));
                    firstSubformula.negate();
                    secondSubformula.negate();
                    subformulas.add(firstSubformula);
                    subformulas.add(secondSubformula);
                }
                // ~|
                else if (operator == Operator.OR) {
                    operator = Operator.NOTOR;
                    Formula firstSubformula = new Formula(string.substring(2, operatorIndex));
                    Formula secondSubformula = new Formula(string.substring(operatorIndex+1, end-1));
                    firstSubformula.negate();
                    secondSubformula.negate();
                    subformulas.add(firstSubformula);
                    subformulas.add(secondSubformula);
                }
                // ~->
                else if (operator == Operator.CONDITION) {
                    operator = Operator.NOTCONDITION;
                    Formula firstSubformula = new Formula(string.substring(2, operatorIndex));
                    Formula secondSubformula = new Formula(string.substring(operatorIndex+2, end-1));
                    secondSubformula.negate();
                    subformulas.add(firstSubformula);
                    subformulas.add(secondSubformula);
                }
                // ~<->
                else if (operator == Operator.BICONDITION) {
                    operator = Operator.NOTBICONDITION;
                    String firstOperand = string.substring(2, operatorIndex);
                    String secondOperand = string.substring(operatorIndex+3, end-1);
                    Formula firstSubformula = new Formula("(" + firstOperand + "->" + secondOperand + ")");
                    Formula secondSubformula = new Formula("(" + secondOperand + "->" + firstOperand + ")");
                    firstSubformula.negate();
                    secondSubformula.negate();
                    subformulas.add(firstSubformula);
                    subformulas.add(secondSubformula);
                }
            }
        }

        // Binary operators
        else if (end > 3 && string.charAt(0) == '(') {

            // Check for non-matching parenthesis
            if (string.charAt(end-1) != ')') {
                return false;
            }
            if (!findBinaryOperator(this)) {
                return false;
            }

            // In the case of a bicondition, we treat it as a conjunction (&) of conditions
            if (operator == Operator.BICONDITION) {
                String firstOperand = string.substring(1, operatorIndex);
                String secondOperand = string.substring(operatorIndex+3, end-1);
                Formula firstSubformula = new Formula("(" + firstOperand + "->" + secondOperand + ")");
                Formula secondSubformula = new Formula("(" + secondOperand + "->" + firstOperand + ")");
                subformulas.add(firstSubformula);
                subformulas.add(secondSubformula);
            }

            else {
                Formula firstSubformula = null;
                Formula secondSubformula = null;
                if (operator == Operator.AND || operator == Operator.OR) {
                    firstSubformula = new Formula(string.substring(1, operatorIndex));
                    secondSubformula = new Formula(string.substring(operatorIndex+1, end-1));
                } else if (operator == Operator.CONDITION) {
                    firstSubformula = new Formula(string.substring(1, operatorIndex));
                    secondSubformula = new Formula(string.substring(operatorIndex+2, end-1));
                    firstSubformula.negate();
                }
                subformulas.add(firstSubformula);
                subformulas.add(secondSubformula);
            }
        }

        // []
        else if (end > 2 && string.substring(0,2).equals("[]")) {
            operator = Operator.NECESSARILY;
            operatorIndex = 0;
            subformulas.add(new Formula(string.substring(2, end)));
        }

        // <>
        else if (end > 2 && string.substring(0,2).equals("<>")) {
            operator = Operator.POSSIBLY;
            operatorIndex = 0;
            subformulas.add(new Formula(string.substring(2, end)));
        }

        // Proposition
        else {
            operator = Operator.NONE;
        }

        return true;
    }

    // Finds and sets the operator and operatorIndex of the formula given, and returns false if no operator is found
    private boolean findBinaryOperator(Formula formula) {

        String formulaString = formula.string;
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
        if (string.length() == 0) {
            return;
        }
        if (string.charAt(0) == '~') {
            string = string.substring(1, string.length());
        } else {
            string = "~" + string;
        }
    }

    @Override
    public Formula clone() {
        Formula clone = new Formula(string);
        clone.setOperator(operator);
        clone.setOperatorIndex(operatorIndex);
        clone.setWorldsExpandedTo(worldsExpandedTo);
        clone.setSubformulas(subformulas);
        clone.setTicked(ticked);
        return clone;
    }

    // Only used for cloning
    private void setOperator(Operator operator) {
        this.operator = operator;
    }

    // Only used for cloning
    private void setOperatorIndex(int operatorIndex) {
        this.operatorIndex = operatorIndex;
    }

    // Only used for cloning
    private void setWorldsExpandedTo(HashSet<Integer> worldsExpandedTo) {
        HashSet<Integer> clone = new HashSet<>();
        for (int i : worldsExpandedTo) {
            clone.add(i);
        }
        this.worldsExpandedTo = clone;
    }

    // TODO: Is cloning "tick" ok with []-formulas?
    // Only used for cloning
    private void setTicked(boolean ticked) {
        this.ticked = ticked;
    }

    // Only used for cloning
    private void setSubformulas(ArrayList<Formula> subformulas) {
        for (Formula subformula : subformulas) {
            this.subformulas.add(subformula.clone());
        }
    }

    public ArrayList<Formula> getSubformulas() {
        return subformulas;
    }

    public String getString() {
        return string;
    }

    public Operator getOperator() {
        return operator;
    }

    public boolean isTicked() {
        return ticked;
    }

    public void untick() {
        ticked = false;
    }

    public void tick() {
        ticked = true;
    }

    public HashSet<Integer> getWorldsExpandedTo() {
        return worldsExpandedTo;
    }

    public void addWorldExpandedTo(int worldId) {
        worldsExpandedTo.add(worldId);
    }
}
