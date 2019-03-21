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
    private int worldIdCount;
    private boolean isExpandable;

    public Frame(LinkedList<Formula> initialFormulas, Tableau tableau, int id, ModalSystem system) {
        this.id = id;
        this.system = system;
        this.isExpandable = true;
        this.tableau = tableau;
        this.worlds = new LinkedList<>();
        this.currentWorldId = 0;
        worldIdCount = 1;
        World initialWorld = new World(initialFormulas, worldIdCount);
        if (system.isSerial()) {
            initialWorld.addFormula(new Formula("T"));
            initialWorld.addFormula(new Formula("<>T"));
        }
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
        this.worldIdCount = originalFrame.getWorldIdCount();
        this.worlds = originalFrame.cloneWorlds();
        this.currentWorldId = originalFrame.currentWorldId;
        this.transitions = originalFrame.cloneTransitions();
    }

    public void expandNextFormula() {

        if (hasContradiction()) {
            isExpandable = false;
            return;
        }

        // We prioritise expanding negated boolean formulas, then alpha, then beta, then gamma and finally delta
        World chosenWorld = null;
        Formula chosenFormula = null;
        int seenFormulas;
        int totalFormulas;
        int seenWorlds = 0;
        int totalWorlds = worlds.size();

        // Negated Boolean
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
                if (formula.getType() == FormulaType.NOTTRUE || formula.getType() == FormulaType.NOTFALSE) {
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
                if (formula.getType() == FormulaType.AND || formula.getType() == FormulaType.BICONDITION || formula.getType() == FormulaType.NOTOR || formula.getType() == FormulaType.NOTCONDITION) {
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
                    if (formula.getType() == FormulaType.OR || formula.getType() == FormulaType.NOTAND || formula.getType() == FormulaType.CONDITION || formula.getType() == FormulaType.NOTBICONDITION) {
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
                    if (formula.getType() == FormulaType.POSSIBLY || formula.getType() == FormulaType.NOTNECESSARILY) {
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
                    if (formula.getType() == FormulaType.NECESSARILY || formula.getType() == FormulaType.NOTPOSSIBLY) {
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

        // Only booleans, propositions, negated propositions and ticked delta-formulas remaining on the frame
        if (chosenWorld == null || chosenFormula == null) {
            isExpandable = false;
            return;
        }

        expand(chosenFormula, chosenWorld);
    }

    private void expand(Formula formula, World world) {

        FormulaType type = formula.getType();

        // Negated Boolean Rule
        if (type == FormulaType.NOTTRUE || type == FormulaType.NOTFALSE) {
            world.addFormula(formula.getSubformulas().get(0));
            formula.tick();
        }

        // Alpha Rule
        if (type == FormulaType.AND || type == FormulaType.BICONDITION || type == FormulaType.NOTOR || type == FormulaType.NOTCONDITION) {
            world.addFormula(formula.getSubformulas().get(0));
            world.addFormula(formula.getSubformulas().get(1));
            formula.tick();
        }

        // Beta Rule
        else if (type == FormulaType.OR || type == FormulaType.NOTAND || type == FormulaType.NOTBICONDITION || type == FormulaType.CONDITION) {
            Formula subformula1 = formula.getSubformulas().get(0);
            Formula subformula2 = formula.getSubformulas().get(1);
            formula.tick();
            Frame disjunctiveFrame = new Frame(this, tableau, tableau.getNewFrameId(), system);
            world.addFormula(subformula1);
            disjunctiveFrame.addFormula(subformula2);
            tableau.addFrame(disjunctiveFrame);
        }

        // Gamma Rule
        else if (type == FormulaType.POSSIBLY || type == FormulaType.NOTNECESSARILY) {

            formula.tick();

            // The reason this works is because you always choose <> after & and |. Hence, when expanding a <> formula,
            // you only have props, neg props, <> and [] remaining in the frame. Thus, it's not possible that by expanding
            // <>p, you create a world with p and (e.g.) (q&~p), and then you expand the later formula and found a contradiction
            // that if you would have created a new world, you would not have found. BUT: what happens if you expand
            // <>(q&~p) into a world with p? YOU CAN'T, because in order to do that, (q&~p) would need to be in that world
            // already (so they would have been expanded before and found the contradiction anyway) NICE!

            // NO! What if you had a world w with a ticked formula ~(~p->q) (hence also formulas ~p and ~q) and you expand
            // the gamma formula <>p into w? STILL OK, because in order to expand <>p to w, w needs to have p (and possibly other formulas),
            // hence the contradiction introduced by ~p and p would have already existed (and likely been found) in w before.

            HashSet<Formula> expandingFormulas;
            if (system.isTransitive()) {  // Also applies to linearity
                expandingFormulas = world.getTransitiveGammaExpansionFormulas(formula);
            } else {
                expandingFormulas = world.getKripkeGammaExpansionFormulas(formula);
            }

            World existingWorld;
            if (system.isSymmetric()) {  // Need to also check that the "to" world will not introduce a contradiction in the "from" world when applying symmetry
                existingWorld = worldSymmetricallyCompatible(expandingFormulas, world);
            } else if (system.isLinear()) {  // Need to also check that the "to" world will be in the future
                existingWorld = worldLinearlyCompatible(expandingFormulas, world);
            } else {
                existingWorld = worldContainingFormulas(expandingFormulas);
            }
            if (existingWorld == null) {
                LinkedList<Formula> formulas = new LinkedList<>(expandingFormulas);
                worldIdCount++;
                World newWorld = new World(formulas, worldIdCount);
                if (system.isSerial()) {
                    newWorld.addFormula(new Formula("T"));
                    newWorld.addFormula(new Formula("<>T"));
                }
                worlds.add(newWorld);
                world.allCurrentDeltaFormulasExpandedTo(newWorld.getId());
                if (system.isLinear()) {
                    // TODO: If none of the future worlds have the necessary formulas, then the new world will be placed
                    // TODO: ... at the end of the "line". However, is it possible that, in some frame, placing it at the
                    // TODO: ... end of the "line" produces eventually a contradiction, but having it placed at some other
                    // TODO: ... valid position would have not, even if at such position the adjacent worlds did not have
                    // TODO: ... the necessary formulas within them?
                    HashSet<Integer> visitedWorlds = new HashSet<>();
                    visitedWorlds.add(world.getId());
                    HashSet<World> futureWorlds = futureWorlds(world, visitedWorlds);
                    HashSet<Formula> necessaryFormulas;
                    HashSet<Formula> newWorldDeltaFormulas;
                    World leftWorld = null;   // The world we will place at the left of the new world in the "line"
                    World rightWorld = null;  // The world we will place at the right of the new world in the "line"
                    positionSearch:
                    for (World fromWorld : futureWorlds) {
                        necessaryFormulas = fromWorld.getDeltaExpansionFormulas(system);
                        if (!newWorld.containsFormulas(necessaryFormulas)) {
                            continue positionSearch;
                        }
                        World toWorld = linearlyAdjacentWorld(fromWorld);  // Can be null if it is the last world in the "line"
                        newWorldDeltaFormulas = newWorld.getDeltaExpansionFormulas(system);
                        if (toWorld == null || !toWorld.containsFormulas(newWorldDeltaFormulas)) { // If toWorld == null then it is the last world in the "line"
                            continue positionSearch;
                        }
                        leftWorld = fromWorld;
                        rightWorld = toWorld;
                    }
                    if (!(leftWorld == null)) {
                        // Remove previous transition
                        removeTransition(leftWorld.getId(), rightWorld.getId());
                        // Create new transitions
                        transitions.add(new Transition(leftWorld.getId(), newWorld.getId()));
                        transitions.add(new Transition(newWorld.getId(), rightWorld.getId()));
                    } else {
                        // We find the last world in the "line"
                        World lastWorld = null;  // We assume we will always find a last world
                        lastWorldSearch:
                        for (World futureWorld : futureWorlds) {
                            for (Transition transition : transitions) {
                                if (transition.from() == futureWorld.getId() && transition.to() != world.getId()) {  // We don't consider loop transitions
                                    continue lastWorldSearch;
                                }
                            }
                            lastWorld = futureWorld;
                            break lastWorldSearch;
                        }
                        // We place the new world at the end of the "line"
                        transitions.add(new Transition(lastWorld.getId(), newWorld.getId()));
                    }
                } else {
                    addTransition(new Transition(world.getId(), newWorld.getId()));
                }
                if (system.isSymmetric()) {
                    this.addTransition(new Transition(newWorld.getId(), world.getId()));
                }
                if (system.isReflexive()) {
                    this.addTransition(new Transition(newWorld.getId(), newWorld.getId()));
                }
            } else {
                if (!system.isTransitive()) {  // We avoid creating transitions to non-adjacent future worlds since we imply many transitions
                    addTransition(new Transition(world.getId(), existingWorld.getId()));
                }
            }
        }

        // Delta Rule
        else if (type == FormulaType.NECESSARILY || type == FormulaType.NOTPOSSIBLY) {
            HashSet<World> deltaWorlds;
            if (system.isTransitive()) {
                HashSet<Integer> visitedWorlds = new HashSet<>();
                visitedWorlds.add(world.getId());
                deltaWorlds = reachableWorlds(world, visitedWorlds);  // Necessary since we imply many transitions in transitive frames (not explicit)
            } else {
                deltaWorlds = getDeltaWorldsFor(world, formula);
            }
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

    private void removeTransition(int from, int to) {
        for (Transition transition : transitions) {
            if (transition.from() == from && transition.to() == to) {
                transitions.remove(transition);
            }
        }
    }

    // Returns the adjacent world to any world in a linear frame, assuming there is only such adjacent world
    private World linearlyAdjacentWorld(World world) {
        for (Transition transition : transitions) {
            if (transition.from() == world.getId() && transition.to() != world.getId()) {  // Not counting loop transitions
                return getWorldWithId(transition.to());
            }
        }
        return null;
    }

    private World worldSymmetricallyCompatible(HashSet<Formula> formulas, World currentWorld) {

        // The first world containing all given formulas is returned, if any
        worldsLoop:
        for (World newWorld : worlds) {
            for (Formula formula : formulas) {
                if (!newWorld.containsFormula(formula)) {
                    continue worldsLoop;
                }
            }
            HashSet<Formula> newWorldFormulas = newWorld.getDeltaExpansionFormulas(system);
            for (Formula newWorldFormula : newWorldFormulas) {
                if (!currentWorld.containsFormula(newWorldFormula)) {
                    continue worldsLoop;
                }
            }
            return newWorld;
        }

        return null;
    }

    private HashSet<World> reachableWorlds(World currentWorld, HashSet<Integer> visitedWorlds) {
        HashSet<World> reachableWorlds = new HashSet<>();

        for (World world : worlds) {
            for (Transition transition : transitions) {
                if (transition.from() == currentWorld.getId() && transition.to() == world.getId()) {
                    if (!visitedWorlds.contains(currentWorld.getId())) {
                        visitedWorlds.add(currentWorld.getId());
                        reachableWorlds.add(world);
                        reachableWorlds.addAll(reachableWorlds(world, visitedWorlds));
                    }
                }
            }
        }

        return reachableWorlds;
    }

    private HashSet<World> futureWorlds(World currentWorld, HashSet<Integer> visitedWorlds) {

        HashSet<World> futureWorlds = new HashSet<>();
        futureWorlds.add(currentWorld);  // Difference with reachableWorlds() method is that the current world is always a future world

        reachableWorlds(currentWorld, visitedWorlds);

        return futureWorlds;
    }

    private boolean worldsAreLinearlyCompatible(HashSet<Formula> formulas, World toWorld) {
        for (Formula formula : formulas) {
            if (!toWorld.containsFormula(formula)) {
                return false;
            }
        }
        return true;
    }

    private World worldLinearlyCompatible(HashSet<Formula> formulas, World currentWorld) {
        HashSet<Integer> visitedWorlds = new HashSet<>();
        visitedWorlds.add(currentWorld.getId());
        HashSet<World> futureWorlds = futureWorlds(currentWorld, visitedWorlds);
        // The first world containing all given formulas is returned, if any
        worldsLoop:
        for (World world : futureWorlds) {
            if (worldsAreLinearlyCompatible(formulas, world)) {
                continue worldsLoop;
            }
            return world;
        }
        return null;
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
                if (formula.getType() == FormulaType.NECESSARILY || formula.getType() == FormulaType.NOTPOSSIBLY) {
                    formula.untick();
                }
            }
        }
    }

    // Returns false if only propositions, negated propositions, booleans, []-formulas and ticked formulas are remaining in the frame, true otherwise
    private boolean expandable() {
        for (World world : worlds) {
            for (Formula formula : world.getFormulas()) {
                FormulaType type = formula.getType();
                if (type != FormulaType.TRUE && type != FormulaType.FALSE && type != FormulaType.NOTPROPOSITION && type != FormulaType.PROPOSITION && type != FormulaType.NECESSARILY && type != FormulaType.NOTPOSSIBLY) {
                    if (!formula.isTicked()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private HashSet<World> getDeltaWorldsFor(World world, Formula formula) {
        HashSet<World> deltaWorlds = new HashSet<>();
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

    public int getWorldIdCount() {
        return worldIdCount;
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
