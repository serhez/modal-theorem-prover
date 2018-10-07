import java.util.ArrayList;
import java.util.Arrays;

public class Parser {

    private final String input;
    private ArrayList<Theorem> theorems;
    private ArrayList<Integer> invalidTheorems;


    public Parser(String input) {
        this.input = input;
        theorems = new ArrayList<>();
        invalidTheorems = new ArrayList<>();
    }

    // Takes the input string extracted from the input file and populates the variable theorems
    public void parse() {

        ArrayList<String> theoremStrings = new ArrayList(Arrays.asList(input.split(";", 0)));
        for (int i = 0; i < theoremStrings.size(); i++) {
            Theorem currentTheorem = new Theorem(theoremStrings.get(i));
            theorems.add(currentTheorem);
            if (!currentTheorem.parse()) {
                invalidTheorems.add(i);
            }
        }
    }

    public ArrayList<Theorem> getTheorems() {
        return theorems;
    }

    public ArrayList<Integer> getInvalidTheorems() {
        return invalidTheorems;
    }
}
