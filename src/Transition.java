public class Transition {

    private final int fromWorld;
    private final int toWorld;

    public Transition(int from, int to) {
        this.fromWorld = from;
        this.toWorld = to;
    }

    public int from() {
        return fromWorld;
    }

    public int to() {
        return toWorld;
    }
}
