import java.util.ArrayList;
import java.util.Arrays;

public class Parser {

    private final String input;
    ArrayList<Theorem> theorems;


    public Parser(String input) {
        this.input = input;
        theorems = new ArrayList<Theorem>();
    }

    // Takes the input string extracted from the input file and populates the variable theorems; it returns false if any formula is invalid
    public boolean parse() {

        boolean validInput = true; // This variable allows the program to keep looking for invalid formulas once one has been found, for ease of debugging

        ArrayList<String> theoremStrings = new ArrayList(Arrays.asList(input.split(";", 0)));
        for (String theoremString : theoremStrings) {
            Theorem currentTheorem = new Theorem(theoremString);
            if (!currentTheorem.parse()) {
                validInput = false;
            }
            theorems.add(currentTheorem);
    }

        return validInput;
    }

    public ArrayList<Theorem> getTheorems() {
        return theorems;
    }
}
