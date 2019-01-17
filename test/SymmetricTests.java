import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SymmetricTests {

    @Test
    public void correctness() throws IncompatibleFrameConditionsException {
        Prover prover = new Prover(true, false);
        ModalSystem systemB = new ModalSystem("B");
        ModalSystem systemK = new ModalSystem("K");
        String validFormula = "~((p & <>p) & [][]~p)";

        try {
            Assertions.assertTrue(prover.proveFormula(validFormula, systemB));   // Should be valid on System B
            Assertions.assertFalse(prover.proveFormula(validFormula, systemK));  // ... but invalid on System K
        } catch (UnrecognizableFormulaException e) {
            e.printStackTrace();
        }
    }
}