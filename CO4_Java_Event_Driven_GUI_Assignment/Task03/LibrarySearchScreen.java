import javax.swing.*;
import java.awt.*;

/**
 * Task 3: Campus Library Search Screen
 * Demonstrates proper layout manager nesting:
 * - BorderLayout for the top-level container
 * - FlowLayout / BoxLayout for fixed-size control bars
 * - JScrollPane in BorderLayout.CENTER so that only the results table/area expands during window resizing
 */
public class LibrarySearchScreen extends JFrame {

    private final JTextField searchField = new JTextField(20);
    private final JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"All Categories", "Computer Science", "Mathematics", "Physics", "Literature"});
    private final JButton searchButton = new JButton("Search");

    private final JTextArea resultsArea = new JTextArea(12, 40);
    private final JButton prevButton = new JButton("Prev");
    private final JButton nextButton = new JButton("Next");
    private final JButton reserveButton = new JButton("Reserve");
    private final JLabel statusLabel = new JLabel("Status: Ready. Showing 0 results.");

    public LibrarySearchScreen() {
        super("Campus Library Search Catalog");
        initLayout();
    }

    private void initLayout() {
        // Root container uses BorderLayout: ideal for resizing behavior
        setLayout(new BorderLayout(8, 8));

        // 1. NORTH: Title & Search Row in a nested Panel (Fixed Height)
        JPanel northPanel = new JPanel(new BorderLayout(5, 5));
        JLabel titleLabel = new JLabel("CAMPUS CENTRAL LIBRARY CATALOG", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        northPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        searchRow.add(new JLabel("Keyword:"));
        searchRow.add(searchField);
        searchRow.add(new JLabel("Category:"));
        searchRow.add(categoryCombo);
        searchRow.add(searchButton);
        northPanel.add(searchRow, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);

        // 2. CENTER: Results Area in a JScrollPane (Grows and shrinks dynamically)
        resultsArea.setEditable(false);
        resultsArea.setText("1. Java: The Complete Reference - Herbert Schildt [Available - Shelf CS-04]\n"
                + "2. Introduction to Algorithms - CLRS [Available - Shelf CS-01]\n"
                + "3. Clean Code - Robert C. Martin [Reserved]\n"
                + "4. Design Patterns: Elements of Reusable Object-Oriented Software [Available - Shelf CS-02]\n"
                + "5. Operating System Concepts - Silberschatz [Available - Shelf CS-07]\n");
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        add(scrollPane, BorderLayout.CENTER);

        // 3. SOUTH: Navigation buttons and status bar (Fixed Height)
        JPanel southPanel = new JPanel(new BorderLayout(5, 5));
        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        navButtons.add(prevButton);
        navButtons.add(nextButton);
        navButtons.add(reserveButton);
        southPanel.add(navButtons, BorderLayout.NORTH);

        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        southPanel.add(statusLabel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        // Action handlers
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            statusLabel.setText("Status: Found 5 results matching '" + (query.isEmpty() ? "All" : query) + "'");
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 400);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        System.out.println("Initializing LibrarySearchScreen...");
        LibrarySearchScreen screen = new LibrarySearchScreen();
        Dimension d1 = screen.getSize();
        System.out.println("Default Window Dimensions: " + d1.width + "x" + d1.height);

        // Test resizing to larger dimensions
        screen.setSize(900, 600);
        System.out.println("Resized Window Dimensions: 900x600 (Results area expands dynamically via BorderLayout.CENTER)");
        screen.dispose();
    }
}
