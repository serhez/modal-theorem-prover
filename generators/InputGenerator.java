import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class InputGenerator {

    public String generateInputFile(int n, int size, int maxPropositions, String systemString) throws InvalidNumberOfPropositionsException {

        String inputString = generateInput(n, size, maxPropositions, systemString);

        try {
            write(inputString);
        } catch (IOException e) {
            System.out.println("The input formulas could not be written.");
            return null;
        }

        return inputString;
    }

    public void generateInputFileAndMolleFile(int n, int size, int maxPropositions, String systemString) throws InvalidNumberOfPropositionsException {

        String inputString = generateInputFile(n, size, maxPropositions, systemString);
        String molleInputString = translateInputToMolle(inputString);

        try {
            writeToMolle(molleInputString);
        } catch (IOException e) {
            System.out.println("The input formulas could not be written.");
            return;
        }
    }

    // Generates an input for the Theorem Prover
    private String generateInput(int n, int size, int maxPropositions, String systemString) throws InvalidNumberOfPropositionsException {

        String inputString = ":" + systemString + ":\n";
        FormulaGenerator formulaGenerator = new FormulaGenerator();

        for (int i=0; i < n; i++) {
            String formula = formulaGenerator.generate(size, maxPropositions);
            inputString += (formula + ";\n");
        }

        return inputString;
    }

    public ArrayList<String> generateFormulas(int n, int size, int maxPropositions) throws InvalidNumberOfPropositionsException {

        String inputString = generateInput(n, size, maxPropositions, "");

        // Delete the modal system specification from the input string
        if(inputString.charAt(0) == ':') {
            inputString = inputString.substring(1, inputString.length());
            int i = 0;
            while (i<inputString.length() && inputString.charAt(0) != ':') {
                inputString = inputString.substring(1, inputString.length());
                i++;
            }
            inputString = inputString.substring(2, inputString.length());  // Get rid of the last ':' and the '\n'
        }

        ArrayList<String> formulas = new ArrayList(Arrays.asList(inputString.split(";\n", 0)));
        return formulas;
    }

    private void write(String string) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("input/input.txt"));
        writer.write(string);
        writer.close();
    }

    private void writeToMolle(String string) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("input/inputMolle.txt"));
        writer.write(string);
        writer.close();
    }

    public void translateCurrentInputFileToMolle() {
        String molleInputString = "";
        try {
            molleInputString = translateInputToMolle(readInputFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            writeToMolle(molleInputString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static private String readInputFile() throws IOException {
        String inputPath = "input/input.txt";
        return (Files.lines(Paths.get(inputPath), StandardCharsets.UTF_8)).collect(Collectors.joining());
    }

    private String translateInputToMolle(String inputString) {

        ArrayList<String> formulas = new ArrayList(Arrays.asList(inputString.split(";\n", 0)));
        String molleInputString = "";
        for (String formula : formulas) {
            molleInputString += (translateFormulaToMolle(formula) + "\n");
        }

        return molleInputString;
    }

    // Translates the input string to Molle syntax and writes it to a file called "inputMolle.txt"
    public String translateFormulaToMolle(String string) {

        string = string.replaceAll("a", "A");
        string = string.replaceAll("b", "B");
        string = string.replaceAll("c", "C");
        string = string.replaceAll("d", "D");
        string = string.replaceAll("e", "E");
        string = string.replaceAll("f", "F");
        string = string.replaceAll("g", "G");
        string = string.replaceAll("h", "H");
        string = string.replaceAll("i", "I");
        string = string.replaceAll("j", "J");
        string = string.replaceAll("k", "K");
        string = string.replaceAll("l", "L");
        string = string.replaceAll("m", "M");
        string = string.replaceAll("n", "N");
        string = string.replaceAll("o", "O");
        string = string.replaceAll("p", "P");
        string = string.replaceAll("q", "Q");
        string = string.replaceAll("r", "R");
        string = string.replaceAll("s", "S");
        string = string.replaceAll("t", "T");
        string = string.replaceAll("u", "U");
        string = string.replaceAll("v", "V");
        string = string.replaceAll("w", "W");
        string = string.replaceAll("x", "X");
        string = string.replaceAll("y", "Y");
        string = string.replaceAll("z", "Z");
        string = string.replaceAll("-", "=");

        return string;
    }
}
