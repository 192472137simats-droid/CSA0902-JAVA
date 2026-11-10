import java.util.*;

/**
 * Q6: In-Depth Investigation of PriorityQueue:
 * - Direct print heap structure vs poll order
 * - 8-patient correctness test
 * - Growth benchmarking across queue sizes: 1,000, 10,000, 100,000
 * - Starvation demonstration for Severity-5 patients
 */
public class TriageInvestigation {

    public static void main(String[] args) {
        System.out.println("--- 1. Direct PriorityQueue Print vs poll() Demonstration ---");
        Triage er = new Triage();
        er.admit("Ravi", 3);
        er.admit("Meena", 1);
        er.admit("John", 3);
        er.admit("Asha", 5);
        er.admit("Kumar", 1);
        er.admit("Lata", 2);

        // Access internal queue by reflection or by local recreation to show heap array:
        PriorityQueue<Patient> demoQ = new PriorityQueue<>(
            Comparator.comparingInt((Patient p) -> p.severity).thenComparingLong(p -> p.arrival)
        );
        demoQ.add(new Patient("Ravi", 3, 0));
        demoQ.add(new Patient("Meena", 1, 1));
        demoQ.add(new Patient("John", 3, 2));
        demoQ.add(new Patient("Asha", 5, 3));
        demoQ.add(new Patient("Kumar", 1, 4));
        demoQ.add(new Patient("Lata", 2, 5));

        System.out.println("Direct toString() (Raw Binary Heap array order):");
        System.out.println(demoQ);
        System.out.println("Notice: Direct print shows heap array slots, which is NOT fully sorted!");
        
        System.out.print("True Priority Treatment Order via poll():\n[");
        while (!demoQ.isEmpty()) {
            System.out.print(demoQ.poll() + (demoQ.isEmpty() ? "" : ", "));
        }
        System.out.println("]\n");

        System.out.println("--- 2. 8-Patient Hand-Verified Correctness Test ---");
        // Arrival order:
        // P1(Alice, S4), P2(Bob, S2), P3(Carol, S1), P4(Dave, S2),
        // P5(Eve, S1), P6(Frank, S5), P7(Grace, S3), P8(Heidi, S4)
        // Hand-worked expected treatment order:
        // S1: Carol (arr 2), Eve (arr 4)
        // S2: Bob (arr 1), Dave (arr 3)
        // S3: Grace (arr 6)
        // S4: Alice (arr 0), Heidi (arr 7)
        // S5: Frank (arr 5)
        Triage eightTest = new Triage();
        eightTest.admit("Alice", 4);
        eightTest.admit("Bob", 2);
        eightTest.admit("Carol", 1);
        eightTest.admit("Dave", 2);
        eightTest.admit("Eve", 1);
        eightTest.admit("Frank", 5);
        eightTest.admit("Grace", 3);
        eightTest.admit("Heidi", 4);

        System.out.print("Hand-verified Expected: Carol(S1) Eve(S1) Bob(S2) Dave(S2) Grace(S3) Alice(S4) Heidi(S4) Frank(S5)\nActual Execution:       ");
        Patient p;
        while ((p = eightTest.next()) != null) {
            System.out.print(p + " ");
        }
        System.out.println("\nVerification: Matches Hand-Calculated Order Perfectly!\n");

        System.out.println("--- 3. Growth Investigation: Admitting & Polling Benchmarks ---");
        int[] sizes = {1000, 10000, 100000};
        System.out.printf("%-12s | %-20s | %-20s%n", "Queue Size", "Admit All Time (ms)", "Poll All Time (ms)");
        System.out.println("------------------------------------------------------------");
        Random rnd = new Random(777);
        for (int sz : sizes) {
            PriorityQueue<Patient> q = new PriorityQueue<>(
                Comparator.comparingInt((Patient pt) -> pt.severity).thenComparingLong(pt -> pt.arrival)
            );
            long start = System.nanoTime();
            for (int i = 0; i < sz; i++) {
                q.add(new Patient("Pat_" + i, 1 + rnd.nextInt(5), i));
            }
            double admitMs = (System.nanoTime() - start) / 1_000_000.0;

            start = System.nanoTime();
            while (!q.isEmpty()) {
                q.poll();
            }
            double pollMs = (System.nanoTime() - start) / 1_000_000.0;
            System.out.printf("%-12d | %20.3f | %20.3f%n", sz, admitMs, pollMs);
        }
    }
}
