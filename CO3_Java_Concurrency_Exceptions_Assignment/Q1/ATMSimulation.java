import java.io.File;
import java.io.FileNotFoundException;

/**
 * Q1: Handling Failure in a Bank ATM Simulation
 * Demonstrates robust exception handling with multi-catch and guaranteed cleanup in finally.
 */
public class ATMSimulation {

    // Custom unchecked exception for business rule violation
    static class InsufficientBalanceException extends RuntimeException {
        InsufficientBalanceException(String message) {
            super(message);
        }
    }

    private double balance = 10000.0;
    private final File cardReaderConfigFile;

    public ATMSimulation(File configFile) {
        this.cardReaderConfigFile = configFile;
    }

    public void withdraw(String accountInput, String amountInput) throws FileNotFoundException {
        System.out.println("\n[Transaction Initiated] Processing withdrawal...");

        try {
            // 1. Check card-reader hardware/config file (Checked Exception: FileNotFoundException)
            if (!cardReaderConfigFile.exists()) {
                throw new FileNotFoundException("Card reader hardware config missing: " + cardReaderConfigFile.getName());
            }

            // 2. Validate and parse Account Number (Unchecked: IllegalArgumentException)
            if (accountInput == null || accountInput.trim().isEmpty()) {
                throw new IllegalArgumentException("Account number cannot be empty.");
            }
            long accountNo = Long.parseLong(accountInput.trim());

            // 3. Validate and parse Amount (Unchecked: NumberFormatException / IllegalArgumentException)
            double amount = Double.parseDouble(amountInput.trim());
            if (amount <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be strictly positive. Received: " + amount);
            }

            // 4. Verify account balance (Unchecked: InsufficientBalanceException)
            if (amount > balance) {
                throw new InsufficientBalanceException("Requested Rs " + amount + " exceeds balance of Rs " + balance);
            }

            // 5. Successful cash dispensation
            balance -= amount;
            System.out.printf(">>> SUCCESS: Dispensed Rs %.2f to Account %d. Remaining Balance: Rs %.2f%n",
                    amount, accountNo, balance);

        } catch (NumberFormatException e) {
            // Specific format parsing failure
            System.err.println("Handling Format Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Argument validation failure
            System.err.println("Handling Argument Error: " + e.getMessage());
        } catch (InsufficientBalanceException e) {
            // Business rule failure
            System.err.println("Handling Account Error: " + e.getMessage());
        } catch (FileNotFoundException e) {
            // Hardware/Checked failure re-thrown or handled
            System.err.println("Handling Hardware/System Error: " + e.getMessage());
            throw e;
        } finally {
            // Guaranteed cleanup
            System.out.println("[Finally Block Executed] Card ejected safely. Session audit logged. State closed.");
        }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("Q1: Bank ATM Simulation - Failure Handling & Tracing");
        System.out.println("==========================================================");

        File validCard = new File("card_reader.cfg");
        try {
            validCard.createNewFile();
        } catch (Exception ignored) {}

        File missingCard = new File("missing_hardware.cfg");

        ATMSimulation atm = new ATMSimulation(validCard);

        // Test 1: Valid Normal Withdrawal
        System.out.println("--- Test 1: Valid Inputs ---");
        try {
            atm.withdraw("1234567890", "2500");
        } catch (Exception e) {
            System.err.println("Caught in main: " + e);
        }

        // Test 2: Letters instead of numbers (NumberFormatException)
        System.out.println("\n--- Test 2: Non-numeric Amount ---");
        try {
            atm.withdraw("1234567890", "FiveThousand");
        } catch (Exception e) {
            System.err.println("Caught in main: " + e);
        }

        // Test 3: Negative withdrawal amount (IllegalArgumentException)
        System.out.println("\n--- Test 3: Negative Amount ---");
        try {
            atm.withdraw("1234567890", "-500");
        } catch (Exception e) {
            System.err.println("Caught in main: " + e);
        }

        // Test 4: Insufficient Balance (InsufficientBalanceException)
        System.out.println("\n--- Test 4: Overdraft / Insufficient Balance ---");
        try {
            atm.withdraw("1234567890", "50000");
        } catch (Exception e) {
            System.err.println("Caught in main: " + e);
        }

        // Test 5: Missing Card Reader Hardware (FileNotFoundException)
        System.out.println("\n--- Test 5: Missing Card Reader Config ---");
        ATMSimulation brokenAtm = new ATMSimulation(missingCard);
        try {
            brokenAtm.withdraw("1234567890", "1000");
        } catch (FileNotFoundException e) {
            System.err.println("Main caught propagating checked exception: " + e.getMessage());
        }

        // Clean up dummy file
        validCard.delete();
    }
}
