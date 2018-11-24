import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReflexiveTests {

    @Test
    public void correctnessOfReflexiveFormulas() {
        Prover prover = new Prover(false);
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
