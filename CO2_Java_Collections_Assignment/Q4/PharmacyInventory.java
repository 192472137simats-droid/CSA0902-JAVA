import java.util.*;

/**
 * Q4: Choosing a Map for a Pharmacy Inventory
 * Problem Context: Hospital Pharmacy Stock Lookup
 * 
 * Compares HashMap, TreeMap, and LinkedHashMap across:
 * - 100,000 lookups
 * - Code-order report generation
 * - Insertion-order report generation
 * Tested at 3 catalog sizes: 500, 5,000, and 50,000 medicines.
 */
public class PharmacyInventory {

    static class Medicine {
        final String code;
        final String name;
        final int quantity;
        final double unitPrice;

        Medicine(String code, String name, int quantity, double unitPrice) {
            this.code = code;
            this.name = name;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        @Override
        public String toString() {
            return code + ":" + name + "(Qty:" + quantity + ")";
        }
    }

    private static void benchmarkCatalog(int catalogSize, int lookupCount) {
        System.out.println("====================================================================================================");
        System.out.println("Catalog Size: " + catalogSize + " medicines | Lookups: " + lookupCount);
        System.out.println("====================================================================================================");

        // Generate synthetic medicines in pseudo-random insertion order
        List<Medicine> medicineList = new ArrayList<>(catalogSize);
        Random rnd = new Random(101);
        for (int i = 0; i < catalogSize; i++) {
            // Medicine codes like MED-08421
            int codeNum = (i * 7919 + 1013) % 90000 + 10000;
            String code = "MED-" + codeNum;
            medicineList.add(new Medicine(code, "Drug_" + i, 100 + rnd.nextInt(500), 10.0 + rnd.nextDouble() * 50));
        }

        // 1. Populate Maps
        Map<String, Medicine> hashMap = new HashMap<>();
        Map<String, Medicine> treeMap = new TreeMap<>();
        Map<String, Medicine> linkedHashMap = new LinkedHashMap<>();

        for (Medicine m : medicineList) {
            hashMap.put(m.code, m);
            treeMap.put(m.code, m);
            linkedHashMap.put(m.code, m);
        }

        // Prepare lookup keys
        String[] queryCodes = new String[lookupCount];
        for (int i = 0; i < lookupCount; i++) {
            queryCodes[i] = medicineList.get(rnd.nextInt(catalogSize)).code;
        }

        // 2. Measure 100,000 lookups
        long start = System.nanoTime();
        long dummyHM = 0;
        for (String q : queryCodes) dummyHM += hashMap.get(q).quantity;
        double hmLookupMs = (System.nanoTime() - start) / 1_000_000.0;

        start = System.nanoTime();
        long dummyTM = 0;
        for (String q : queryCodes) dummyTM += treeMap.get(q).quantity;
        double tmLookupMs = (System.nanoTime() - start) / 1_000_000.0;

        start = System.nanoTime();
        long dummyLHM = 0;
        for (String q : queryCodes) dummyLHM += linkedHashMap.get(q).quantity;
        double lhmLookupMs = (System.nanoTime() - start) / 1_000_000.0;

        // 3. Measure Code-Order Report
        // TreeMap: Natural in-order iteration
        start = System.nanoTime();
        int tmCodeReportCount = 0;
        for (Map.Entry<String, Medicine> e : treeMap.entrySet()) tmCodeReportCount++;
        double tmCodeReportMs = (System.nanoTime() - start) / 1_000_000.0;

        // HashMap: Must extract and sort keys
        start = System.nanoTime();
        List<String> hmSortedKeys = new ArrayList<>(hashMap.keySet());
        Collections.sort(hmSortedKeys);
        int hmCodeReportCount = 0;
        for (String k : hmSortedKeys) {
            Medicine m = hashMap.get(k);
            hmCodeReportCount++;
        }
        double hmCodeReportMs = (System.nanoTime() - start) / 1_000_000.0;

        // LinkedHashMap: Must extract and sort keys
        start = System.nanoTime();
        List<String> lhmSortedKeys = new ArrayList<>(linkedHashMap.keySet());
        Collections.sort(lhmSortedKeys);
        int lhmCodeReportCount = 0;
        for (String k : lhmSortedKeys) {
            Medicine m = linkedHashMap.get(k);
            lhmCodeReportCount++;
        }
        double lhmCodeReportMs = (System.nanoTime() - start) / 1_000_000.0;

        // 4. Measure Insertion-Order Report
        // LinkedHashMap: Natural insertion order iteration
        start = System.nanoTime();
        int lhmInsertReportCount = 0;
        for (Map.Entry<String, Medicine> e : linkedHashMap.entrySet()) lhmInsertReportCount++;
        double lhmInsertReportMs = (System.nanoTime() - start) / 1_000_000.0;

        // Print Report Table
        System.out.printf("%-18s | %-16s | %-24s | %-24s%n", "Map Implementation", "100k Lookups (ms)", "Code-Order Report (ms)", "Insertion-Order Report (ms)");
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.printf("%-18s | %16.3f | %24.3f | %-24s%n", "HashMap", hmLookupMs, hmCodeReportMs, "Requires External Tracking");
        System.out.printf("%-18s | %16.3f | %24.3f | %-24s%n", "TreeMap", tmLookupMs, tmCodeReportMs, "Requires External Tracking");
        System.out.printf("%-18s | %16.3f | %24.3f | %24.3f%n", "LinkedHashMap", lhmLookupMs, lhmCodeReportMs, lhmInsertReportMs);
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("Q4: Hospital Pharmacy Stock Lookup & Reporting Benchmark");
        System.out.println("Comparing HashMap vs TreeMap vs LinkedHashMap\n");

        benchmarkCatalog(500, 100000);
        benchmarkCatalog(5000, 100000);
        benchmarkCatalog(50000, 100000);
    }
}
