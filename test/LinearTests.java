import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class LinearTests {
    @Test
    public void correctness() throws IncompatibleFrameConditionsException {
        Prover prover = new Prover();
        ModalLogic logicL = new ModalLogic("L");
        ModalLogic logicK = new ModalLogic("K");
        String validFormula = "~(<>T & <>[]F)";

        try {
            Assertions.assertTrue(prover.proveFormula(validFormula, logicL));   // Should be valid on logic L
            Assertions.assertFalse(prover.proveFormula(validFormula, logicK));  // ... but invalid on logic K
        } catch (UnrecognizableFormulaException e) {
            e.printStackTrace();
        }
    }
}
