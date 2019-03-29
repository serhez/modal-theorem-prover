import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class TransitiveTests {

    @Test
    public void termination() throws IncompatibleFrameConditionsException {
        Prover prover = new Prover();
        ModalLogic logic4 = new ModalLogic("4");
        ArrayList<String> validFormulas = new ArrayList<>();
        ArrayList<String> invalidFormulas = new ArrayList<>();

        // Valid formulas
        validFormulas.add("~((<>p & []<>p) & [][]~p)");
        validFormulas.add("~((<>p & []<>p) & [][][]~p)");

        // Invalid formulas
        invalidFormulas.add("~(<>p & []<>p)");
        invalidFormulas.add("~((<>p & []<>p) & [][]p)");
        invalidFormulas.add("~((<>p & []<>p) & []<>~p)");
        invalidFormulas.add("~((<>p & []<>p) & []<>q)");
        invalidFormulas.add("~(((<>p & []<>p) & []<>q) & []<>~p)");
        invalidFormulas.add("~((((<>p & []<>p) & []<>q) & []<>~p) & []<>~q)");

        try {
            for (String validFormula : validFormulas) {
                Assertions.assertTrue(prover.proveFormula(validFormula, logic4));
            }
            for (String invalidFormula : invalidFormulas) {
                Assertions.assertFalse(prover.proveFormula(invalidFormula, logic4));
            }
        } catch (UnrecognizableFormulaException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void correctness() throws IncompatibleFrameConditionsException {
        Prover prover = new Prover();
        ModalLogic logic4 = new ModalLogic("4");
        ModalLogic logicK = new ModalLogic("K");
        String validFormula = "~(<>p & ([]<>p & ([]q & [][]~q)))";

        try {
            Assertions.assertTrue(prover.proveFormula(validFormula, logic4));   // Should be valid on logic 4
            Assertions.assertFalse(prover.proveFormula(validFormula, logicK));  // ... but invalid on logic K
        } catch (UnrecognizableFormulaException e) {
            e.printStackTrace();
        }
    }
}
