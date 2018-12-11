import java.util.HashSet;

public class ModalSystem {

    private HashSet<FrameCondition> frameConditions;

    public ModalSystem(String frameConditionsString) {
        this.frameConditions = new HashSet<>();
        this.frameConditions.add(FrameCondition.K);  // All frames must conform to K
        parseFrameConditions(frameConditionsString);
    }

    private void parseFrameConditions(String frameConditionsString) {
        for (int i=0; i<frameConditionsString.length(); i++) {
            switch (frameConditionsString.charAt(i)) {
                case 'T': frameConditions.add(FrameCondition.T);    break;
                case 'B': frameConditions.add(FrameCondition.B);    break;
                case 'D': frameConditions.add(FrameCondition.D);    break;
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

    public boolean isTransitive() {
        if (frameConditions.contains(FrameCondition.FOUR)) {
            return true;
        }
        return false;
    }
}
