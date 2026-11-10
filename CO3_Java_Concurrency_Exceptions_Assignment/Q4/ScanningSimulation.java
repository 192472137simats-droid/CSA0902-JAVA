import java.util.concurrent.atomic.AtomicInteger;

/**
 * Q4: Thread Creation and the Thread Life Cycle
 * Problem Context: Parallel Answer-Script Scanning (4 scanner stations)
 * 
 * Demonstrates:
 * - Thread vs Runnable implementation
 * - start(), sleep(), join(), isAlive()
 * - run() vs start() execution thread evidence
 * - Thread priorities and daemon thread termination
 */
public class ScanningSimulation {

    // Implementation 1: Implementing Runnable
    static class ScannerRunnable implements Runnable {
        private final int stationId;
        private final int batchSize;
        private final AtomicInteger totalScanned;

        ScannerRunnable(int stationId, int batchSize, AtomicInteger totalScanned) {
            this.stationId = stationId;
            this.batchSize = batchSize;
            this.totalScanned = totalScanned;
        }

        @Override
        public void run() {
            String currentThread = Thread.currentThread().getName();
            System.out.printf("[Station %d] Started on Thread: %s%n", stationId, currentThread);
            try {
                // Simulate variable scanning time
                Thread.sleep(50 + (long) (Math.random() * 50));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            totalScanned.addAndGet(batchSize);
            System.out.printf("[Station %d] Finished scanning %d scripts.%n", stationId, batchSize);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("==========================================================");
        System.out.println("Q4: Parallel Answer-Script Scanning Simulation");
        System.out.println("==========================================================");

        // 1. Evidence: run() vs start()
        System.out.println("\n--- Experiment 1: Calling run() vs start() Evidence ---");
        AtomicInteger testCounter = new AtomicInteger(0);
        ScannerRunnable syncTask = new ScannerRunnable(99, 100, testCounter);
        System.out.println("Invoking syncTask.run() directly:");
        syncTask.run(); // Executes on main thread!

        System.out.println("Invoking new Thread(syncTask).start():");
        Thread asyncThread = new Thread(syncTask, "DedicatedScannerThread-1");
        asyncThread.start();
        asyncThread.join();

        // 2. Parallel scanning with 4 stations using join()
        System.out.println("\n--- Experiment 2: 4 Parallel Scanning Stations with join() ---");
        AtomicInteger grandTotal = new AtomicInteger(0);
        Thread[] stations = new Thread[4];
        int[] batchSizes = {250, 300, 200, 250}; // Total 1000 scripts

        for (int i = 0; i < 4; i++) {
            stations[i] = new Thread(new ScannerRunnable(i + 1, batchSizes[i], grandTotal), "ScannerStation-" + (i + 1));
            // Setting differing priorities
            if (i == 0) stations[i].setPriority(Thread.MAX_PRIORITY);
            if (i == 3) stations[i].setPriority(Thread.MIN_PRIORITY);
        }

        // Start all threads
        for (Thread t : stations) {
            t.start();
        }

        // Verify isAlive() before joining
        System.out.println("Active check immediately after launch: Station-1 isAlive? " + stations[0].isAlive());

        // Use join() to wait for all threads to terminate
        for (Thread t : stations) {
            t.join();
        }

        System.out.println("All scanner stations completed. isAlive check: " + stations[0].isAlive());
        System.out.println(">>> Verified Final Grand Total Scanned: " + grandTotal.get() + " (Expected 1000)");

        // 3. Daemon thread demonstration
        System.out.println("\n--- Experiment 3: Daemon Thread Behavior ---");
        Thread backgroundMonitor = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
            }
        }, "BackgroundMonitorDaemon");
        backgroundMonitor.setDaemon(true);
        backgroundMonitor.start();
        System.out.println("Started daemon thread: " + backgroundMonitor.getName() + " (isDaemon=" + backgroundMonitor.isDaemon() + ")");
        System.out.println("Main thread now exiting. The daemon thread will be abruptly terminated by the JVM.");
    }
}
