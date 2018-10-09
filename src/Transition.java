public class Transition {

    private final World fromWorld;
    private final World toWorld;

    public Transition(World from, World to) {
        this.fromWorld = from;
        this.toWorld = to;
    }

    public World from() {
        return fromWorld;
    }

    public World to() {
        return toWorld;
    }
}
