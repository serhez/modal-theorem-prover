import java.util.LinkedList;

public class Tableau {

    private final Theorem theorem;
    private LinkedList<Frame> frames;

    public Tableau(Theorem theorem) {
        this.theorem = theorem;
        this.frames = new LinkedList<>();
    }

    // Returns true if the theorem is valid, false otherwise
    public boolean run() {

        boolean allFormulasExpanded = false;
        frames.add(new Frame(theorem.getFormulas(), this));
        while (!allFormulasExpanded) {
            allFormulasExpanded = true;
            if (!frames.isEmpty()) {
                Frame frame = frames.getFirst();
                if (frame.expandNextFormula()) {
                    allFormulasExpanded = false;
                }
                if (frame.hasContradiction()) {
                    frames.removeFirst();
                } else {
                    frames.add(frames.removeFirst());
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
