public class UnrecognizableFormulaException extends Exception {

    public UnrecognizableFormulaException(String formulaString) {
        super("The following formula has not being recognized by the parser: " + formulaString);
    }

    public void printMessage() {
        System.out.println(getMessage());
    }
}
