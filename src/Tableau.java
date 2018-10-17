import java.util.HashSet;

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
            for (Frame frame : frames) {
                if (frame.expandNextFormula()) {
                    allFormulasExpanded = false;
                }
                if (frame.hasContradiction()) {
                    frames.remove(frame);
                    break;  // break from the for-loop to evade Concurrent Modification Exceptions
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

    public void print() {
        int count = 1;
        for (Frame frame : frames) {
            System.out.println();
            System.out.println();
            System.out.println("----------------------------------------------------------------------");
            System.out.println("\t\t\t\t\t\t\t  Frame " + count);
            System.out.println("----------------------------------------------------------------------");
            count++;
            frame.print();
        }
    }
}
