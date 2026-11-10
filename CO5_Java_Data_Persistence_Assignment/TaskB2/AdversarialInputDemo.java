import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Task B2: Engineer an Adversarial Input
 * Demonstrates:
 * 1. SQL Injection vulnerability in dynamic string concatenation vs immunity in PreparedStatement
 * 2. Fault tolerance when parsing damaged / adversarial CSV input files
 */
public class AdversarialInputDemo {

    // Simulates an SQL Execution Engine to demonstrate AST/lexical injection vs parameter binding
    static class MockDatabase {
        private final Map<String, String> users = new HashMap<>();

        MockDatabase() {
            users.put("admin", "SuperSecretPass123!");
            users.put("student1", "pass2026");
        }

        // Simulates dynamic SQL string concatenation: parsing raw clauses
        public boolean unsafeLogin(String rawSql) {
            System.out.println("Executing Unsafe Raw SQL: " + rawSql);
            // Lexical simulation: if the SQL contains an injected tautology "OR '1'='1'", it bypasses authentication
            if (rawSql.contains("' OR '1'='1") || rawSql.contains("' OR 1=1")) {
                return true; // Bypass triggered!
            }
            // Normal parsing
            return false;
        }

        // Simulates PreparedStatement parameter binding
        public boolean safeLogin(String usernameParam, String passwordParam) {
            System.out.printf("Executing Parameterized Query: SELECT * FROM users WHERE id = ? AND pwd = ? [Param1='%s', Param2='%s']%n",
                    usernameParam, passwordParam);
            // Parameters are compared as strict literal values, completely neutralizing syntax injection
            return users.containsKey(usernameParam) && users.get(usernameParam).equals(passwordParam);
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("==========================================================================");
        System.out.println("Task B2: Adversarial Input Analysis & SQL Injection Demonstration");
        System.out.println("==========================================================================");

        MockDatabase db = new MockDatabase();
        String maliciousPayload = "admin' OR '1'='1' --";
        String dummyPassword = "anyPassword";

        // 1. Unsafe Dynamic Concatenation Test
        System.out.println("--- 1. Testing Unsafe Dynamic SQL Concatenation ---");
        String unsafeSql = "SELECT * FROM users WHERE id='" + maliciousPayload + "' AND pwd='" + dummyPassword + "'";
        boolean unsafeResult = db.unsafeLogin(unsafeSql);
        System.out.println("Result: Authenticated? " + unsafeResult);
        System.out.println("Vulnerability Confirmed: Injected tautology 'OR '1'='1'' changed query semantics, granting unauthorized access.\n");

        // 2. Safe Parameterized PreparedStatement Test
        System.out.println("--- 2. Testing PreparedStatement Parameter Binding ---");
        boolean safeResult = db.safeLogin(maliciousPayload, dummyPassword);
        System.out.println("Result: Authenticated? " + safeResult);
        System.out.println("Protection Confirmed: PreparedStatement treated payload as a literal string value. Injection completely neutralized.\n");

        // 3. Damaged Bus-Pass File Test
        System.out.println("--- 3. Testing Adversarial & Corrupted CSV File Inputs ---");
        Path tempCsv = Files.createTempFile("adversarial_passes", ".csv");
        // Inject multiple corrupted line formats:
        // Line 1: Valid
        // Line 2: Empty line
        // Line 3: Insufficient fields (2 fields)
        // Line 4: Non-numeric amount ("TenThousand")
        // Line 5: Valid record to verify resumption
        Files.writeString(tempCsv, "P101,19231101,Aakash,10,3500.0\n");
        Files.writeString(tempCsv, "\n", StandardOpenOption.APPEND);
        Files.writeString(tempCsv, "P102,MALFORMED_SHORT\n", StandardOpenOption.APPEND);
        Files.writeString(tempCsv, "P103,19231103,Bhavna,12,TenThousand\n", StandardOpenOption.APPEND);
        Files.writeString(tempCsv, "P104,19231104,Chetan,14,4000.0\n", StandardOpenOption.APPEND);

        int validCount = 0;
        int corruptedCount = 0;
        try (var reader = Files.newBufferedReader(tempCsv)) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) {
                    System.out.printf("Line %d: Empty line skipped.%n", lineNum);
                    corruptedCount++;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length != 5) {
                    System.out.printf("Line %d: Invalid field count (%d != 5). Skipping malformed row: %s%n", lineNum, parts.length, line);
                    corruptedCount++;
                    continue;
                }
                try {
                    double amt = Double.parseDouble(parts[4]);
                    validCount++;
                    System.out.printf("Line %d: Successfully parsed valid pass: %s (Rs %.2f)%n", lineNum, parts[0], amt);
                } catch (NumberFormatException e) {
                    System.out.printf("Line %d: Non-numeric fee '%s'. Skipping.%n", lineNum, parts[4]);
                    corruptedCount++;
                }
            }
        }
        System.out.printf("%nFile Processing Summary -> Valid Records: %d | Corrupted / Skipped: %d%n", validCount, corruptedCount);
        Files.delete(tempCsv);
    }
}
