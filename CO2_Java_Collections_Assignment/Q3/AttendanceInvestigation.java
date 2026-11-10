import java.util.HashSet;
import java.util.Set;

/**
 * Q3: In-depth Investigation of the hashCode-equals Contract
 * Implements the 3 challenge tests:
 * 1. Scans where HashSet appears to work correctly (same object reference)
 * 2. Scans deliberately exposing duplicate entries (separate instances with same regNo)
 * 3. Mutation after insertion test (mutable key causing lost elements in HashSet)
 */
public class AttendanceInvestigation {

    // Flawed student class: equals() overridden, but hashCode() NOT overridden
    static class FlawedStudent {
        String regNo;
        String name;

        FlawedStudent(String regNo, String name) {
            this.regNo = regNo;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FlawedStudent)) return false;
            return regNo.equals(((FlawedStudent) o).regNo);
        }

        @Override
        public String toString() {
            return regNo + " " + name;
        }
    }

    // Mutable student with hashCode to demonstrate mutation after insertion
    static class MutableStudent {
        String regNo;
        String name;

        MutableStudent(String regNo, String name) {
            this.regNo = regNo;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MutableStudent)) return false;
            return regNo.equals(((MutableStudent) o).regNo);
        }

        @Override
        public int hashCode() {
            return regNo.hashCode();
        }
    }

    public static void main(String[] args) {
        System.out.println("--- Test 1: Re-scanning Same Object Reference (Appears to work) ---");
        Set<FlawedStudent> test1Set = new HashSet<>();
        FlawedStudent s1 = new FlawedStudent("192311001", "Arun");
        test1Set.add(s1);
        test1Set.add(s1); // Same reference added again
        System.out.println("Predicted Size: 1 | Actual Size: " + test1Set.size());
        System.out.println("Explanation: HashSet checks (e == p) reference equality first, so it detects duplicate without calling hashCode().\n");

        System.out.println("--- Test 2: Two Distinct Instances with Same RegNo (Exposes bug) ---");
        Set<FlawedStudent> test2Set = new HashSet<>();
        test2Set.add(new FlawedStudent("192311001", "Arun"));
        test2Set.add(new FlawedStudent("192311002", "Divya"));
        test2Set.add(new FlawedStudent("192311001", "Arun")); // Distinct instance
        System.out.println("Predicted Size without hashCode(): 3 | Actual Size: " + test2Set.size());
        System.out.println("Explanation: Default Object.hashCode() uses system memory addresses. Two instances land in different buckets; equals() is never called!\n");

        System.out.println("--- Test 3: Modifying Key Field After Insertion (Lost element bug) ---");
        Set<MutableStudent> test3Set = new HashSet<>();
        MutableStudent ms = new MutableStudent("192311099", "Kavya");
        test3Set.add(ms);
        System.out.println("Before mutation: contains 192311099? " + test3Set.contains(ms));
        
        // Mutate the key field that determines hashCode
        ms.regNo = "192311888";
        System.out.println("After mutating regNo to 192311888: contains? " + test3Set.contains(ms));
        System.out.println("Set size: " + test3Set.size() + " (element is orphaned in old bucket; cannot be found or removed!)");
    }
}
