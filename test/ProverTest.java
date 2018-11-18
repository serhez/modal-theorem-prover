import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class ProverTest {

    // Current Input File

    @Test
    public void translateCurrentInputFileToMolle() {
        InputGenerator inputGenerator = new InputGenerator();
        inputGenerator.translateCurrentInputFileToMolle();
    }

    @Test
    public void runCurrentInputFile() {
        Prover prover = new Prover(false);
        prover.proveInputFile();
    }

    @Test
    public void debugCurrentInputFile() {
        Prover prover = new Prover(true);
        prover.proveInputFile();
    }

    // System K

    @Test
    public void runRandomInputFile() {
        InputGenerator inputGenerator = new InputGenerator();
        inputGenerator.generateInputFile(10000, 50, "K");

        Prover prover = new Prover(false);
        prover.proveInputFile();
    }

    @Test
    public void runRandomInputFileAndTranslateToMolle() {
        InputGenerator inputGenerator = new InputGenerator();
        inputGenerator.generateInputFileAndMolleFile(10000, 50, "K");

        Prover prover = new Prover(false);
        prover.proveInputFile();
    }

    @Test
    public void acceptsValidFormulas() {

        Prover prover = new Prover(false);
        ModalSystem system = new ModalSystem("K");
        ArrayList<String> formulas = new ArrayList<>();

        // Examples of valid formulas
        formulas.add("((((q<->p)->p)|q)|(p<->q))"); formulas.add("(q<->q)");
        formulas.add("(((q<->p)<->p)|((q|q)<->q))"); formulas.add("((p&q)->([]<>p|p))");
        formulas.add("(~p|(p|(p&q)))"); formulas.add("[]((((p|~(q<->q))->p)|(q<->p))|q)");
        formulas.add("([](q<->~~(q|(q&q)))|([]q&q))"); formulas.add("((q->q)->(q<->(q|q)))");
        formulas.add("[][](q<->((q->q)->q))"); formulas.add("(((p&(p<->p))|(q->q))|((p|p)|q))");
        formulas.add("((<>(p|p)&q)->(p->(q->q)))"); formulas.add("((p<->p)->(q|(p|(p->q))))");
        formulas.add("(<>p->(p<->p))"); formulas.add("(~((q|q)->p)|(~~(q|[]<>~~p)|(q->q)))");
        formulas.add("(~(([]q<->[](p->q))<->q)->(q<->q))"); formulas.add("[]((q->p)|q)");

        for (String formula : formulas) {
            try {
                Assertions.assertTrue(prover.proveFormula(formula, system), ("The prover cannot validate " + formula));
            } catch (UnrecognizableFormulaException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void rejectsInvalidFormulas() {

        Prover prover = new Prover(false);
        ModalSystem system = new ModalSystem("K");
        ArrayList<String> formulas = new ArrayList<>();

        // Examples of invalid formulas
        formulas.add("((p<->p)&p)"); formulas.add("<>(<>p&(q->q))");
        formulas.add("([](q<->(p<->q))->((p&<>(p|q))<->q))"); formulas.add("<>[]p");
        formulas.add("~((<>[]~q|(p|(q&(q->q))))<->q)"); formulas.add("([]~[][](q&p)->p)");
        formulas.add("(p&[](<>(~(p&q)->q)<->(p&q)))"); formulas.add("((<>q<->q)|[](q|([]<>p&q)))");
        formulas.add("<>((p|q)<->((<>[](p<->(p|q))|q)->p))"); formulas.add("[]q");
        formulas.add("<>q"); formulas.add("((~(<>(q<->q)<->q)|p)<->q)");
        formulas.add("~~~~<>q"); formulas.add("(<>(<>q&q)&(q|~<>(p<->~[]q)))");
        formulas.add("([]q&((((q->q)&p)<-><>(q|q))|q))"); formulas.add("<>((q<->((p->q)&q))<->p)");

        for (String formula : formulas) {
            try {
                Assertions.assertFalse(prover.proveFormula(formula, system), ("The prover cannot invalidate " + formula));
            } catch (UnrecognizableFormulaException e) {
                e.printStackTrace();
            }
        }
    }

    // System T

    @Test
    public void testReflexiveFormulas() {
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

    // System B

    @Test
    public void testSymmetricFormulas() {
        Prover prover = new Prover(false);
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

    // System 4

    @Test
    public void testTerminationOfTransitiveFormulas() {
        Prover prover = new Prover(false);
        ModalSystem system4 = new ModalSystem("4");
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
                Assertions.assertTrue(prover.proveFormula(validFormula, system4));
            }
            for (String invalidFormula : invalidFormulas) {
                Assertions.assertFalse(prover.proveFormula(invalidFormula, system4));
            }
        } catch (UnrecognizableFormulaException e) {
            e.printStackTrace();
        }
    }
}
