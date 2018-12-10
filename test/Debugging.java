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
    public void runCurrentInputFile() {
        Prover prover = new Prover(false);
        prover.proveInputFile();
    }

    @Test
    public void debugCurrentInputFile() {
        Prover prover = new Prover(true);
        prover.proveInputFile();
    }

    @Test
    public void runRandomInputFile() throws InvalidNumberOfPropositionsException {
        InputGenerator inputGenerator = new InputGenerator();
        inputGenerator.generateInputFile(100000, 100, 2, "K");
        Prover prover = new Prover(false);
        prover.proveInputFile();
    }

    @Test
    public void runRandomInputFileAndTranslateToMolle() throws InvalidNumberOfPropositionsException {
        InputGenerator inputGenerator = new InputGenerator();
        inputGenerator.generateInputFileAndMolleFile(10000, 50, 2, "K");

        Prover prover = new Prover(false);
        prover.proveInputFile();
    }
}
