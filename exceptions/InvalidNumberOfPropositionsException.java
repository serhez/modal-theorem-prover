public class InvalidNumberOfPropositionsException extends Exception {

    public InvalidNumberOfPropositionsException() {
        super("The max number of propositions used by the generator must be between 1 and 26, both included");
    }

    public void printMessage() {
            System.out.println(getMessage());
        }
}
