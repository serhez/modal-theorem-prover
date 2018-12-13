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

    public void allCurrentDeltaFormulasExpandedTo(int worldId) {
        for (Formula formula : formulas) {
            if (formula.getOperator() == Operator.NECESSARILY || formula.getOperator() == Operator.NOTPOSSIBLY) {
                formula.addWorldExpandedTo(worldId);
            }
        }
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

    // This function also counts ticked formulas
    public boolean containsFormula(Formula newFormula) {
        for (Formula formula : formulas){
            if(newFormula.getString().equals(formula.getString())) {
                return true;
            }
        }
        return false;
    }

    public void addFormula(Formula newFormula) {
        // Check for duplicate formulas
        if (containsFormula(newFormula)) {
            return;
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

    public HashSet<Formula> getTransitiveGammaExpansionFormulas(Formula gammaFormula) {
        HashSet<Formula> transitiveFormulas = new HashSet<>();
        transitiveFormulas.add(gammaFormula.getSubformulas().get(0));
        for (Formula formula : formulas) {
            if (formula.getOperator() == Operator.NECESSARILY || formula.getOperator() == Operator.NOTPOSSIBLY) {
                transitiveFormulas.add(formula);
                transitiveFormulas.add(formula.getSubformulas().get(0));
            }
        }

        return transitiveFormulas;
    }

    public HashSet<Formula> getKripkeGammaExpansionFormulas(Formula gammaFormula) {
        HashSet<Formula> kripkeFormulas = new HashSet<>();
        kripkeFormulas.add(gammaFormula.getSubformulas().get(0));
        for (Formula formula : formulas) {
            if (formula.getOperator() == Operator.NECESSARILY || formula.getOperator() == Operator.NOTPOSSIBLY) {
                kripkeFormulas.add(formula.getSubformulas().get(0));
            }
        }

        return kripkeFormulas;
    }

    public HashSet<Formula> getDeltaExpansionFormulas() {
        HashSet<Formula> deltaFormulas = new HashSet<>();
        for (Formula formula : formulas) {
            if (formula.getOperator() == Operator.NECESSARILY || formula.getOperator() == Operator.NOTPOSSIBLY) {
                deltaFormulas.add(formula.getSubformulas().get(0));
            }
        }

        return deltaFormulas;
    }

    public int getId() {
        return id;
    }

    // Debugging
    public void print() {
        for(Formula formula : formulas) {
            if (formula.isTicked()) {
                System.out.print("[TICKED] ");
            }
            System.out.print(formula.getString() + "\t:\t" + formula.getOperator());
            if (!formula.getWorldsExpandedTo().isEmpty()) {
                System.out.println("\t\t; expanded to " + formula.getWorldsExpandedTo());
            } else {
                System.out.println();
            }
        }
    }
}
