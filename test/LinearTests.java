import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class LinearTests {

    // Same as with transitive frames
    @Test
    public void termination() throws IncompatibleFrameConditionsException {
        Prover prover = new Prover(false);
        ModalSystem systemL = new ModalSystem("L");
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
                prover.proveFormula(validFormula, systemL);
            }
            for (String invalidFormula : invalidFormulas) {
                prover.proveFormula(invalidFormula, systemL);
            }
        } catch (UnrecognizableFormulaException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void correctness() throws IncompatibleFrameConditionsException {

    }
}
