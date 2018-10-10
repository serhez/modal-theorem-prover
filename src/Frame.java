import java.util.ArrayList;
import java.util.HashSet;

public class Frame {

    private final Tableau tableau;
    private HashSet<World> worlds; // TODO: Make it a queue to make it a fair schedule when searching for a formula to expand
    private World currentWorld;
    private HashSet<Transition> transitions;

    public Frame(ArrayList<Formula> initialFormulas, Tableau tableau) {
        this.tableau = tableau;
        this.worlds = new HashSet<>();
        this.currentWorld = new World(initialFormulas);
        this.worlds.add(currentWorld);
        this.transitions = new HashSet<>();
    }

    public Frame(Frame originalFrame, Tableau tableau) {
        this.tableau = tableau;
        this.worlds = originalFrame.cloneWorlds();
        this.currentWorld = originalFrame.currentWorld.clone();
        this.transitions = originalFrame.transitions;
    }

    public boolean expandNextFormula() {

        // We prioritise expanding alpha formulas, then gamma, then beta and finally delta
        World chosenWorld = null;
        Formula chosenFormula = null;
        outerLoop:
        for (World world : worlds) {
            for (Formula formula : world.getFormulas()) {
                if (formula.getOperator() == Operator.AND || formula.getOperator() == Operator.BICONDITION || formula.getOperator() == Operator.NOTOR || formula.getOperator() == Operator.NOTCONDITION) {
                    currentWorld = world;
                    chosenWorld = world;
                    chosenFormula = formula;
                    break outerLoop;
                } else if (formula.getOperator() == Operator.POSSIBLY || formula.getOperator() == Operator.NOTNECESSARILY) {
                    currentWorld = world;
                    chosenWorld = world;
                    chosenFormula = formula;
                    break outerLoop;
                } else if (formula.getOperator() == Operator.OR || formula.getOperator() == Operator.NOTAND || formula.getOperator() == Operator.CONDITION || formula.getOperator() == Operator.NOTBICONDITION) {
                    currentWorld = world;
                    chosenWorld = world;
                    chosenFormula = formula;
                    break outerLoop;
                } else if (formula.getOperator() == Operator.NECESSARILY || formula.getOperator() == Operator.NOTPOSSIBLY) {
                    currentWorld = world;
                    chosenWorld = world;
                    chosenFormula = formula;
                    break outerLoop;
                } else {
                    return false;
                }
            }
        }

        if (chosenWorld == null || chosenFormula == null) {
            return false;
        }

        expand(chosenFormula, chosenWorld);

        return true;
    }

    private void expand(Formula formula, World world) {

        Operator operator = formula.getOperator();

        // Alpha Rule // TODO: Change these ifs for switch cases
        if (operator == Operator.AND || operator == Operator.BICONDITION) {
            world.addFormula(formula.getSubformulas().get(0));
            world.addFormula(formula.getSubformulas().get(1));
            world.eliminateFormula(formula);
        } else if (operator == Operator.NOTOR) {
            Formula subformula1 = formula.getSubformulas().get(0).getSubformulas().get(0);
            Formula subformula2 = formula.getSubformulas().get(0).getSubformulas().get(1);
            world.eliminateFormula(formula);
            subformula1.negate();
            subformula2.negate();
            world.addFormula(subformula1);
            world.addFormula(subformula2);
        } else if (operator == Operator.NOTCONDITION) {
            Formula subformula1 = formula.getSubformulas().get(0).getSubformulas().get(0);
            Formula subformula2 = formula.getSubformulas().get(0).getSubformulas().get(1);
            world.eliminateFormula(formula);
            subformula2.negate();
            world.addFormula(subformula1);
            world.addFormula(subformula2);
        }

        // Gamma Rule
        else if (operator == Operator.POSSIBLY) {
            ArrayList<Formula> gammaFormula = new ArrayList<>();
            gammaFormula.add(formula.getSubformulas().get(0));
            World newWorld = new World(gammaFormula);
            worlds.add(newWorld);
            transitions.add(new Transition(world, newWorld));
            world.eliminateFormula(formula);
        } else if (operator == Operator.NOTNECESSARILY) {
            ArrayList<Formula> gammaFormula = new ArrayList<>();
            Formula negatedFormula = formula.getSubformulas().get(0).getSubformulas().get(0);
            world.eliminateFormula(formula);
            negatedFormula.negate();
            gammaFormula.add(negatedFormula);
            World newWorld = new World(gammaFormula);
            worlds.add(newWorld);
            transitions.add(new Transition(world, newWorld));
        }

        // Beta Rule
        else if (operator == Operator.OR) {
            Formula subformula1 = formula.getSubformulas().get(0);
            Formula subformula2 = formula.getSubformulas().get(1);
            world.eliminateFormula(formula);
            world.addFormula(subformula1);
            Frame disjunctiveFrame = new Frame(this, tableau);
            disjunctiveFrame.addFormula(subformula2);
            tableau.addFrame(disjunctiveFrame);
        } else if (operator == Operator.NOTAND || operator == Operator.NOTBICONDITION) {
            Formula subformula1 = formula.getSubformulas().get(0).getSubformulas().get(0);
            Formula subformula2 = formula.getSubformulas().get(0).getSubformulas().get(1);
            world.eliminateFormula(formula);
            subformula1.negate();
            subformula2.negate();
            world.addFormula(subformula1);
            Frame disjunctiveFrame = new Frame(this, tableau);
            disjunctiveFrame.addFormula(subformula2);
            tableau.addFrame(disjunctiveFrame);
        } else if (operator == Operator.CONDITION) {
            Formula subformula1 = formula.getSubformulas().get(0);
            Formula subformula2 = formula.getSubformulas().get(1);
            world.eliminateFormula(formula);
            subformula1.negate();
            world.addFormula(subformula1);
            Frame disjunctiveFrame = new Frame(this, tableau);
            disjunctiveFrame.addFormula(subformula2);
            tableau.addFrame(disjunctiveFrame);
        }

        // Delta Rule
        else if (operator == Operator.NECESSARILY) {
            HashSet<World> deltaWorlds = getDeltaWorldsFor(world);
            for (World deltaWorld : deltaWorlds) {
                deltaWorld.addFormula(formula.getSubformulas().get(0));
            }
        } else if (operator == Operator.NOTPOSSIBLY) {
            HashSet<World> deltaWorlds = getDeltaWorldsFor(world);
            Formula negatedFormula = formula.getSubformulas().get(0).getSubformulas().get(0);
            negatedFormula.negate();
            for (World deltaWorld : deltaWorlds) {
                deltaWorld.addFormula(negatedFormula);
            }
        }
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

    public HashSet<World> cloneWorlds() {
        HashSet<World> clonedWorlds = new HashSet<>();
        for (World world : worlds) {
            clonedWorlds.add(world.clone());
        }
        return clonedWorlds;
    }

    private void addFormula(Formula formula) {
        currentWorld.addFormula(formula);
    }

}
