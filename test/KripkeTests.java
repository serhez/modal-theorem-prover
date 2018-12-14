import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

public class KripkeTests {

    @Test
    public void acceptsValidFormulas() throws IncompatibleFrameConditionsException {

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
    public void rejectsInvalidFormulas() throws IncompatibleFrameConditionsException {

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
}
