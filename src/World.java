import java.util.HashSet;
import java.util.LinkedList;

public class World {

    private final int id;
    private LinkedList<Formula> formulas;

    public World(LinkedList<Formula> formulas, int id) {
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
    private LinkedList<Formula> cloneFormulas(LinkedList<Formula> formulas) {
        LinkedList<Formula> clonedFormulas = new LinkedList<>();
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
        int formulasCount = 0;
        int formulasSize = formulas.size();
        while(formulasCount != formulasSize) {
            Formula formula = formulas.getFirst();
            formulasCount++;
            if(formula.getString().equals(deadFormula.getString())) {
                formulas.removeFirst();
            } else {
                formulas.add(formulas.removeFirst());
            }
        }
    }

    public LinkedList<Formula> getFormulas() {
        return formulas;
    }

    public int getId() {
        return id;
    }

    // Debugging
    public void print() {
        for(Formula formula : formulas) {
            System.out.println(formula.getString() + "\t:\t" + formula.getOperator());
        }
    }
}
