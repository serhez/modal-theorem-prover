import org.junit.jupiter.api.Test;

public class ProverTest {

    @Test
    public void runCurrentInputFile() {
        Prover prover = new Prover();
        prover.proveInputFile();
    }

    @Test
    public void runRandomInputFile() {
        InputGenerator inputGenerator = new InputGenerator(1000, 50);
        inputGenerator.generateInputFile();

        Prover prover = new Prover();
        prover.proveInputFile();
    }
}
