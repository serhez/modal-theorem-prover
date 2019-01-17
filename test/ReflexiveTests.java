import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// TODO: create tests to make sure that the resulting frames in the tableau are enforcing reflexivity (will need a way to inspect the resulting tableau)
// TODO: do this as well for all test sets of the other frame conditions

public class ReflexiveTests {

    @Test
    public void correctness() throws IncompatibleFrameConditionsException {
        Prover prover = new Prover(false, false);
        ModalSystem systemT = new ModalSystem("T");
        ModalSystem systemK = new ModalSystem("K");
        String validFormula = "~(~p & []p)";

        try {
            Assertions.assertTrue(prover.proveFormula(validFormula, systemT));   // Should be valid on System T
            Assertions.assertFalse(prover.proveFormula(validFormula, systemK));  // ... but invalid on System K
        } catch (UnrecognizableFormulaException e) {
            e.printStackTrace();
        }
    }
}
