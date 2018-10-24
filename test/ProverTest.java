import org.junit.jupiter.api.Test;

public class ProverTest {

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
    public void runRandomInputFile() {
        InputGenerator inputGenerator = new InputGenerator();
        inputGenerator.generateInputFile(10000, 50, "K");

        Prover prover = new Prover(false);
        prover.proveInputFile();
    }

    @Test
    public void runRandomInputFileAndTranslateToMolle() {
        InputGenerator inputGenerator = new InputGenerator();
        inputGenerator.generateInputFileAndMolleFile(10000, 50, "K");

        Prover prover = new Prover(false);
        prover.proveInputFile();
    }
}
