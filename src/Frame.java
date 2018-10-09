import java.util.ArrayList;
import java.util.HashSet;

public class Frame {

    Tableau tableau =
    private HashSet<World> worlds; // TODO: Make it a queue to make it a fair schedule when searching for a formula to expand
    private HashSet<Transition> transitions;

    public Frame(ArrayList<Formula> initialFormulas, Tableau tableau) {
        this.tableau = tableau;
        this.worlds = new HashSet<>();
        World initialWorld = new World(initialFormulas);
        this.worlds.add(initialWorld);
        this.transitions = new HashSet<>();
    }

    // TODO: THIS IS WRONG BECAUSE IT IS NOT CLONING THE WORLDS
    public Frame(Frame originalFrame, Tableau tableau) {
        this.tableau = tableau;
        this.worlds = originalFrame.worlds;
        this.transitions = originalFrame.transitions;
    }

    public boolean expandNextFormula() {

        for (World world : worlds) {
            for (Formula formula : world.getFormulas()) {

                Operator operator = formula.getOperator();

                // Alpha Rule // TODO: Change these ifs for switch cases
                if (operator == Operator.AND) {
                    world.addFormula(formula.getSubformulas().get(0));
                    world.addFormula(formula.getSubformulas().get(1));
                    world.eliminateFormula(formula);
                } else if (operator == Operator.NOTOR) {
                    Formula subformula1 = formula.getSubformulas().get(0);
                    Formula subformula2 = formula.getSubformulas().get(1);
                    subformula1.negate();
                    subformula2.negate();
                    world.addFormula(subformula1);
                    world.addFormula(subformula2);
                    world.eliminateFormula(formula);
                } else if (operator == Operator.NOTCONDITION) {
                    Formula subformula2 = formula.getSubformulas().get(1);
                    subformula2.negate();
                    world.addFormula(formula.getSubformulas().get(0));
                    world.addFormula(subformula2);
                    world.eliminateFormula(formula);
                } else if (operator == Operator.BICONDITION) {
                    world.addFormula(formula.getSubformulas().get(0));
                    world.addFormula(formula.getSubformulas().get(1));
                    world.eliminateFormula(formula);
                }

                // Gamma Rule
                else if (operator == Operator.POSSIBLY) {
                    ArrayList<Formula> gammaFormula = new ArrayList<>();
                    gammaFormula.add(formula.getSubformulas().get(0));
                    World newWorld = new World(gammaFormula);
                    worlds.add(newWorld);
                    transitions.add(new Transition(world, newWorld));
                } else if (operator == Operator.NOTNECESSARILY) {
                    ArrayList<Formula> gammaFormula = new ArrayList<>();
                    Formula negatedFormula = formula.getSubformulas().get(0).getSubformulas().get(0);
                    negatedFormula.negate();
                    gammaFormula.add(negatedFormula);
                    World newWorld = new World(gammaFormula);
                    worlds.add(newWorld);
                    transitions.add(new Transition(world, newWorld));
                }

                // Beta Rule
                else if (operator == Operator.OR) {
                    Frame
                } else if (operator == Operator.NOTAND) {

                } else if (operator == Operator.CONDITION) {

                } else if (operator == Operator.NOTBICONDITION) {

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

}
