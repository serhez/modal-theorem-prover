import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class World {

    private ArrayList<Formula> formulas;

    public World(ArrayList<Formula> formulas) {
        this.formulas = cloneFormulas(formulas);
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
            return false;
        }

        return true;
    }

    @Override
    public World clone() {
        World clone = new World(formulas);

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

    public void addFormula(Formula formula) {
        formulas.add(formula.clone());
    }

    public void eliminateFormula(Formula deadFormula) {
        for (Iterator<Formula> iterator = formulas.iterator(); iterator.hasNext();) {
            Formula formula = iterator.next();
            if(formula.getString().equals(deadFormula.getString())) {
                iterator.remove();
            }
        }
    }

    public ArrayList<Formula> getFormulas() {
        return formulas;
    }
}
