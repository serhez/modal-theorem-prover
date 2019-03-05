import org.junit.jupiter.api.Test;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class PerformanceTests {

    @Test
    public void formulaSizeTest() throws NegativeTimeoutException, IncompatibleFrameConditionsException,
            UnrecognizableFormulaException, InvalidNumberOfPropositionsException, IOException {  // For system K and all connectives
        Prover prover = new Prover();
        prover.enableProtectedMode(10000);  // 10 seconds timeout
        String outputPath = "test/formula_size_test_all.csv";

        // Variables
        int repetitions = 1;   // Useful if minSize = maxSize, for large sizes (default is 1)
        int jumpSize = 10;
        int minSize = 10;
        int maxSize = 100;
        int samples = 1000;
        int maxPropositions = 2;
        String system = "K";

        StringBuilder sb = new StringBuilder();
//        sb.append("Size (symbols), Average time (ms), Max. time (ms), Min. time (ms), Abortion rate after " +
//                prover.getTimeout()/1000 + "s (%)\n");
//        write(sb.toString(), outputPath);
//        sb.setLength(0);
        Prover.Results results;
        ArrayList<Long> times;
        DecimalFormat formatter = new DecimalFormat("#0.000");
        for (int i = 0; i < repetitions; i++) {
            for (int size = minSize; size <= maxSize; size += jumpSize) {
                results = prover.proveRandomFormulas(samples, size, maxPropositions, new ModalSystem(system),
                        false);
                times = results.getTimes();
                sb.append(size + ", " + formatter.format(times.stream().mapToDouble(val->val).average().getAsDouble()) + ", " +
                        formatter.format(times.stream().mapToDouble(val->val).max().getAsDouble()) + ", " +
                        formatter.format(times.stream().mapToDouble(val->val).min().getAsDouble()) + ", " +
                        formatter.format(results.getAbortionRate()) + "\n");
                write(sb.toString(), outputPath);
                sb.setLength(0);
            }
        }
    }

    private static void write(String string, String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
        writer.write(string);
        writer.close();
    }
}
