import java.util.ArrayList;
import java.util.Arrays;

public class Parser {

    private final String input;

    public Parser(String input) {
        this.input = input;
    }

    // Takes the input string extracted from the input file and returns a list of theorems
    public ArrayList<Theorem> parse() {

        ArrayList<Theorem> theorems = new ArrayList<Theorem>();

        ArrayList<String> theoremStrings = new ArrayList(Arrays.asList(input.split(";", 0)));
        for (String theoremString : theoremStrings) {
            theorems.add(new Theorem(theoremString));
        }

        return theorems;
    }
}
