import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashSet;

public class GeneratorTests {

    @Test
    public void correctSyntax() throws InvalidNumberOfPropositionsException, IncompatibleFrameConditionsException {
        InputGenerator inputGenerator = new InputGenerator();
        Parser parser = new Parser();
        ArrayList<String> formulas = inputGenerator.generateFormulas(1000, 100, 5);
        for (String formula : formulas) {
            Assertions.assertTrue(parser.parseFormula(formula), ("The following formula has not being recognised:\n" + formula + "\n"));
        }
    }

    @Test
    public void correctN() throws InvalidNumberOfPropositionsException {
        InputGenerator inputGenerator = new InputGenerator();
        int n = 1000;
        ArrayList<String> formulas = inputGenerator.generateFormulas(n, 10, 2);
        Assertions.assertTrue(formulas.size() == n, ("The input generator did not produce the correct number of formulas."));
    }

    @Test
    public void correctSize() throws InvalidNumberOfPropositionsException {
        InputGenerator inputGenerator = new InputGenerator();
        int size = 15;
        ArrayList<String> formulas = inputGenerator.generateFormulas(1000, size, 2);
        for (String formula : formulas) {
            Assertions.assertTrue(getSymbolSize(formula) == size, ("The following formula is not of the correct size:\n" + formula + "\n"));
        }
    }

    @Test
    public void correctNoProps() throws InvalidNumberOfPropositionsException {
        InputGenerator inputGenerator = new InputGenerator();
        int noProps = 5;
        ArrayList<String> formulas = inputGenerator.generateFormulas(1000, 500, noProps);
        for (String formula : formulas) {
            Assertions.assertTrue(getNoProps(formula) <= noProps, ("The following formula does not have the correct number of propositions:\n" + formula + "\n"));
        }
    }

    // Assumes single character propositions
    private int getNoProps(String formula) {
        HashSet<Character> props = new HashSet<>();
        HashSet<Character> other = new HashSet<>();
        other.add('<'); other.add('>'); other.add('['); other.add(']'); other.add('~');
        other.add('&'); other.add('|'); other.add('-'); other.add('('); other.add(')');
        for (int i=0; i<formula.length(); i++) {
            if (!other.contains(formula.charAt(i))) {
                if (!props.contains(formula.charAt(i))) {
                    props.add(formula.charAt(i));
                }
            }
        }
        return props.size();
    }

    private int getSymbolSize(String formula) {
        int size = 0;
        for (int i=0; i<formula.length(); i++) {
            if (formula.charAt(i) == '[' || formula.charAt(i) == '<' || formula.charAt(i) == '-') {
                continue;
            }
            size++;
        }
        return size;
    }
}
