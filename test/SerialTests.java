import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SerialTests {
    @Test
    public void correctness() throws IncompatibleFrameConditionsException {
        Prover prover = new Prover();
        ModalLogic logicD = new ModalLogic("D");
        ModalLogic logicK = new ModalLogic("K");
        String validFormula = "~(T & []F)";

        try {
            Assertions.assertTrue(prover.proveFormula(validFormula, logicD));   // Should be valid on logic D
            Assertions.assertFalse(prover.proveFormula(validFormula, logicK));  // ... but invalid on logic K
        } catch (UnrecognizableFormulaException e) {
            e.printStackTrace();
        }
    }
}
