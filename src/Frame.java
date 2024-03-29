import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class Frame {

    private final int id;
    private final ModalLogic logic;
    private final Tableau tableau;
    private LinkedList<World> worlds;
    private int currentWorldId;
    private HashSet<Transition> transitions;
    private int worldIdCount;
    private boolean isExpandable;

    public Frame(LinkedList<Formula> initialFormulas, Tableau tableau, int id, ModalLogic logic) {
        this.id = id;
        this.logic = logic;
        this.isExpandable = true;
        this.tableau = tableau;
        this.worlds = new LinkedList<>();
        this.currentWorldId = 0;
        worldIdCount = 1;
        World initialWorld = new World(initialFormulas, worldIdCount);
        if (logic.isSerial()) {
            Formula fmla1 = new Formula("T");
            Formula fmla2 = new Formula("<>T");
            fmla1.parse();
            fmla2.parse();
            initialWorld.addFormula(fmla1);
            initialWorld.addFormula(fmla2);
        }
        this.worlds.add(initialWorld);
        this.transitions = new HashSet<>();
        if (logic.isReflexive()) {
            this.addTransition(new Transition(1, 1));
        }
    }

    public Frame(Frame originalFrame, Tableau tableau, int id, ModalLogic specification) {
        this.id = id;
        this.logic = specification;
        this.isExpandable = true;
        this.tableau = tableau;
        this.worldIdCount = originalFrame.getWorldIdCount();
        this.worlds = originalFrame.cloneWorlds();
        this.currentWorldId = originalFrame.currentWorldId;
        this.transitions = originalFrame.cloneTransitions();
    }

    public void scheduleNextExpansion() {

        if (hasContradiction()) {
            isExpandable = false;
            return;
        }

        // We prioritise expanding negated boolean formulas, then alpha, then beta, then delta and finally gamma
        World chosenWorld = null;
        Formula chosenFormula = null;
        int i, j, k, noFormulas;
        int noWorlds = worlds.size();

        HashSet<FormulaType> types = new HashSet<>();

        outerLoop:
        for (i=1; i<=5; i++) {
            types.clear();
            switch (i) {
                case 1:
                    types.add(FormulaType.NOTTRUE);
                    types.add(FormulaType.NOTFALSE);
                    break;
                case 2:
                    types.add(FormulaType.AND);
                    types.add(FormulaType.NOTOR);
                    types.add(FormulaType.BICONDITION);
                    types.add(FormulaType.NOTCONDITION);
                    break;
                case 3:
                    types.add(FormulaType.OR);
                    types.add(FormulaType.NOTAND);
                    types.add(FormulaType.CONDITION);
                    types.add(FormulaType.NOTBICONDITION);
                    break;
                case 4:
                    types.add(FormulaType.POSSIBLY);
                    types.add(FormulaType.NOTNECESSARILY);
                    break;
                case 5:
                    types.add(FormulaType.NECESSARILY);
                    types.add(FormulaType.NOTPOSSIBLY);
                    break;
            }
            for (j=0; j<noWorlds; j++) {
                World world = worlds.getFirst();
                LinkedList<Formula> formulas = world.getFormulas();
                noFormulas = formulas.size();
                for (k=0; k<noFormulas; k++) {
                    Formula formula = formulas.getFirst();
                    if (types.contains(formula.getType())) {
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

        // Only booleans, propositions, negated propositions and ticked gamma-formulas remaining on the frame
        if (chosenFormula == null) {
            isExpandable = false;
            return;
        }

        expand(chosenFormula, chosenWorld);
    }

    private void expand(Formula formula, World world) {

        switch (formula.getType()) {

            // Negated Boolean Rule
            case NOTTRUE:
            case NOTFALSE:
                expandNegatedBoolean(formula, world);
                break;

            // Alpha Rule
            case AND:
            case NOTOR:
            case NOTCONDITION:
            case BICONDITION:
                expandAlpha(formula, world);
                break;

            // Beta Rule
            case OR:
            case NOTAND:
            case CONDITION:
            case NOTBICONDITION:
                expandBeta(formula, world);
                break;

            // Delta Rule
            case POSSIBLY:
            case NOTNECESSARILY:
                expandDelta(formula, world);
                break;

            // Gamma Rule
            case NECESSARILY:
            case NOTPOSSIBLY:
                expandGamma(formula, world);
                break;
        }
    }

    private void expandNegatedBoolean(Formula formula, World world) {
        world.addFormula(formula.getSubformulas().get(0));
        formula.tick();
    }

    private void expandAlpha(Formula formula, World world) {
        world.addFormula(formula.getSubformulas().get(0));
        world.addFormula(formula.getSubformulas().get(1));
        formula.tick();
    }

    private void expandBeta(Formula formula, World world) {
        Formula subformula1 = formula.getSubformulas().get(0);
        Formula subformula2 = formula.getSubformulas().get(1);
        formula.tick();
        Frame disjunctiveFrame = new Frame(this, tableau, tableau.getNewFrameId(), logic);
        world.addFormula(subformula1);
        disjunctiveFrame.addFormula(subformula2);
        tableau.addFrame(disjunctiveFrame);
    }

    private void expandDelta(Formula formula, World world) {
        formula.tick();
        HashSet<Formula> expandingFormulas;
        if (logic.isTransitive()) {  // Also applies to linearity
            expandingFormulas = world.getTransitiveDeltaExpansionFormulas(formula);
        } else {
            expandingFormulas = world.getKripkeDeltaExpansionFormulas(formula);
        }

        World existingWorld;
        if (logic.isSymmetric()) {  // Need to also check that the "to" world will not introduce a contradiction in the "from" world when applying symmetry
            existingWorld = worldSymmetricallyCompatible(expandingFormulas, world);
        } else if (logic.isLinear()) {  // Need to also check that the "to" world will be in the future
            existingWorld = worldLinearlyCompatible(expandingFormulas, world);
        } else {
            existingWorld = worldContainingFormulas(expandingFormulas);
        }
        if (existingWorld == null) {
            LinkedList<Formula> formulas = new LinkedList<>(expandingFormulas);
            worldIdCount++;
            World newWorld = new World(formulas, worldIdCount);
            if (logic.isSerial()) {
                Formula fmla1 = new Formula("T");
                Formula fmla2 = new Formula("<>T");
                fmla1.parse();
                fmla2.parse();
                newWorld.addFormula(fmla1);
                newWorld.addFormula(fmla2);
            }
            worlds.add(newWorld);
            world.allCurrentGammaFormulasExpandedTo(newWorld.getId());
            if (logic.isLinear()) {
                HashSet<Integer> visitedWorlds = new HashSet<>();
                visitedWorlds.add(world.getId());
                HashSet<World> futureWorlds = futureWorlds(world, visitedWorlds);
                HashSet<Formula> necessaryFormulas;
                HashSet<Formula> newWorldGammaFormulas;
                World leftWorld = null;   // The world we will place at the left of the new world in the "line"
                World rightWorld = null;  // The world we will place at the right of the new world in the "line"
                positionSearch:
                for (World fromWorld : futureWorlds) {
                    necessaryFormulas = fromWorld.getGammaExpansionFormulas(logic);
                    if (!newWorld.containsFormulas(necessaryFormulas)) {
                        continue positionSearch;
                    }
                    World toWorld = linearlyAdjacentWorld(fromWorld);  // Can be null if it is the last world in the "line"
                    newWorldGammaFormulas = newWorld.getGammaExpansionFormulas(logic);
                    if (toWorld == null || !toWorld.containsFormulas(newWorldGammaFormulas)) { // If toWorld == null then it is the last world in the "line"
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
            if (logic.isSymmetric()) {
                this.addTransition(new Transition(newWorld.getId(), world.getId()));
            }
            if (logic.isReflexive()) {
                this.addTransition(new Transition(newWorld.getId(), newWorld.getId()));
            }
        } else {
            if (!logic.isTransitive()) {  // We avoid creating transitions to non-adjacent future worlds since we imply many transitions
                addTransition(new Transition(world.getId(), existingWorld.getId()));
            }
        }
    }

    private void expandGamma(Formula formula, World world) {
        HashSet<World> gammaWorlds;
        if (logic.isTransitive()) {
            HashSet<Integer> visitedWorlds = new HashSet<>();
            gammaWorlds = reachableWorlds(world, visitedWorlds);  // Necessary since we imply many transitions in transitive frames (not explicit)
        } else {
            gammaWorlds = getGammaWorldsFor(world, formula);
        }
        for (World gammaWorld : gammaWorlds) {
            gammaWorld.addFormula(formula.getSubformulas().get(0));
        }
        if (!expandable()) {
            formula.tick();
        } else {
            untickAllGammaFormulas();
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
            HashSet<Formula> newWorldFormulas = newWorld.getGammaExpansionFormulas(logic);
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
        for (World world : futureWorlds) {
            if (worldsAreLinearlyCompatible(formulas, world)) {
                return world;
            }
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

    private void untickAllGammaFormulas() {
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

    private HashSet<World> getGammaWorldsFor(World world, Formula formula) {
        HashSet<World> gammaWorlds = new HashSet<>();
        for (Transition transition : transitions) {
            if (transition.from() == world.getId() && !formula.getWorldsExpandedTo().contains(transition.to())) {
                gammaWorlds.add(getWorldWithId(transition.to()));
                formula.addWorldExpandedTo(transition.to());
            }
        }
        return gammaWorlds;
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
