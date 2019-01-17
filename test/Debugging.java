import org.junit.jupiter.api.Test;

public class Debugging {

    @Test
    public void generateRandomInputFile() throws InvalidNumberOfPropositionsException {
        InputGenerator inputGenerator = new InputGenerator();
        inputGenerator.generateInputFile(1000000, 100, 5, "K");
    }

    @Test
    public void translateCurrentInputFileToMolle() {
        InputGenerator inputGenerator = new InputGenerator();
        inputGenerator.translateCurrentInputFileToMolle();
    }

    @Test
    public void runCurrentInputFile() throws IncompatibleFrameConditionsException {
        Prover prover = new Prover(false, false);
        prover.proveInputFile();
    }

    @Test
    public void debugCurrentInputFile() throws IncompatibleFrameConditionsException {
        Prover prover = new Prover(true, false);
        prover.proveInputFile();
    }

    @Test
    public void runRandomInputFile() throws InvalidNumberOfPropositionsException, IncompatibleFrameConditionsException {
        InputGenerator inputGenerator = new InputGenerator();
        inputGenerator.generateInputFile(1, 70000, 2, "K");
        Prover prover = new Prover(false, false);
        prover.proveInputFile();
    }

    @Test
    public void runRandomInputFileAndTranslateToMolle() throws InvalidNumberOfPropositionsException, IncompatibleFrameConditionsException {
        InputGenerator inputGenerator = new InputGenerator();
        inputGenerator.generateInputFileAndMolleFile(10000, 50, 2, "K");

        Prover prover = new Prover(false, false);
        prover.proveInputFile();
    }
}
