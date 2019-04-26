import java.util.Random;

public class FormulaGenerator {

    public String generate(int size, int maxPropositions) throws InvalidNumberOfPropositionsException {

        if (maxPropositions < 1 || maxPropositions > 26) {
            throw new InvalidNumberOfPropositionsException();
        }

        String formula = "";

        Random operator = new Random();


        if (size >= 5) {  // We count "->" and "<->" as 1 symbol each
            switch (operator.nextInt(7)) {  // REMEMBER TO CHANGE BOUND WHEN CHANGING NUMBER OF OPERATORS
                case 0:
                    formula = appendNegation(size, maxPropositions);
                    break;
                case 1:
                    formula = appendOr(size, maxPropositions);
                    break;
                case 2:
                    formula = appendPossibly(size, maxPropositions);
                    break;
                case 3:
                    formula = appendNecessarily(size, maxPropositions);
                    break;
                case 4:
                    formula = appendAnd(size, maxPropositions);
                    break;
                case 5:
                    formula = appendCondition(size, maxPropositions);
                    break;
                case 6:
                    formula = appendBicondition(size, maxPropositions);
            }
        } else if (size >= 2) {  // We count "[]" and "<>" as 1 symbol each
            switch (operator.nextInt(3)) {  // REMEMBER TO CHANGE BOUND WHEN CHANGING NUMBER OF OPERATORS
                case 0:
                    formula = appendNegation(size, maxPropositions);
                    break;
                case 1:
                    formula = appendPossibly(size, maxPropositions);
                    break;
                case 2:
                    formula = appendNecessarily(size, maxPropositions);
                    break;
            }
        } else {
            formula = appendProposition(maxPropositions);
        }

        return formula;
    }

    // 0 < n < 27
    private String appendProposition(int maxPropositions) {
        Random proposition = new Random();
        int propositionASCII = proposition.nextInt(maxPropositions) + 97;
        String formula = "" + (char)propositionASCII + "";
        return formula;
    }

    private String appendNegation(int size, int maxPropositions) throws InvalidNumberOfPropositionsException {
        String subformula = generate(size-1, maxPropositions);
        String formula = ("~" + subformula);
        return formula;
    }

    private String appendOr(int size, int maxPropositions) throws InvalidNumberOfPropositionsException {
        Random number = new Random();
        int subLength = size-3;
        int length1 = number.nextInt(subLength-1) + 1; // At least length 1 and at most subLength-1
        int length2 = subLength - length1;
        String subformula1 = generate(length1, maxPropositions);
        String subformula2 = generate(length2, maxPropositions);
        String formula = "(" + subformula1 + "|" + subformula2 + ")";
        return formula;
    }

    private String appendAnd(int size, int maxPropositions) throws InvalidNumberOfPropositionsException {
        Random number = new Random();
        int subLength = size-3;
        int length1 = number.nextInt(subLength-1) + 1; // At least length 1 and at most subLength-1
        int length2 = subLength - length1;
        String subformula1 = generate(length1, maxPropositions);
        String subformula2 = generate(length2, maxPropositions);
        String formula = "(" + subformula1 + "&" + subformula2 + ")";
        return formula;
    }

    private String appendCondition(int size, int maxPropositions) throws InvalidNumberOfPropositionsException {
        Random number = new Random();
        int subLength = size-3;
        int length1 = number.nextInt(subLength-1) + 1; // At least length 1 and at most subLength-1
        int length2 = subLength - length1;
        String subformula1 = generate(length1, maxPropositions);
        String subformula2 = generate(length2, maxPropositions);
        String formula = "(" + subformula1 + "->" + subformula2 + ")";
        return formula;
    }

    private String appendBicondition(int size, int maxPropositions) throws InvalidNumberOfPropositionsException {
        Random number = new Random();
        int subLength = size-3;
        int length1 = number.nextInt(subLength-1) + 1; // At least length 1 and at most subLength-1
        int length2 = subLength - length1;
        String subformula1 = generate(length1, maxPropositions);
        String subformula2 = generate(length2, maxPropositions);
        String formula = "(" + subformula1 + "<->" + subformula2 + ")";
        return formula;
    }

    private String appendNecessarily(int size, int maxPropositions) throws InvalidNumberOfPropositionsException {
        String subformula = generate(size-1, maxPropositions);
        String formula = ("[]" + subformula);
        return formula;
    }

    private String appendPossibly(int size, int maxPropositions) throws InvalidNumberOfPropositionsException {
        String subformula = generate(size-1, maxPropositions);
        String formula = ("<>" + subformula);
        return formula;
    }
}
