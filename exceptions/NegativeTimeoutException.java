public class NegativeTimeoutException extends Exception {
    public NegativeTimeoutException() {
        super("The timeout in protected mode cannot be negative. Also note it must be given in milliseconds.");
    }
}
