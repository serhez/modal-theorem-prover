import java.util.ArrayList;
import java.util.HashSet;

public class World {

    private final int id;
    private ArrayList<Formula> formulas;

    public World(ArrayList<Formula> formulas, int id) {
        this.formulas = cloneFormulas(formulas);
        this.id = id;
    }

    public boolean hasContradiction() {

        HashSet<String> propositions = new HashSet<>();
        HashSet<String> negatedPropositions = new HashSet<>();

        for (Formula formula : formulas) {
            if (formula.getOperator() == Operator.NONE) {
                propositions.add(formula.getString());
            } else if (formula.getOperator() == Operator.NOT) {
                negatedPropositions.add(formula.getString().substring(1));
            }
        }

        propositions.retainAll(negatedPropositions);  // Calculate the intersection of both sets, now stored in "propositions"
        if (!propositions.isEmpty()) {
            return true;
        }

        return false;
    }

    @Override
    public World clone() {
        World clone = new World(formulas, id);
        return clone;
    }

    // We clone the formulas so changes to a formula in one world are not made also in the rest of worlds
    private ArrayList<Formula> cloneFormulas(ArrayList<Formula> formulas) {
        ArrayList<Formula> clonedFormulas = new ArrayList<>();
        for (Formula formula : formulas) {
            clonedFormulas.add(formula.clone());
        }
        return clonedFormulas;
    }

    public void addFormula(Formula newFormula) {
        // Check for duplicate formulas
        for (Formula formula : formulas){
            if(formula.getString().equals(newFormula.getString())) {
                return;
            }
        }
        formulas.add(newFormula.clone());
    }

    public void eliminateFormula(Formula deadFormula) {
        boolean allFormulasChecked = false;  // Allows all formulas to be checked (to eliminate duplicates) while evading Concurrent Modification Exceptions
        whileLoop:
        while(!allFormulasChecked) {
            for (Formula formula : formulas) {
                if(formula.getString().equals(deadFormula.getString())) {
                    formulas.remove(formula);
                    continue whileLoop;
                }
            }
            allFormulasChecked = true;
        }
    }

    public ArrayList<Formula> getFormulas() {
        return formulas;
    }

    public int getId() {
        return id;
    }

    // Debugging
    public void print() {
        for(Formula formula : formulas) {
            System.out.println(formula.getString());
        }
    }
}
