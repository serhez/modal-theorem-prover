import java.util.HashSet;
import java.util.Iterator;

public class Tableau {

    private final Theorem theorem;
    private HashSet<Frame> frames;

    public Tableau(Theorem theorem) {
        this.theorem = theorem;
        this.frames = new HashSet<>();
    }

    // Returns true if the theorem is valid, false otherwise
    public boolean run() {

        boolean allFormulasExpanded = false;
        frames.add(new Frame(theorem.getFormulas(), this));
        while (!allFormulasExpanded) {
            allFormulasExpanded = true;
            for (Iterator<Frame> iterator = frames.iterator(); iterator.hasNext();) {
                Frame frame = iterator.next();
                if (frame.expandNextFormula()) {
                    allFormulasExpanded = false;
                }
                if (frame.hasContradiction()) {
                    frames.remove(frame);
                }
            }
        }

        if (frames.isEmpty()) {
            return true;
        }

        return false;
    }

    public void addFrame(Frame frame) {
        frames.add(frame);
    }

}
