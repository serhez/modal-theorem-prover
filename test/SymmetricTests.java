import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SymmetricTests {

    @Test
    public void correctness() throws IncompatibleFrameConditionsException {
        Prover prover = new Prover();
        ModalLogic logicB = new ModalLogic("B");
        ModalLogic logicK = new ModalLogic("K");
        String validFormula = "~((p & <>p) & [][]~p)";

        try {
            Assertions.assertTrue(prover.proveFormula(validFormula, logicB));   // Should be valid on logic B
            Assertions.assertFalse(prover.proveFormula(validFormula, logicK));  // ... but invalid on logic K
        } catch (UnrecognizableFormulaException e) {
            e.printStackTrace();
        }
    }
}