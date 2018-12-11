import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class Frame {

    private final int id;
    private final ModalSystem system;
    private final Tableau tableau;
    private LinkedList<World> worlds;
    private int currentWorldId;
    private HashSet<Transition> transitions;
    private int numberOfWorlds;
    private boolean isExpandable;

    public Frame(LinkedList<Formula> initialFormulas, Tableau tableau, int id, ModalSystem system) {
        this.id = id;
        this.system = system;
        this.isExpandable = true;
        this.tableau = tableau;
        this.worlds = new LinkedList<>();
        this.currentWorldId = 0;
        numberOfWorlds = 1;
        World initialWorld = new World(initialFormulas, numberOfWorlds);
        this.worlds.add(initialWorld);
        this.transitions = new HashSet<>();
        if (system.isReflexive()) {
            this.addTransition(new Transition(1, 1));
        }
    }

    public Frame(Frame originalFrame, Tableau tableau, int id, ModalSystem specification) {
        this.id = id;
        this.system = specification;
        this.isExpandable = true;
        this.tableau = tableau;
        this.numberOfWorlds = originalFrame.getNumberOfWorlds();
        this.worlds = originalFrame.cloneWorlds();
        this.currentWorldId = originalFrame.currentWorldId;
        this.transitions = originalFrame.cloneTransitions();
    }

    public void expandNextFormula() {

        if (hasContradiction()) {
            isExpandable = false;
            return;
        }

        // We prioritise expanding alpha formulas, then beta, then gamma and finally delta
        World chosenWorld = null;
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
            if (system.isSerial()) {
                World nonSerialWorld = getNonSerialWorld();
                if (nonSerialWorld == null) {
                    isExpandable = false;
                    return;
                } else {
                    serialise(nonSerialWorld);
                }
            }
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
            formula.tick();
        }

        // Beta Rule
        else if (operator == Operator.OR || operator == Operator.NOTAND || operator == Operator.NOTBICONDITION || operator == Operator.CONDITION) {
            Formula subformula1 = formula.getSubformulas().get(0);
            Formula subformula2 = formula.getSubformulas().get(1);
            formula.tick();
            Frame disjunctiveFrame = new Frame(this, tableau, tableau.getNewFrameId(), system);
            world.addFormula(subformula1);
            disjunctiveFrame.addFormula(subformula2);
            tableau.addFrame(disjunctiveFrame);
        }

        // Gamma Rule
        else if (operator == Operator.POSSIBLY || operator == Operator.NOTNECESSARILY) {

            formula.tick();

            if (system.isTransitive()) {
                HashSet<Formula> transitiveFormulas = world.getTransitiveGammaExpansionFormulas(formula);
                World existingWorld = worldContainingFormulas(transitiveFormulas);
                if (existingWorld == null) {
                    LinkedList<Formula> gammaFormulas = new LinkedList<>(transitiveFormulas);
                    numberOfWorlds++;
                    World newWorld = new World(gammaFormulas, numberOfWorlds);
                    worlds.add(newWorld);
                    addTransition(new Transition(world.getId(), newWorld.getId()));
                    world.allDeltaFormulasExpandedTo(newWorld.getId());
                } else {
                    addTransition(new Transition(world.getId(), existingWorld.getId()));
                }
            }

            else {
                LinkedList<Formula> gammaFormula = new LinkedList<>();
                gammaFormula.add(formula.getSubformulas().get(0));
                numberOfWorlds++;
                World newWorld = new World(gammaFormula, numberOfWorlds);
                worlds.add(newWorld);
                addTransition(new Transition(world.getId(), newWorld.getId()));
                if (system.isReflexive()) {
                    this.addTransition(new Transition(newWorld.getId(), newWorld.getId()));
                }
                if (system.isSymmetric()) {
                    this.addTransition(new Transition(newWorld.getId(), world.getId()));
                }
            }
        }

        // Delta Rule
        else if (operator == Operator.NECESSARILY || operator == Operator.NOTPOSSIBLY) {
            LinkedList<World> deltaWorlds = getDeltaWorldsFor(world, formula);
            for (World deltaWorld : deltaWorlds) {
                deltaWorld.addFormula(formula.getSubformulas().get(0));
            }
            if (!expandable()) {
                formula.tick();
            } else {
                untickAllDeltaFormulas();
            }
        }
    }

    private void serialise(World world) {

        // In this frame, create a transition from the world to itself
        addTransition(new Transition(world.getId(), world.getId()));

        // Create (|worlds| - 1) new frames as this one, and in each one create a transition from the world to a different world (other than itself)
        for (World otherWorld : worlds) {
            if (otherWorld.getId() != world.getId()) {
                Frame newFrame = new Frame(this, tableau, tableau.getNewFrameId(), system);
                newFrame.addTransition(new Transition(world.getId(), otherWorld.getId()));
                tableau.addFrame(newFrame);
            }
        }
    }

    private World getNonSerialWorld() {

        ArrayList<Integer> nonSerialWorldsIds = new ArrayList<>();

        // Initially we assume all worlds are non-serial
        for (World world : worlds) {
            nonSerialWorldsIds.add(world.getId());
        }

        // We discard worlds that have a transition to another world as non-serial
        for (Transition transition : transitions) {
            nonSerialWorldsIds.remove(transition.from());
        }

        if (nonSerialWorldsIds.isEmpty()) {
            return null;
        }
        return getWorldWithId(nonSerialWorldsIds.get(0));
    }

    private World worldContainingFormulas(HashSet<Formula> formulas) {

        // The first world containing all given formulas is returned, if any
        worldsLoop:
        for (World world : worlds) {
            for (Formula formula : formulas) {
                if (!world.containsFormula(formula)) {
                    continue worldsLoop;
                }
            }
            return world;
        }

        return null;
    }

    private void untickAllDeltaFormulas() {
        for (World world : worlds) {
            for (Formula formula : world.getFormulas()) {
                if (formula.getOperator() == Operator.NECESSARILY || formula.getOperator() == Operator.NOTPOSSIBLY) {
                    formula.untick();
                }
            }
        }
    }

    // Returns false if only propositions, negated propositions, []-formulas and ticked formulas are remaining in the frame, true otherwise
    private boolean expandable() {
        for (World world : worlds) {
            for (Formula formula : world.getFormulas()) {
                Operator operator = formula.getOperator();
                if (operator != Operator.NOT && operator != Operator.NONE && operator != Operator.NECESSARILY && operator != Operator.NOTPOSSIBLY) {
                    if (!formula.isTicked()) {
                        return true;
                    }
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

    private void addTransition(Transition transition) {
        if (transitionExists(transition)) {
            return;
        }
        transitions.add(transition);
    }

    private boolean transitionExists(Transition newTransition) {
        for (Transition transition : transitions) {
            if (transition.from() == newTransition.from() && transition.to() == newTransition.to()) {
                return true;
            }
        }
        return false;
    }

    private World getWorldWithId(int id) {
        for (World world : worlds) {
            if (world.getId() == id) {
                return world;
            }
        }
        return null;
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
}
