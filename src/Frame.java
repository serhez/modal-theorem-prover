import java.util.ArrayList;
import java.util.HashSet;

public class Frame {

    private final Tableau tableau;
    private ArrayList<World> worlds; // TODO: Make it a queue to make it a fair schedule when searching for a formula to expand
    private int currentWorld;
    private HashSet<Transition> transitions;

    public Frame(ArrayList<Formula> initialFormulas, Tableau tableau) {
        this.tableau = tableau;
        this.worlds = new ArrayList<>();
        this.currentWorld = 0;
        World initialWorld = new World(initialFormulas);
        this.worlds.add(initialWorld);
        this.transitions = new HashSet<>();
    }

    public Frame(Frame originalFrame, Tableau tableau) {
        this.tableau = tableau;
        this.worlds = originalFrame.cloneWorlds();
        this.currentWorld = originalFrame.currentWorld;
        this.transitions = originalFrame.transitions;
    }

    public boolean expandNextFormula() {

        // We prioritise expanding alpha formulas, then gamma, then beta and finally delta
        World chosenWorld = null;
        Formula chosenFormula = null;

        // Repetition in the loop is needed to keep chosenWorld and chosenFormula as null objects if no operators are found
        outerLoop:
        for (int i = 0; i < worlds.size(); i++) {
            for (Formula formula : worlds.get(i).getFormulas()) {
                if (formula.getOperator() == Operator.AND || formula.getOperator() == Operator.BICONDITION || formula.getOperator() == Operator.NOTOR || formula.getOperator() == Operator.NOTCONDITION) {
                    currentWorld = i;
                    chosenWorld = worlds.get(i);
                    chosenFormula = formula;
                    break outerLoop;
                } else if (formula.getOperator() == Operator.POSSIBLY || formula.getOperator() == Operator.NOTNECESSARILY) {
                    currentWorld = i;
                    chosenWorld = worlds.get(i);
                    chosenFormula = formula;
                    break outerLoop;
                } else if (formula.getOperator() == Operator.OR || formula.getOperator() == Operator.NOTAND || formula.getOperator() == Operator.CONDITION || formula.getOperator() == Operator.NOTBICONDITION) {
                    currentWorld = i;
                    chosenWorld = worlds.get(i);
                    chosenFormula = formula;
                    break outerLoop;
                } else if (formula.getOperator() == Operator.NECESSARILY || formula.getOperator() == Operator.NOTPOSSIBLY) {
                    if (!formula.isTicked()) {
                        currentWorld = i;
                        chosenWorld = worlds.get(i);
                        chosenFormula = formula;
                        break outerLoop;
                    }
                }
            }
        }

        // Only propositions and negated propositions remaining on the frame
        if (chosenWorld == null || chosenFormula == null) {
            return false;
        }

        expand(chosenFormula, chosenWorld);

        return true;
    }

    private void expand(Formula formula, World world) {

        Operator operator = formula.getOperator();

        // Alpha Rule
        if (operator == Operator.AND || operator == Operator.BICONDITION || operator == Operator.NOTOR || operator == Operator.NOTCONDITION) {
            world.addFormula(formula.getSubformulas().get(0));
            world.addFormula(formula.getSubformulas().get(1));
            world.eliminateFormula(formula);
        }

        // Gamma Rule
        else if (operator == Operator.POSSIBLY || operator == Operator.NOTNECESSARILY) {
            ArrayList<Formula> gammaFormula = new ArrayList<>();
            gammaFormula.add(formula.getSubformulas().get(0));
            world.eliminateFormula(formula);
            World newWorld = new World(gammaFormula);
            worlds.add(newWorld);
            transitions.add(new Transition(world, newWorld));
        }

        // Beta Rule
        else if (operator == Operator.OR || operator == Operator.NOTAND || operator == Operator.NOTBICONDITION || operator == Operator.CONDITION) {
            Formula subformula1 = formula.getSubformulas().get(0);
            Formula subformula2 = formula.getSubformulas().get(1);
            world.eliminateFormula(formula);
            Frame disjunctiveFrame = new Frame(this, tableau);
            world.addFormula(subformula1);
            disjunctiveFrame.addFormula(subformula2);
            tableau.addFrame(disjunctiveFrame);
        }

        // Delta Rule
        else if (operator == Operator.NECESSARILY || operator == Operator.NOTPOSSIBLY) {
            HashSet<World> deltaWorlds = getDeltaWorldsFor(world);
            for (World deltaWorld : deltaWorlds) {
                deltaWorld.addFormula(formula.getSubformulas().get(0));
            }
            if (!expandable()) {
                formula.ticked();
            } else {
                untickAllFormulas();
            }
        }
    }

    private void untickAllFormulas() {
        for (World world : worlds) {
            for (Formula formula : world.getFormulas()) {
                formula.notTicked();  // No need to only untick [] formulas, as all formulas are unticked by default, plus the tick is irrelevant for non-[] formulas
            }
        }
    }

    // Returns false if only propositions, negated propositions and [] formulas are remaining in the frame, true otherwise
    private boolean expandable() {

        for (World world : worlds) {
            for (Formula formula : world.getFormulas()) {
                Operator operator = formula.getOperator();
                if (operator != Operator.NOT && operator != Operator.NONE && operator != Operator.NECESSARILY && operator != Operator.NOTPOSSIBLY) {
                    return true;
                }
            }
        }

        return false;
    }

    private HashSet<World> getDeltaWorldsFor(World world) {
        HashSet<World> deltaWorlds = new HashSet<>();
        for (Transition transition : transitions) {
            if (transition.from().equals(world)) {
                deltaWorlds.add(transition.to());
            }
        }
        return deltaWorlds;
    }

    public boolean hasContradiction() {
        for (World world : worlds) {
            if (world.hasContradiction()) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<World> cloneWorlds() {
        ArrayList<World> clonedWorlds = new ArrayList<>();
        for (World world : worlds) {
            clonedWorlds.add(world.clone());
        }
        return clonedWorlds;
    }

    private void addFormula(Formula formula) {
        worlds.get(currentWorld).addFormula(formula);
    }

    public void print() {
        int count = 1;
        for (World world : worlds) {
            System.out.println();
            System.out.println("\t\t\t\t\t\t\t  WORLD " + count);
            count++;
            world.print();
            System.out.println();
        }
    }
}
