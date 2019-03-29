public class Prove {
    public static void main(String args[]) {
        Prover prover = new Prover();
        try {
            prover.proveInputFile();
        } catch (IncompatibleFrameConditionsException e) {
            e.printMessage();
        }
    }
}
