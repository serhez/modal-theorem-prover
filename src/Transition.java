public class Transition {

    private final int fromWorldId;
    private final int toWorldId;

    public Transition(int from, int to) {
        this.fromWorldId = from;
        this.toWorldId = to;
    }

    public int from() {
        return fromWorldId;
    }

    public int to() {
        return toWorldId;
    }
}
