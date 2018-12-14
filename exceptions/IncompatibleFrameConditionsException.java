import java.util.ArrayList;

public class IncompatibleFrameConditionsException extends Exception {

    private String message;

    public IncompatibleFrameConditionsException(ArrayList<String> conditions) {
        message = "The following frame conditions are incompatible: ";
        int n = conditions.size();
        for (int i = 0; i < n; i++) {
            if (i != n-1) {
                message += conditions.get(i) + ", ";
            } else {
                message += conditions.get(i) + ".";
            }
        }
    }

    public void printMessage() {
        System.out.println(message);
    }
}