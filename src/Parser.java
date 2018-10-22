import java.util.ArrayList;
import java.util.Arrays;

public class Parser {

    private ArrayList<Theorem> theorems;      // TODO: THESE TWO VARIABLES ARE BAD, RETURN EVERYTHING IN parseInput() CALL
    private ArrayList<Integer> invalidTheorems;

    public Parser() {
        theorems = new ArrayList<>();
        invalidTheorems = new ArrayList<>();
    }

    // Takes the input string extracted from the input file and populates the variable theorems
    public void parseInput(String input) {

        ArrayList<String> theoremStrings = new ArrayList(Arrays.asList(input.split(";", 0)));
        for (int i = 0; i < theoremStrings.size(); i++) {
            Theorem currentTheorem = new Theorem(theoremStrings.get(i));
            theorems.add(currentTheorem);
            if (!currentTheorem.parse()) {
                invalidTheorems.add(i);
            }
        }
    }

    public boolean parseFormula(String formulaString) {
        Theorem theorem = new Theorem(formulaString);
        theorems.add(theorem);
        if (!theorem.parse()) {
            invalidTheorems.add(0);
            return false;
        }

        return true;
    }

    public ArrayList<Theorem> getTheorems() {
        return theorems;
    }

    public ArrayList<Integer> getInvalidTheorems() {
        return invalidTheorems;
    }
}
