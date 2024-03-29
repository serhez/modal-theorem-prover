import java.util.HashSet;
import java.util.LinkedList;

public class World {

    private final int id;
    private LinkedList<Formula> formulas;

    public World(LinkedList<Formula> formulas, int id) {
        this.formulas = new LinkedList<>();
        LinkedList<Formula> clonedFormulas = cloneFormulas(formulas);
        for (Formula formula : clonedFormulas) {
            addFormula(formula);  // To keep all formulas unique
        }
        this.id = id;
    }

    public boolean hasContradiction() {

        HashSet<String> propositions = new HashSet<>();
        HashSet<String> negatedPropositions = new HashSet<>();

        for (Formula formula : formulas) {
            if (formula.getType() == FormulaType.FALSE) {
                return true;
            }
            if (formula.getType() == FormulaType.PROPOSITION) {
                propositions.add(formula.getString());
            } else if (formula.getType() == FormulaType.NOTPROPOSITION) {
                negatedPropositions.add(formula.getString().substring(1));
            }
        }

        propositions.retainAll(negatedPropositions);  // Calculate the intersection of both sets, now stored in "propositions"
        if (!propositions.isEmpty()) {
            return true;
        }

        return false;
    }

    public void allCurrentGammaFormulasExpandedTo(int worldId) {
        for (Formula formula : formulas) {
            if (formula.getType() == FormulaType.NECESSARILY || formula.getType() == FormulaType.NOTPOSSIBLY) {
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
    public boolean containsFormulas(HashSet<Formula> formulas) {
        for (Formula formula : formulas){
            if (!containsFormula(formula)) {
                return false;
            }
        }
        return true;
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

    public HashSet<Formula> getTransitiveDeltaExpansionFormulas(Formula deltaFormula) {
        HashSet<Formula> transitiveFormulas = new HashSet<>();
        transitiveFormulas.add(deltaFormula.getSubformulas().get(0));
        for (Formula formula : formulas) {
            if (formula.getType() == FormulaType.NECESSARILY || formula.getType() == FormulaType.NOTPOSSIBLY) {
                transitiveFormulas.add(formula);
                transitiveFormulas.add(formula.getSubformulas().get(0));
            }
        }

        return transitiveFormulas;
    }

    public HashSet<Formula> getKripkeDeltaExpansionFormulas(Formula deltaFormula) {
        HashSet<Formula> kripkeFormulas = new HashSet<>();
        kripkeFormulas.add(deltaFormula.getSubformulas().get(0));
        for (Formula formula : formulas) {
            if (formula.getType() == FormulaType.NECESSARILY || formula.getType() == FormulaType.NOTPOSSIBLY) {
                kripkeFormulas.add(formula.getSubformulas().get(0));
            }
        }

        return kripkeFormulas;
    }

    public HashSet<Formula> getGammaExpansionFormulas(ModalLogic logic) {
        HashSet<Formula> gammaFormulas = new HashSet<>();
        for (Formula formula : formulas) {
            if (formula.getType() == FormulaType.NECESSARILY || formula.getType() == FormulaType.NOTPOSSIBLY) {
                if (logic.isTransitive()) {
                    gammaFormulas.add(formula);
                }
                gammaFormulas.add(formula.getSubformulas().get(0));
            }
        }

        return gammaFormulas;
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
            System.out.print(formula.getString() + "\t:\t" + formula.getType());
            if (!formula.getWorldsExpandedTo().isEmpty()) {
                System.out.println("\t\t; expanded to " + formula.getWorldsExpandedTo());
            } else {
                System.out.println();
            }
        }
    }
}
