import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProverTest {

    // Current Input File
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

    // System K
    @Test
    public void runRandomInputFileForSystemK() {
        InputGenerator inputGenerator = new InputGenerator();
        inputGenerator.generateInputFile(10000, 50, "K");

        Prover prover = new Prover(false);
        prover.proveInputFile();
    }

    @Test
    public void runRandomInputFileAndTranslateToMolleForSystemK() {
        InputGenerator inputGenerator = new InputGenerator();
        inputGenerator.generateInputFileAndMolleFile(10000, 50, "K");

        Prover prover = new Prover(false);
        prover.proveInputFile();
    }

    // System T
    @Test
    public void testReflexiveFormulas() {
        Prover prover = new Prover(false);
        String validFormula = "~(~p & []p)";
        ModalSystem systemT = new ModalSystem("T");
        ModalSystem systemK = new ModalSystem("K");

        try {
            Assertions.assertTrue(prover.proveFormula(validFormula, systemT));   // Should be valid on System T
            Assertions.assertFalse(prover.proveFormula(validFormula, systemK));  // ... but invalid on System K
        } catch (UnrecognizableFormulaException e) {
            e.printStackTrace();
        }
    }

    // System B
    @Test
    public void testSymmetricFormulas() {
        Prover prover = new Prover(false);
        String validFormula = "~((p & <>p) & [][]~p)";
        ModalSystem systemB = new ModalSystem("B");
        ModalSystem systemK = new ModalSystem("K");

        try {
            Assertions.assertTrue(prover.proveFormula(validFormula, systemB));   // Should be valid on System B
            Assertions.assertFalse(prover.proveFormula(validFormula, systemK));  // ... but invalid on System K
        } catch (UnrecognizableFormulaException e) {
            e.printStackTrace();
        }
    }
}
