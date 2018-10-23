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
                case 'D': frameConditions.add(FrameCondition.D);    break;
                case 'T': frameConditions.add(FrameCondition.T);    break;
                case 'B': frameConditions.add(FrameCondition.B);    break;
                case '4': frameConditions.add(FrameCondition.FOUR); break;
                case '5': frameConditions.add(FrameCondition.FIVE); break;
                default :                                           break;
            }
        }
    }

    public boolean isSerial() {
        if (frameConditions.contains(FrameCondition.D)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isReflexive() {
        if (frameConditions.contains(FrameCondition.T)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isSymmetric() {
        if (frameConditions.contains(FrameCondition.B)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isTransitive() {
        if (frameConditions.contains(FrameCondition.FOUR)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEuclidean() {
        if (frameConditions.contains(FrameCondition.FIVE)) {
            return true;
        } else {
            return false;
        }
    }
}
