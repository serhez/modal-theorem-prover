import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class Frame {

    private final Tableau tableau;
    private LinkedList<World> worlds;
    private int currentWorldId; // TODO: THIS NOW USES WORLD ID, HAVEN'T CHECKED IMPLICATIONS
    private HashSet<Transition> transitions;
    private int numberOfWorlds;
    private final int id;
    private boolean isExpandable;

    public Frame(LinkedList<Formula> initialFormulas, Tableau tableau, int id) {
        this.id = id;
        this.isExpandable = true;
        this.tableau = tableau;
        this.worlds = new LinkedList<>();
        this.currentWorldId = 0;
        numberOfWorlds = 1;
        World initialWorld = new World(initialFormulas, numberOfWorlds);
        this.worlds.add(initialWorld);
        this.transitions = new HashSet<>();
    }

    public Frame(Frame originalFrame, Tableau tableau, int id) {
        this.id = id;
        this.isExpandable = true;
        this.tableau = tableau;
        this.numberOfWorlds = originalFrame.getNumberOfWorlds();
        this.worlds = originalFrame.cloneWorlds();
        this.currentWorldId = originalFrame.currentWorldId;
        this.transitions = originalFrame.cloneTransitions();
    }

    public void expandNextFormula() {

        // We prioritise expanding alpha formulas, then gamma, then beta and finally delta
        World chosenWorld = null;  // TODO: MAY NOT BE NEEDED; USE CURRENT WORLD INSTEAD
        Formula chosenFormula = null;
        int seenFormulas;
        int totalFormulas;
        int seenWorlds = 0;
        int totalWorlds = worlds.size();

        // Alpha
        outerLoop:
        while (seenWorlds != totalWorlds) {
            World world = worlds.getFirst();
            LinkedList<Formula> formulas = world.getFormulas();
            totalFormulas = formulas.size();
            seenFormulas = 0;
            seenWorlds++;
            while (seenFormulas != totalFormulas) {
                Formula formula = formulas.getFirst();
                seenFormulas++;
                if (formula.getOperator() == Operator.AND || formula.getOperator() == Operator.BICONDITION || formula.getOperator() == Operator.NOTOR || formula.getOperator() == Operator.NOTCONDITION) {
                    currentWorldId = world.getId();
                    chosenWorld = world;
                    chosenFormula = formula;
                    break outerLoop;
                }
                formulas.add(formulas.removeFirst());
            }
            worlds.add(worlds.removeFirst());
        }

        seenWorlds = 0;

        // Gamma
        if (chosenWorld == null || chosenFormula == null) {
            outerLoop:
            while (seenWorlds != totalWorlds) {
                World world = worlds.getFirst();
                LinkedList<Formula> formulas = world.getFormulas();
                totalFormulas = formulas.size();
                seenFormulas = 0;
                seenWorlds++;
                while (seenFormulas != totalFormulas) {
                    Formula formula = formulas.getFirst();
                    seenFormulas++;
                    if (formula.getOperator() == Operator.POSSIBLY || formula.getOperator() == Operator.NOTNECESSARILY) {
                        currentWorldId = world.getId();
                        chosenWorld = world;
                        chosenFormula = formula;
                        break outerLoop;
                    }
                    formulas.add(formulas.removeFirst());
                }
                worlds.add(worlds.removeFirst());
            }
        }

        seenWorlds = 0;

        // Beta
        if (chosenWorld == null || chosenFormula == null) {
            outerLoop:
            while (seenWorlds != totalWorlds) {
                World world = worlds.getFirst();
                LinkedList<Formula> formulas = world.getFormulas();
                totalFormulas = formulas.size();
                seenFormulas = 0;
                seenWorlds++;
                while (seenFormulas != totalFormulas) {
                    Formula formula = formulas.getFirst();
                    seenFormulas++;
                    if (formula.getOperator() == Operator.OR || formula.getOperator() == Operator.NOTAND || formula.getOperator() == Operator.CONDITION || formula.getOperator() == Operator.NOTBICONDITION) {
                        currentWorldId = world.getId();
                        chosenWorld = world;
                        chosenFormula = formula;
                        break outerLoop;
                    }
                    formulas.add(formulas.removeFirst());
                }
                worlds.add(worlds.removeFirst());
            }
        }

        seenWorlds = 0;

        // Delta
        if (chosenWorld == null || chosenFormula == null) {
            outerLoop:
            while (seenWorlds != totalWorlds) {
                World world = worlds.getFirst();
                LinkedList<Formula> formulas = world.getFormulas();
                totalFormulas = formulas.size();
                seenFormulas = 0;
                seenWorlds++;
                while (seenFormulas != totalFormulas) {
                    Formula formula = formulas.getFirst();
                    seenFormulas++;
                    if (formula.getOperator() == Operator.NECESSARILY || formula.getOperator() == Operator.NOTPOSSIBLY) {
                        if (!formula.isTicked()) {
                            currentWorldId = world.getId();
                            chosenWorld = world;
                            chosenFormula = formula;
                            break outerLoop;
                        }
                    }
                    formulas.add(formulas.removeFirst());
                }
                worlds.add(worlds.removeFirst());
            }
        }

        // Only propositions, negated propositions and ticked delta-formulas remaining on the frame
        if (chosenWorld == null || chosenFormula == null) {
            isExpandable = false;
            return;
        }

        expand(chosenFormula, chosenWorld);
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
            LinkedList<Formula> gammaFormula = new LinkedList<>();
            gammaFormula.add(formula.getSubformulas().get(0));
            world.eliminateFormula(formula);
            numberOfWorlds++;
            World newWorld = new World(gammaFormula, numberOfWorlds);
            worlds.add(newWorld);
            transitions.add(new Transition(world.getId(), newWorld.getId()));
        }

        // Beta Rule
        else if (operator == Operator.OR || operator == Operator.NOTAND || operator == Operator.NOTBICONDITION || operator == Operator.CONDITION) {
            Formula subformula1 = formula.getSubformulas().get(0);
            Formula subformula2 = formula.getSubformulas().get(1);
            world.eliminateFormula(formula);
            Frame disjunctiveFrame = new Frame(this, tableau, tableau.getNewFrameId());
            world.addFormula(subformula1);
            disjunctiveFrame.addFormula(subformula2);
            tableau.addFrame(disjunctiveFrame);
        }

        // Delta Rule
        else if (operator == Operator.NECESSARILY || operator == Operator.NOTPOSSIBLY) {
            LinkedList<World> deltaWorlds = getDeltaWorldsFor(world, formula);
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

    private LinkedList<World> getDeltaWorldsFor(World world, Formula formula) {
        LinkedList<World> deltaWorlds = new LinkedList<>();
        for (Transition transition : transitions) {
            if (transition.from() == world.getId() && !formula.getWorldsExpandedTo().contains(transition.to())) {
                deltaWorlds.add(getWorldWithId(transition.to()));
                formula.addWorldExpandedTo(transition.to());
            }
        }
        return deltaWorlds;
    }

    public boolean hasContradiction() {
        for (World world : worlds) {
            if (world.hasContradiction()) {
                return true;
            }
        }
        return false;
    }

    private LinkedList<World> cloneWorlds() {
        LinkedList<World> clonedWorlds = new LinkedList<>();
        for (World world : worlds) {
            clonedWorlds.add(world.clone());
        }
        return clonedWorlds;
    }

    private HashSet<Transition> cloneTransitions() {
        HashSet<Transition> clones = new HashSet<>();
        for (Transition transition : transitions) {
            Transition clone = new Transition(transition.from(), transition.to());
            clones.add(clone);
        }
        return clones;
    }

    private void addFormula(Formula formula) {
        getWorldWithId(currentWorldId).addFormula(formula);
    }

    private World getWorldWithId(int id) {
        for (World world : worlds) {
            if (world.getId() == id) {
                return world;
            }
        }
        return null;
    }

    public int getNumberOfWorlds() {
        return numberOfWorlds;
    }

    public int getId() {
        return id;
    }

    public boolean isExpandable() {
        return isExpandable;
    }

    // Debugging

    public void print() {
        for (World world : worlds) {
            System.out.println();
            System.out.println("\t\t\t\t\t\t\t  WORLD " + world.getId() + " -> " + getTransitionsFrom(world.getId()));
            world.print();
            System.out.println();
        }
    }

    public String getTransitionsFrom(int id) {
        ArrayList<Integer> accessibleWorlds = new ArrayList<>();
        for (Transition transition : transitions) {
            if (transition.from() == id) {
                accessibleWorlds.add(transition.to());
            }
        }
        return accessibleWorlds.toString();
    }
}
