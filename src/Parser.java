import java.util.ArrayList;
import java.util.Arrays;

public class Parser {

    private ArrayList<Theorem> theorems;      // TODO: THESE TWO VARIABLES ARE BAD, RETURN EVERYTHING IN parseInput() CALL
    private ArrayList<Integer> unrecognisedTheorems;
    private ModalSystem system;

    public Parser() {
        theorems = new ArrayList<>();
        unrecognisedTheorems = new ArrayList<>();
    }

    // Takes the input string extracted from the input file and populates the variable theorems
    public void parseInput(String input) throws IncompatibleFrameConditionsException {

        input = preprocess(input);

        ArrayList<String> theoremStrings = new ArrayList(Arrays.asList(input.split(";", 0)));
        for (int i = 0; i < theoremStrings.size(); i++) {
            Theorem currentTheorem = new Theorem(theoremStrings.get(i));
            theorems.add(currentTheorem);
            if (!currentTheorem.parse()) {
                unrecognisedTheorems.add(i);
            }
        }
    }

    public boolean parseFormula(String formulaString) throws IncompatibleFrameConditionsException {
        formulaString = preprocess(formulaString);
        Theorem theorem = new Theorem(formulaString);
        theorems.add(theorem);
        if (!theorem.parse()) {
            unrecognisedTheorems.add(0);
            return false;
        }

        return true;
    }

    private String preprocess(String inputString) throws IncompatibleFrameConditionsException {
        // Eliminate all spaces, tabs and new lines
        inputString = inputString.replaceAll(" ","");
        inputString = inputString.replaceAll("\t","");
        inputString = inputString.replaceAll("\n","");

        // Find a modal system with frame conditions, if any; also, delete the modal system specification from the input string
        String frameConditions = "";
        if (inputString.length() > 0) {
            if(inputString.charAt(0) == ':') {
                inputString = inputString.substring(1, inputString.length());
                int i = 0;
                while (i<inputString.length() && inputString.charAt(0) != ':') {
                    frameConditions += inputString.charAt(0);
                    inputString = inputString.substring(1, inputString.length());
                    i++;
                }
                inputString = inputString.substring(1, inputString.length());  // Get rid of the last ':'
            }
        }
        system = new ModalSystem(frameConditions);

        return inputString;
    }

    public ModalSystem getSystem() {
        return system;
    }

    public ArrayList<Theorem> getTheorems() {
        return theorems;
    }

    public ArrayList<Integer> getUnrecognisedTheorems() {
        return unrecognisedTheorems;
    }
}
