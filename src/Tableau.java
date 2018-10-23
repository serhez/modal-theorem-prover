import java.util.LinkedList;

public class Tableau {

    private final Theorem theorem;
    private LinkedList<Frame> frames;
    private int frameIdCount;

    public Tableau(Theorem theorem) {
        this.theorem = theorem;
        this.frames = new LinkedList<>();
        this.frameIdCount = 1;
    }

    // Returns true if the theorem is valid, false otherwise
    public boolean run() {

        frames.add(new Frame(theorem.getFormulas(), this, frameIdCount));
        frameIdCount++;

        int step = 1;
        int fullyExpandedFrames = 0;
        while (fullyExpandedFrames != frames.size()) {
            if (!frames.isEmpty()) {
                Frame frame = frames.getFirst();
                frame.expandNextFormula();
                if (!frame.isExpandable()) {
                    fullyExpandedFrames++;
                }
                if (frame.hasContradiction()) {
                    frames.removeFirst();
                    if (!frame.isExpandable()) {
                        fullyExpandedFrames--;
                    }
                } else {
                    frames.add(frames.removeFirst());
                }
            }

            /*
            // Debugging //
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("**********************************************************************");
            System.out.println("**********************************************************************");
            System.out.println("\t\t\t\t\t\t\t   STEP " + step);
            System.out.println("**********************************************************************");
            System.out.println("**********************************************************************");
            System.out.println();
            System.out.println();
            print();
            // Debugging //
            */

            step++;
        }

        if (frames.isEmpty()) {
            return true;
        }

        return false;
    }

    public void addFrame(Frame frame) {
        frames.add(frame);
        frameIdCount++;
    }

    public int getNewFrameId() {
        return frameIdCount;
    }

    public void print() {
        for (Frame frame : frames) {
            System.out.println();
            System.out.println();
            System.out.println("----------------------------------------------------------------------");
            System.out.println("\t\t\t\t\t\t\t  Frame " + frame.getId());
            System.out.println("----------------------------------------------------------------------");
            frame.print();
        }
    }
}
