import java.util.ArrayList;
import java.util.HashSet;

public class ModalSystem {

    private HashSet<FrameCondition> frameConditions;

    public ModalSystem(String frameConditionsString) throws IncompatibleFrameConditionsException {
        this.frameConditions = new HashSet<>();
        this.frameConditions.add(FrameCondition.K);  // All frames must conform to K
        parseFrameConditions(frameConditionsString);
        addNecessaryConditions();  // Because some frame conditions imply other conditions (e.g.: linearity implies transitivity)
        findIncompatibilities();
    }

    private void addNecessaryConditions() {
        if (isLinear() && !isTransitive()) {
            frameConditions.add(FrameCondition.FOUR);
        }
    }

    private void findIncompatibilities() throws IncompatibleFrameConditionsException {
        if (isLinear() && isSymmetric()) {
            ArrayList<String> incompatibleConditions = new ArrayList<>();
            incompatibleConditions.add("L");
            incompatibleConditions.add("B");
            throw new IncompatibleFrameConditionsException(incompatibleConditions);

        }
    }

    private void parseFrameConditions(String frameConditionsString) {
        for (int i=0; i<frameConditionsString.length(); i++) {
            switch (frameConditionsString.charAt(i)) {
                case 'T': frameConditions.add(FrameCondition.T);    break;
                case 'B': frameConditions.add(FrameCondition.B);    break;
                case 'D': frameConditions.add(FrameCondition.D);    break;
                case 'L': frameConditions.add(FrameCondition.L);    break;
                case '4': frameConditions.add(FrameCondition.FOUR); break;
                default :                                           break;
            }
        }
    }

    public boolean isReflexive() {
        if (frameConditions.contains(FrameCondition.T)) {
            return true;
        }
        return false;
    }

    public boolean isSymmetric() {
        if (frameConditions.contains(FrameCondition.B)) {
            return true;
        }
        return false;

    }

    public boolean isSerial(){
        if (frameConditions.contains(FrameCondition.D)) {
            return true;
        }
        return false;
    }

    public boolean isLinear(){
        if (frameConditions.contains(FrameCondition.L)) {
            return true;
        }
        return false;
    }

    public boolean isTransitive() {
        if (frameConditions.contains(FrameCondition.FOUR)) {
            return true;
        }
        return false;
    }
}
