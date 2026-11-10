import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Q3: Resource Safety, Debugging and Robust Code
 * Problem Context: Daily Sales Report Generator
 * 
 * Compares:
 * 1. Manual close() in finally block (vulnerable to resource leakage and lost exceptions)
 * 2. try-with-resources (guaranteed suppression-aware closing)
 */
public class SalesReportGenerator {

    // Simulates processing a batch of store sales files
    public static void processReportManual(List<File> storeFiles, File outputFile) {
        System.out.println("\n--- Running Manual try-finally Processing ---");
        BufferedReader reader = null;
        BufferedWriter writer = null;
        int filesOpenCount = 0;

        try {
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write("=== Daily Sales Report ===\n");

            for (File file : storeFiles) {
                // Simulating opening files in sequence
                reader = new BufferedReader(new FileReader(file));
                filesOpenCount++;
                String line = reader.readLine();

                if (line != null && line.contains("CORRUPT")) {
                    throw new IOException("Data corruption error encountered in " + file.getName());
                }

                writer.write("Store: " + file.getName() + " -> Total: " + (line != null ? line : "0") + "\n");
                reader.close(); // Manual close here
                filesOpenCount--;
            }
        } catch (IOException e) {
            System.err.println("Manual Pipeline Caught Failure: " + e.getMessage());
        } finally {
            // Note: If reader failed, writer might close, but if writer throws in close(), cleanup fails
            try {
                if (writer != null) writer.close();
            } catch (IOException ignored) {}
            // Reader from current iteration may remain unclosed if exception triggered before reader.close()!
            System.out.println("Manual cleanup complete. Estimated dangling file handles: " + filesOpenCount);
        }
    }

    public static void processReportTryWithResources(List<File> storeFiles, File outputFile) {
        System.out.println("\n--- Running try-with-resources Processing ---");
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile.toPath())) {
            writer.write("=== Daily Sales Report ===\n");

            for (File file : storeFiles) {
                try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
                    String line = reader.readLine();
                    if (line != null && line.contains("CORRUPT")) {
                        throw new IOException("Corrupted payload in " + file.getName());
                    }
                    writer.write("Store: " + file.getName() + " -> " + line + "\n");
                } catch (IOException innerEx) {
                    System.err.println("Safely logged single store error (" + file.getName() + "): " 
                                       + innerEx.getMessage() + ". Resource auto-closed.");
                }
            }
            System.out.println("Report completed. All file handles strictly auto-closed in reverse acquisition order.");
        } catch (IOException outerEx) {
            System.err.println("Top-level output error: " + outerEx.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("==========================================================");
        System.out.println("Q3: Resource Safety & Daily Sales Report Generator");
        System.out.println("==========================================================");

        // Create sample test files
        File tempDir = Files.createTempDirectory("sales_data").toFile();
        tempDir.deleteOnExit();

        List<File> storeFiles = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            File f = new File(tempDir, "store_" + i + ".txt");
            f.deleteOnExit();
            FileWriter fw = new FileWriter(f);
            if (i == 3) {
                fw.write("CORRUPT_PAYLOAD_DATA");
            } else {
                fw.write("Sales: 54000 INR");
            }
            fw.close();
            storeFiles.add(f);
        }

        File outManual = new File(tempDir, "summary_manual.txt");
        File outAuto = new File(tempDir, "summary_auto.txt");

        // Run both implementations with failing input
        processReportManual(storeFiles, outManual);
        processReportTryWithResources(storeFiles, outAuto);
    }
}
