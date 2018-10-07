import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TheoremProver {

    public static void main(String[] args) throws IOException {

        // Important variables
        ArrayList<Theorem> theorems;
        ArrayList<Integer> invalidTheorems;
        String inputPath = "input/input.txt";
        String inputString = (Files.lines(Paths.get(inputPath), StandardCharsets.UTF_8)).collect(Collectors.joining());

        // Parse
        Parser parser = new Parser(inputString);
        parser.parse();
        theorems = parser.getTheorems();
        invalidTheorems = parser.getInvalidTheorems();
        printTheorems(theorems);

        // Prove

    }

    private static void printTheorems(ArrayList<Theorem> theorems) {
        for (int i = 0; i < theorems.size(); i++) {
            System.out.println();
            System.out.println("----------------------------------------------------------------------");
            System.out.println("----------------------------------------------------------------------");
            System.out.println("THEOREM " + i);
            System.out.println("----------------------------------------------------------------------");
            System.out.println("----------------------------------------------------------------------");
            System.out.println();
            for (Formula formula : theorems.get(i).getFormulas()) {
                printFormula(formula, 0);
            }
        }
    }

    private static void printFormula(Formula formula, int indentation) {
        System.out.println();
        for (int i = 0; i < indentation; i++) {
            System.out.print(" ");
        }
        System.out.println(formula.getFormulaString() + "  ;  " + formula.getOperator() + "  ;  " + formula.getOperatorIndex());
        for (Formula subformula : formula.getSubformulas()) {
            printFormula(subformula, indentation+2);
        }
    }

}
