import java.util.ArrayList;
import java.util.Random;

public class FormulaGenerator {

    public String generate(int maxLength) {

        String formula = "";

        Random operator = new Random();

        if (maxLength > 7) {   // 7 because the maximum length of a trivial non-propositional formula is 7: (p<->q)
            switch (operator.nextInt(8)) {
                case 0: formula = appendProposition(); break;
                case 1: formula = appendNegation(maxLength); break;
                case 2: formula = appendAnd(maxLength); break;
                case 3: formula = appendOr(maxLength); break;
                case 4: formula = appendCondition(maxLength); break;
                case 5: formula = appendBicondition(maxLength); break;
                case 6: formula = appendNecessarily(maxLength); break;
                case 7: formula = appendPossibly(maxLength); break;
            }
        } else {
            formula = appendProposition();
        }

        return formula;
    }

    // Only uses two propositions to increase the chances of validity of the formula
    private String appendProposition() {

        String formula = "";
        Random proposition = new Random();

        switch (proposition.nextInt(2)) {
            case 0: formula = "p"; break;
            case 1: formula = "q"; break;
        }

        return formula;
    }

    private String appendNegation(int maxLength) {
        String subformula = generate(maxLength-1);
        String formula = ("~" + subformula);
        return formula;
    }

    private String appendAnd(int maxLength) {
        ArrayList<Integer> randomPair = generateRandomPair(maxLength-3);
        String subformula1 = generate(randomPair.get(0));
        String subformula2 = generate(randomPair.get(1));
        String formula = "(" + subformula1 + "&" + subformula2 + ")";
        return formula;
    }

    private String appendOr(int maxLength) {
        ArrayList<Integer> randomPair = generateRandomPair(maxLength-3);
        String subformula1 = generate(randomPair.get(0));
        String subformula2 = generate(randomPair.get(1));
        String formula = "(" + subformula1 + "|" + subformula2 + ")";
        return formula;
    }

    private String appendCondition(int maxLength) {
        ArrayList<Integer> randomPair = generateRandomPair(maxLength-4);
        String subformula1 = generate(randomPair.get(0));
        String subformula2 = generate(randomPair.get(1));
        String formula = "(" + subformula1 + "->" + subformula2 + ")";
        return formula;
    }

    private String appendBicondition(int maxLength) {
        ArrayList<Integer> randomPair = generateRandomPair(maxLength-5);
        String subformula1 = generate(randomPair.get(0));
        String subformula2 = generate(randomPair.get(1));
        String formula = "(" + subformula1 + "<->" + subformula2 + ")";
        return formula;
    }

    private String appendNecessarily(int maxLength) {
        String subformula = generate(maxLength-1);
        String formula = ("[]" + subformula);
        return formula;
    }

    private String appendPossibly(int maxLength) {
        String subformula = generate(maxLength-1);
        String formula = ("<>" + subformula);
        return formula;
    }

    private ArrayList<Integer> generateRandomPair(int totalLength) {

        Random number = new Random();
        int n1 = 0;
        int n2 = 0;

        while ((n1 + n2) != totalLength) {
            n1 = number.nextInt(totalLength) + 1;
            n2 = number.nextInt(totalLength) + 1;
        }

        ArrayList<Integer> randomPair = new ArrayList<>();
        randomPair.add(n1);
        randomPair.add(n2);

        return randomPair;
    }

}
