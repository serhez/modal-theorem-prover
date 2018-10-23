import org.junit.jupiter.api.Test;

public class ProverTest {

    @Test
    public void translateCurrentInputFileToMolle() {
        InputGenerator inputGenerator = new InputGenerator(0, 0);
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
        InputGenerator inputGenerator = new InputGenerator(1000, 50);
        inputGenerator.generateInputFile();

        Prover prover = new Prover(false);
        prover.proveInputFile();
    }

    @Test
    public void runRandomInputFileAndTranslateToMolle() {
        InputGenerator inputGenerator = new InputGenerator(1000, 50);
        inputGenerator.generateInputFileAndMolleFile();

        Prover prover = new Prover(false);
        prover.proveInputFile();
    }
}
