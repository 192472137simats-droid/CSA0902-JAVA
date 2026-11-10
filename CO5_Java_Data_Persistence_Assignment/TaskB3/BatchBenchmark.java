import java.util.*;

/**
 * Task B3: Statement vs PreparedStatement vs Batch & Transaction Rollback Simulation
 */
public class BatchBenchmark {

    static class MockConnection {
        private boolean autoCommit = true;
        private final List<String[]> committedRows = new ArrayList<>();
        private final List<String[]> uncommittedRows = new ArrayList<>();
        private int roundTrips = 0;

        public void setAutoCommit(boolean autoCommit) {
            this.autoCommit = autoCommit;
        }

        public boolean getAutoCommit() {
            return autoCommit;
        }

        public void addRow(String[] row) {
            roundTrips++;
            if (autoCommit) {
                committedRows.add(row);
            } else {
                uncommittedRows.add(row);
            }
        }

        public void addBatch(List<String[]> batch) {
            roundTrips++; // Only 1 network roundtrip per batch!
            if (autoCommit) {
                committedRows.addAll(batch);
            } else {
                uncommittedRows.addAll(batch);
            }
        }

        public void commit() {
            committedRows.addAll(uncommittedRows);
            uncommittedRows.clear();
        }

        public void rollback() {
            uncommittedRows.clear(); // Reverts uncommitted state!
        }

        public int getCommittedCount() {
            return committedRows.size();
        }

        public int getRoundTrips() {
            return roundTrips;
        }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("Task B3: Statement vs PreparedStatement vs Batch Ingestion Analysis");
        System.out.println("==========================================================================");

        int N = 10000;
        List<String[]> passes = new ArrayList<>(N);
        for (int i = 1; i <= N; i++) {
            passes.add(new String[]{"P" + i, "1923" + i, "Student_" + i, "5", "3500.0"});
        }

        // 1. Single Statement Insert (auto-commit ON)
        MockConnection con1 = new MockConnection();
        long start = System.nanoTime();
        for (String[] p : passes) {
            con1.addRow(p);
        }
        double singleTimeMs = (System.nanoTime() - start) / 1_000_000.0;

        // 2. Batch Insert (batches of 1,000, single transaction)
        MockConnection con2 = new MockConnection();
        con2.setAutoCommit(false);
        start = System.nanoTime();
        List<String[]> currentBatch = new ArrayList<>(1000);
        for (String[] p : passes) {
            currentBatch.add(p);
            if (currentBatch.size() == 1000) {
                con2.addBatch(currentBatch);
                currentBatch.clear();
            }
        }
        if (!currentBatch.isEmpty()) con2.addBatch(currentBatch);
        con2.commit();
        double batchTimeMs = (System.nanoTime() - start) / 1_000_000.0;

        System.out.printf("%-32s | %-16s | %-18s | %-16s%n", "Ingestion Strategy", "Records Inserted", "Network Roundtrips", "Time (ms)");
        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.printf("%-32s | %16d | %18d | %16.3f%n", "Statement / Indiv PreparedStatement", con1.getCommittedCount(), con1.getRoundTrips(), singleTimeMs);
        System.out.printf("%-32s | %16d | %18d | %16.3f%n", "Batch PreparedStatement (1000)", con2.getCommittedCount(), con2.getRoundTrips(), batchTimeMs);

        // 3. Auto-commit vs Transaction with Halfway Failure
        System.out.println("\n--- B3.3: Halfway Ingestion Failure Demonstration ---");

        // Failure under auto-commit
        MockConnection conFailAuto = new MockConnection();
        conFailAuto.setAutoCommit(true);
        try {
            for (int i = 0; i < 500; i++) conFailAuto.addRow(passes.get(i));
            throw new RuntimeException("Power failure / DB disconnect at row 500");
        } catch (Exception e) {
            System.out.println("Auto-commit ON: Failure occurred -> Committed rows in DB: " + conFailAuto.getCommittedCount() + " (Incomplete/Corrupted Data!)");
        }

        // Failure under Transaction with Rollback
        MockConnection conFailTx = new MockConnection();
        conFailTx.setAutoCommit(false);
        try {
            for (int i = 0; i < 500; i++) conFailTx.addRow(passes.get(i));
            throw new RuntimeException("Power failure / DB disconnect at row 500");
        } catch (Exception e) {
            conFailTx.rollback();
            System.out.println("Single Transaction with Rollback: Failure occurred -> Committed rows in DB: " + conFailTx.getCommittedCount() + " (Clean state: 0 rows leaked!)");
        }
    }
}
