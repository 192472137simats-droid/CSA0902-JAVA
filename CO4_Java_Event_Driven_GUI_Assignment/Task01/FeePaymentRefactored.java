import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * Task 1: GUI Design Review - Refactored Student Fee-Payment Window
 * Demonstrates separation of concerns, asynchronous background processing,
 * parameterized PreparedStatement SQL security, and clean user feedback.
 */
public class FeePaymentRefactored {

    // 1. Separate Service Layer (Independent of GUI)
    public static class FeeService {
        private final String dbUrl;

        public FeeService(String dbUrl) {
            this.dbUrl = dbUrl;
        }

        public void processPayment(String regNo, String amountStr) throws IllegalArgumentException, SQLException {
            // Validation
            if (regNo == null || regNo.trim().isEmpty()) {
                throw new IllegalArgumentException("Registration Number cannot be empty.");
            }
            double amount;
            try {
                amount = Double.parseDouble(amountStr.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid amount format. Please enter a valid number.");
            }
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be greater than zero.");
            }

            // Secure JDBC Database Execution using PreparedStatement
            String sql = "INSERT INTO fees (reg_no, amount, payment_time) VALUES (?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(dbUrl);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, regNo.trim());
                ps.setDouble(2, amount);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                ps.executeUpdate();
            }
        }
    }

    // 2. GUI Layer: Clean Presentation Window
    public static class FeeWindow extends Frame {
        private final TextField regNoField = new TextField(15);
        private final TextField amountField = new TextField(10);
        private final Label statusLabel = new Label("Ready");
        private final Button payButton = new Button("Pay");
        private final Button clearButton = new Button("Clear");
        private final Button receiptButton = new Button("Receipt");

        private final FeeService feeService;

        public FeeWindow(FeeService service) {
            super("Student Fee Payment System");
            this.feeService = service;
            initUI();
        }

        private void initUI() {
            setLayout(new BorderLayout(10, 10));

            // Form Panel
            Panel formPanel = new Panel(new GridLayout(2, 2, 5, 5));
            formPanel.add(new Label("Registration No:"));
            formPanel.add(regNoField);
            formPanel.add(new Label("Fee Amount (INR):"));
            formPanel.add(amountField);
            add(formPanel, BorderLayout.CENTER);

            // Button Panel
            Panel buttonPanel = new Panel(new FlowLayout());
            buttonPanel.add(payButton);
            buttonPanel.add(clearButton);
            buttonPanel.add(receiptButton);
            add(buttonPanel, BorderLayout.SOUTH);

            add(statusLabel, BorderLayout.NORTH);

            // Dedicated Event Handlers (Lambda / Separate Listeners)
            payButton.addActionListener(e -> onPayClicked());
            clearButton.addActionListener(e -> onClearClicked());
            receiptButton.addActionListener(e -> onReceiptClicked());

            // Proper Window Closing
            addWindowListener(new WindowAdapter() {
                @Override public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });

            setSize(400, 200);
            setLocationRelativeTo(null);
        }

        private void onPayClicked() {
            String regNo = regNoField.getText();
            String amount = amountField.getText();
            statusLabel.setText("Processing payment in background...");
            payButton.setEnabled(false);

            // Execute on background worker thread to prevent freezing the Event Dispatch Thread (EDT)
            new Thread(() -> {
                try {
                    feeService.processPayment(regNo, amount);
                    // Update UI back on EDT
                    EventQueue.invokeLater(() -> {
                        statusLabel.setText("Payment successful for " + regNo);
                        payButton.setEnabled(true);
                    });
                } catch (Exception ex) {
                    EventQueue.invokeLater(() -> {
                        statusLabel.setText("Error: " + ex.getMessage());
                        payButton.setEnabled(true);
                    });
                }
            }).start();
        }

        private void onClearClicked() {
            regNoField.setText("");
            amountField.setText("");
            statusLabel.setText("Fields cleared.");
        }

        private void onReceiptClicked() {
            statusLabel.setText("Receipt printed for " + regNoField.getText());
        }
    }

    public static void main(String[] args) {
        System.out.println("Task 1: Fee Payment Design Refactoring Initialized.");
        FeeService service = new FeeService("jdbc:sqlite::memory:");
        FeeWindow win = new FeeWindow(service);
        System.out.println("FeeWindow successfully instantiated with separated FeeService and non-blocking handlers.");
        win.dispose();
    }
}
