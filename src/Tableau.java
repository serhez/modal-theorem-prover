import java.util.LinkedList;

public class Tableau {

    private final Theorem theorem;
    private final ModalSystem system;
    private LinkedList<Frame> frames;
    private int frameIdCount;
    private Prover prover;
    private long startTime;

    public Tableau(Prover prover, Theorem theorem, ModalSystem system) {
        this.prover = prover;
        this.system = system;
        this.theorem = theorem;
        this.frames = new LinkedList<>();
        this.frameIdCount = 1;
        if (prover.isProtected()) {
            this.startTime = System.currentTimeMillis();
        }
    }

    // Returns true if the theorem is valid, false otherwise; if in protected mode, it may return null if it times out
    public Boolean run() {

        frames.add(new Frame(theorem.getFormulas(), this, frameIdCount, system));
        frameIdCount++;

        int step = 1;
        int fullyExpandedFrames = 0;   // TODO: DON'T NEED THIS
        while (fullyExpandedFrames != frames.size()) {
            if (prover.isProtected()) {
                long now = System.currentTimeMillis();
                // Time out
                if (now - startTime > prover.getTimeout()) {
                    return null;
                }
            }
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
                    if (!frame.isExpandable()) {
                        return Boolean.FALSE;  // If there is a non-expandable frame which contains no contradictions, we can finish
                    }
                    frames.add(frames.removeFirst());
                }
            }

            if (prover.isDebugging()) {
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
            }

            step++;
        }

        if (frames.isEmpty()) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
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
