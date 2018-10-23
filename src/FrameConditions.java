import java.util.HashSet;

public class FrameConditions {

    private HashSet<FrameCondition> frameConditions;

    public FrameConditions(String frameConditionsString) {
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

    public boolean hasSeriality() {
        if (frameConditions.contains(FrameCondition.D)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasReflexivity() {
        if (frameConditions.contains(FrameCondition.T)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasSymmetry() {
        if (frameConditions.contains(FrameCondition.B)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasTransitivity() {
        if (frameConditions.contains(FrameCondition.FOUR)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasEuclidean() {
        if (frameConditions.contains(FrameCondition.FIVE)) {
            return true;
        } else {
            return false;
        }
    }
}
