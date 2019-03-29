import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReflexiveTests {

    @Test
    public void correctness() throws IncompatibleFrameConditionsException {
        Prover prover = new Prover();
        ModalLogic logicT = new ModalLogic("T");
        ModalLogic logicK = new ModalLogic("K");
        String validFormula = "~(~p & []p)";

        try {
            Assertions.assertTrue(prover.proveFormula(validFormula, logicT));   // Should be valid on logic T
            Assertions.assertFalse(prover.proveFormula(validFormula, logicK));  // ... but invalid on logic K
        } catch (UnrecognizableFormulaException e) {
            e.printStackTrace();
        }
    }
}
