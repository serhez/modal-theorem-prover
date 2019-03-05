import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class ParserTests {

    @Test
    public void canParseGoodFormulas() throws IncompatibleFrameConditionsException {
        Parser parser = new Parser();
        ArrayList<String> formulas = new ArrayList<>();
        formulas.add("peach"); formulas.add("~~~peach");
        formulas.add("~(<>p & []<>p)"); formulas.add("<>[]<>~~~~~<><><>[][][]~(q | p)");
        formulas.add("(mouse->(cheese->(trap<->death)))"); formulas.add("(mouse    ->(cheese  ->   (   trap <->death )))");

        for (String formula : formulas) {
            Assertions.assertTrue(parser.parseFormula(formula), ("The following formula could not be parsed:\n"
                    + formula + "\n"));
        }
    }
    @Test
    public void cannotParseBadFormulas() throws IncompatibleFrameConditionsException {
        Parser parser = new Parser();
        ArrayList<String> formulas = new ArrayList<>();
        formulas.add("pea:ch"); formulas.add("(peach)");
        formulas.add("~(<>p && []<>p)"); formulas.add("<>[]<>~~~~~<><><>[p][][]p");
        formulas.add("(mouse->(cheese->(trap<->death))))"); formulas.add("(mouse    ->(cheese  ->   (   trap <~>death )))");

        for (String formula : formulas) {
            Assertions.assertFalse(parser.parseFormula(formula), ("The following formula should not be parsed, but it was:\n"
                    + formula + "\n"));
        }
    }
}
