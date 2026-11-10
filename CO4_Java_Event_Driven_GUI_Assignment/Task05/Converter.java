import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Converter extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target =
            new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    private static double rate(String currency) {
        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public Converter() {
        super("Currency Converter");
        setLayout(new FlowLayout());
        JButton convertButton = new JButton("Convert");
        add(new JLabel("INR"));
        add(amount);
        add(target);
        add(convertButton);
        add(result);

        // TODO 1: Register ONE lambda listener for both the Convert button and the
        // Enter key in the amount field
        ActionListener convertListener = e -> convert();
        convertButton.addActionListener(convertListener);
        amount.addActionListener(convertListener);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
    }

    private void convert() {
        // TODO 2: Parse the amount and show the converted value with 2 decimals, or
        // "Invalid amount" if parsing fails
        try {
            double inr = Double.parseDouble(amount.getText().trim());
            String cur = (String) target.getSelectedItem();
            double converted = inr * rate(cur);
            result.setText(String.format("%.2f %s", converted, cur));
        } catch (NumberFormatException e) {
            result.setText("Invalid amount");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Converter c = new Converter();
            System.out.println("Converter GUI initialized.");

            // Test 1: Valid conversion (10,000 INR to USD)
            c.amount.setText("10000");
            c.target.setSelectedItem("USD");
            c.convert();
            System.out.println("Test 1 (Valid 10000 INR to USD): " + c.result.getText());

            // Test 2: Valid conversion (5,000 INR to EUR)
            c.amount.setText("5000");
            c.target.setSelectedItem("EUR");
            c.convert();
            System.out.println("Test 2 (Valid 5000 INR to EUR): " + c.result.getText());

            // Test 3: Invalid amount ("abc")
            c.amount.setText("abc");
            c.convert();
            System.out.println("Test 3 (Invalid 'abc'): " + c.result.getText());

            c.dispose();
        });
    }
}
