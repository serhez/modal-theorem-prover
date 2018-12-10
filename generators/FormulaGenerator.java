import java.util.Random;

public class FormulaGenerator {

    public String generate(int maxLength, int maxPropositions) throws InvalidNumberOfPropositionsException {

        if (maxPropositions < 1 || maxPropositions > 26) {
            throw new InvalidNumberOfPropositionsException();
        }

        String formula = "";

        Random operator = new Random();


        if (maxLength >= 5) {  // We count "->" and "<->" as 1 symbol each
            switch (operator.nextInt(3)) {
                case 0:
                    formula = appendNegation(maxLength, maxPropositions);
                    break;
                case 1:
                    formula = appendOr(maxLength, maxPropositions);
                    break;
                case 2:
                    formula = appendPossibly(maxLength, maxPropositions);
                    break;
//                case 3:
//                    formula = appendNecessarily(maxLength, maxPropositions);
//                    break;
//                case 4:
//                    formula = appendAnd(maxLength, maxPropositions);
//                    break;
//                case 5:
//                    formula = appendCondition(maxLength, maxPropositions);
//                    break;
//                case 6:
//                    formula = appendBicondition(maxLength, maxPropositions);
            }
        } else if (maxLength >= 2) {  // We count "[]" and "<>" as 1 symbol each
            switch (operator.nextInt(2)) {
                case 0:
                    formula = appendNegation(maxLength, maxPropositions);
                    break;
                case 1:
                    formula = appendPossibly(maxLength, maxPropositions);
                    break;
//                case 2:
//                    formula = appendNecessarily(maxLength, maxPropositions);
//                    break;
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

    private String appendNegation(int maxLength, int maxPropositions) throws InvalidNumberOfPropositionsException {
        String subformula = generate(maxLength-1, maxPropositions);
        String formula = ("~" + subformula);
        return formula;
    }

    private String appendOr(int maxLength, int maxPropositions) throws InvalidNumberOfPropositionsException {
        Random number = new Random();
        int subLength = maxLength-3;
        int length1 = number.nextInt(subLength-1) + 1; // At least length 1 and at most subLength-1
        int length2 = subLength - length1;
        String subformula1 = generate(length1, maxPropositions);
        String subformula2 = generate(length2, maxPropositions);
        String formula = "(" + subformula1 + "|" + subformula2 + ")";
        return formula;
    }

    private String appendAnd(int maxLength, int maxPropositions) throws InvalidNumberOfPropositionsException {
        Random number = new Random();
        int subLength = maxLength-3;
        int length1 = number.nextInt(subLength-1) + 1; // At least length 1 and at most subLength-1
        int length2 = subLength - length1;
        String subformula1 = generate(length1, maxPropositions);
        String subformula2 = generate(length2, maxPropositions);
        String formula = "(" + subformula1 + "&" + subformula2 + ")";
        return formula;
    }

    private String appendCondition(int maxLength, int maxPropositions) throws InvalidNumberOfPropositionsException {
        Random number = new Random();
        int subLength = maxLength-4;
        int length1 = number.nextInt(subLength-1) + 1; // At least length 1 and at most subLength-1
        int length2 = subLength - length1;
        String subformula1 = generate(length1, maxPropositions);
        String subformula2 = generate(length2, maxPropositions);
        String formula = "(" + subformula1 + "->" + subformula2 + ")";
        return formula;
    }

    private String appendBicondition(int maxLength, int maxPropositions) throws InvalidNumberOfPropositionsException {
        Random number = new Random();
        int subLength = maxLength-5;
        int length1 = number.nextInt(subLength-1) + 1; // At least length 1 and at most subLength-1
        int length2 = subLength - length1;
        String subformula1 = generate(length1, maxPropositions);
        String subformula2 = generate(length2, maxPropositions);
        String formula = "(" + subformula1 + "<->" + subformula2 + ")";
        return formula;
    }

    private String appendNecessarily(int maxLength, int maxPropositions) throws InvalidNumberOfPropositionsException {
        String subformula = generate(maxLength-1, maxPropositions);
        String formula = ("[]" + subformula);
        return formula;
    }

    private String appendPossibly(int maxLength, int maxPropositions) throws InvalidNumberOfPropositionsException {
        String subformula = generate(maxLength-1, maxPropositions);
        String formula = ("<>" + subformula);
        return formula;
    }
}
