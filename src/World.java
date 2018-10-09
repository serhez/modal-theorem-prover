import java.util.HashSet;

public class World {

    private HashSet<String> propositions;
    private HashSet<String> negatedPropositions;

    public World() {
        this.propositions = new HashSet<>();
        this.negatedPropositions = new HashSet<>();
    }

    public void addProposition(String p) {
        propositions.add(p);
    }

    public void addNegatedProposition(String p) {
        negatedPropositions.add(p);
    }

    public boolean hasProposition(String p) {
        if (propositions.contains(p)) {
            return true;
        }
        return false;
    }

    public boolean hasNegatedProposition(String p) {
        if (negatedPropositions.contains(p)) {
            return true;
        }
        return false;
    }
}
