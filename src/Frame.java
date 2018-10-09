import java.util.HashSet;

public class Frame {

    private World currentWorld;
    private HashSet<World> worlds;
    private HashSet<Transition> transitions;

    public Frame() {
        this.currentWorld = new World();
        this.worlds = new HashSet<>();
        this.worlds.add(currentWorld);
        this.transitions = new HashSet<>();
    }

}
