import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MolleTest {

    @Test
    public void outputMatchesMolle() {

        InputGenerator inputGenerator = new InputGenerator(100, 50);
        Prover prover = new Prover();
        MolleAdapter molleAdapter = new MolleAdapter();

        ArrayList<String> formulas = inputGenerator.generateInputFormulas();

        for (String formula : formulas) {
            try {
                assertTrue(prover.proveFormula(formula) == molleAdapter.proveFormula(formula), ("The validity of the formula " + formula + " is not consistent."));
            } catch (UnrecognizableFormulaException e) {
                e.printStackTrace();
            }
        }


    }
}
