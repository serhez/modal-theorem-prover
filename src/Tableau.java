import java.util.LinkedList;

public class Tableau {

    private final FormulaArray formulaArray;
    private final ModalLogic logic;
    private LinkedList<Frame> frames;
    private int frameIdCount;
    private Prover prover;
    private long startTime;

    public Tableau(Prover prover, FormulaArray formulaArray, ModalLogic logic) {
        this.prover = prover;
        this.logic = logic;
        this.formulaArray = formulaArray;
        this.frames = new LinkedList<>();
        this.frameIdCount = 1;
        if (prover.isProtected()) {
            this.startTime = System.currentTimeMillis();
        }
    }

    // Returns true if the formulaArray is valid, false otherwise; if in protected mode, it may return null if it times out
    public Boolean run() {

        frames.add(new Frame(formulaArray.getFormulas(), this, frameIdCount, logic));
        frameIdCount++;

        int step = 1;
        while (!frames.isEmpty()) {
            if (prover.isProtected()) {
                long now = System.currentTimeMillis();
                // Time out
                if (now - startTime > prover.getTimeout()) {
                    return null;
                }
            }
            if (!frames.isEmpty()) {
                Frame frame = frames.getFirst();
                frame.scheduleNextExpansion();
                if (frame.hasContradiction()) {
                    frames.removeFirst();
                } else {
                    if (!frame.isExpandable()) {
                        return Boolean.FALSE;  // Found a frame which satisfies the formula
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

        return Boolean.TRUE;  // No frame which satisfies the formula was found
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
