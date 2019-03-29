import java.util.ArrayList;
import java.util.Arrays;

public class Parser {

    private ArrayList<FormulaArray> formulaArrays;      // TODO: THESE TWO VARIABLES ARE BAD, RETURN EVERYTHING IN parseInput() CALL
    private ArrayList<Integer> unrecognisedTheorems;
    private ModalLogic logic;

    public Parser() {
        formulaArrays = new ArrayList<>();
        unrecognisedTheorems = new ArrayList<>();
    }

    // Takes the input string extracted from the input file and populates the variable formulaArrays
    public void parseInput(String input) throws IncompatibleFrameConditionsException {

        input = preprocess(input);

        ArrayList<String> theoremStrings = new ArrayList(Arrays.asList(input.split(";", 0)));
        for (int i = 0; i < theoremStrings.size(); i++) {
            FormulaArray currentFormulaArray = new FormulaArray(theoremStrings.get(i));
            formulaArrays.add(currentFormulaArray);
            if (!currentFormulaArray.parse()) {
                unrecognisedTheorems.add(i);
            }
        }
    }

    public boolean parseFormula(String formulaString) throws IncompatibleFrameConditionsException {
        formulaString = preprocess(formulaString);
        FormulaArray formulaArray = new FormulaArray(formulaString);
        formulaArrays.add(formulaArray);
        if (!formulaArray.parse()) {
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

        // Find a modal logic with frame conditions, if any; also, delete the modal logic specification from the input string
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
        logic = new ModalLogic(frameConditions);

        return inputString;
    }

    public ModalLogic getLogic() {
        return logic;
    }

    public ArrayList<FormulaArray> getFormulaArrays() {
        return formulaArrays;
    }

    public ArrayList<Integer> getUnrecognisedTheorems() {
        return unrecognisedTheorems;
    }
}
