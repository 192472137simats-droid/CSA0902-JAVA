import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

/**
 * Task B1: Buffering and Growth Benchmarking
 * - Measures unbuffered byte-by-byte read vs buffered block read on files of 1 MB and 10 MB.
 * - Measures linear scan search time across 1,000, 10,000, and 50,000 records.
 */
public class BufferingBenchmark {

    private static void benchmarkBuffering(int sizeMb) throws IOException {
        Path tempFile = Files.createTempFile("bench_" + sizeMb + "mb", ".txt");
        int targetBytes = sizeMb * 1024 * 1024;
        byte[] lineChunk = "LOG_ENTRY: 2026-11-10 14:00:00 USER_ATTENDANCE_RECORD VALID\n".getBytes();

        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(tempFile))) {
            int written = 0;
            while (written < targetBytes) {
                out.write(lineChunk);
                written += lineChunk.length;
            }
        }

        // 1. Unbuffered byte-by-byte read (measured for first 256 KB to project accurately without 10-minute freezes)
        int sampleSize = Math.min(targetBytes, 256 * 1024);
        long start = System.nanoTime();
        int unbufLines = 0;
        try (InputStream in = Files.newInputStream(tempFile)) {
            for (int i = 0; i < sampleSize; i++) {
                int b = in.read();
                if (b == '\n') unbufLines++;
            }
        }
        double unbufSampleMs = (System.nanoTime() - start) / 1_000_000.0;
        double projectedUnbufMs = unbufSampleMs * ((double) targetBytes / sampleSize);

        // 2. Buffered Stream (8 KB buffer)
        start = System.nanoTime();
        int bufLines = 0;
        try (InputStream in = new BufferedInputStream(Files.newInputStream(tempFile), 8192)) {
            int b;
            while ((b = in.read()) != -1) {
                if (b == '\n') bufLines++;
            }
        }
        double bufMs = (System.nanoTime() - start) / 1_000_000.0;

        System.out.printf("File Size: %2d MB | Unbuffered (Projected): %9.2f ms | Buffered (Actual): %6.2f ms | Speedup: %5.1fx%n",
                sizeMb, projectedUnbufMs, bufMs, projectedUnbufMs / Math.max(bufMs, 0.1));

        Files.delete(tempFile);
    }

    private static void benchmarkRecordGrowth(int recordCount) throws IOException {
        Path tempFile = Files.createTempFile("passes_" + recordCount, ".csv");
        PassStore store = new PassStore(tempFile);

        try (BufferedWriter w = Files.newBufferedWriter(tempFile)) {
            for (int i = 1; i <= recordCount; i++) {
                w.write(String.format("P%06d,19231%04d,Student_%d,%d,%.1f%n",
                        i, i % 10000, i, (i % 20) + 1, 3500.0));
            }
        }

        // Search for record at the end of the file (Worst-case linear scan O(n))
        String targetId = String.format("P%06d", recordCount);
        long start = System.nanoTime();
        String[] result = store.find(targetId);
        double scanTimeMs = (System.nanoTime() - start) / 1_000_000.0;

        System.out.printf("Records: %6d | Worst-Case Scan Time: %7.3f ms | Found: %s%n",
                recordCount, scanTimeMs, (result != null ? result[0] : "null"));

        Files.delete(tempFile);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("==========================================================================");
        System.out.println("Task B1.3: Measuring Effect of Buffering (Operating System Call Reduction)");
        System.out.println("==========================================================================");
        benchmarkBuffering(1);
        benchmarkBuffering(10);

        System.out.println("\n==========================================================================");
        System.out.println("Task B1.4: Measuring Search Growth Trend (Linear File Scan O(n))");
        System.out.println("==========================================================================");
        benchmarkRecordGrowth(1000);
        benchmarkRecordGrowth(10000);
        benchmarkRecordGrowth(50000);
    }
}
