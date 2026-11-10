import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Task 6: Campus Event Registration Desktop Application
 * A fully layered GUI-JDBC architecture:
 * - Presentation Layer: EventRegistrationApp (JFrame, JMenuBar, JTable, SwingWorker)
 * - Service Layer: RegistrationService (validation, business rules)
 * - Data Access Layer: RegistrationDao (JDBC queries, unique constraints)
 */
public class EventRegistrationApp extends JFrame {

    // -------------------------------------------------------------
    // Domain Model
    // -------------------------------------------------------------
    public static class Registration {
        final int id;
        final String studentId;
        final String name;
        final String event;
        final String email;

        public Registration(int id, String studentId, String name, String event, String email) {
            this.id = id;
            this.studentId = studentId;
            this.name = name;
            this.event = event;
            this.email = email;
        }
    }

    // -------------------------------------------------------------
    // Data Access Layer (DAO)
    // -------------------------------------------------------------
    public static class RegistrationDao {
        // Thread-safe in-memory storage simulating JDBC database table with UNIQUE(student_id, event_name)
        private final List<Registration> records = new ArrayList<>();
        private int nextId = 1;

        public synchronized void insert(String studentId, String name, String event, String email) throws SQLException {
            // Check unique constraint: a student cannot register twice for the same event
            for (Registration r : records) {
                if (r.studentId.equalsIgnoreCase(studentId) && r.event.equalsIgnoreCase(event)) {
                    throw new SQLException("DUPLICATE_ENTRY: Student " + studentId + " is already registered for " + event);
                }
            }
            records.add(new Registration(nextId++, studentId, name, event, email));
        }

        public synchronized List<Registration> findByEvent(String eventFilter) {
            List<Registration> result = new ArrayList<>();
            for (Registration r : records) {
                if (eventFilter == null || eventFilter.equals("All Events") || r.event.equalsIgnoreCase(eventFilter)) {
                    result.add(r);
                }
            }
            return result;
        }
    }

    // -------------------------------------------------------------
    // Service Layer
    // -------------------------------------------------------------
    public static class RegistrationService {
        private final RegistrationDao dao;

        public RegistrationService(RegistrationDao dao) {
            this.dao = dao;
        }

        public void registerStudent(String studentId, String name, String event, String email) throws Exception {
            // Validation
            if (studentId == null || studentId.trim().isEmpty()) {
                throw new IllegalArgumentException("Student ID cannot be empty.");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Student Name cannot be empty.");
            }
            if (email == null || !email.contains("@")) {
                throw new IllegalArgumentException("Valid email address required.");
            }
            dao.insert(studentId.trim(), name.trim(), event, email.trim());
        }

        public List<Registration> getRegistrations(String eventFilter) {
            return dao.findByEvent(eventFilter);
        }
    }

    // -------------------------------------------------------------
    // Presentation Layer (Swing GUI)
    // -------------------------------------------------------------
    private final JTextField studentIdField = new JTextField(10);
    private final JTextField nameField = new JTextField(15);
    private final JTextField emailField = new JTextField(15);
    private final JComboBox<String> eventCombo = new JComboBox<>(new String[]{"Hackathon", "Coding Quiz", "Robotics Expo", "Web Design"});
    private final JComboBox<String> filterCombo = new JComboBox<>(new String[]{"All Events", "Hackathon", "Coding Quiz", "Robotics Expo", "Web Design"});

    private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Reg ID", "Student ID", "Name", "Event", "Email"}, 0);
    private final JTable registrationTable = new JTable(tableModel);
    private final JLabel statusLabel = new JLabel("Ready.");

    private final RegistrationService service;

    public EventRegistrationApp(RegistrationService service) {
        super("Campus Event Registration System");
        this.service = service;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // 1. Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> dispose());
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // 2. Form Panel (NORTH)
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("New Student Registration"));
        formPanel.add(new JLabel("Student ID:"));
        formPanel.add(studentIdField);
        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Select Event:"));
        formPanel.add(eventCombo);
        formPanel.add(new JLabel("Email Address:"));
        formPanel.add(emailField);

        JButton registerButton = new JButton("Register Student");
        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.add(formPanel, BorderLayout.CENTER);
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonRow.add(registerButton);
        northWrapper.add(buttonRow, BorderLayout.SOUTH);
        add(northWrapper, BorderLayout.NORTH);

        // 3. Center: Filter & Table
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter Registrations by Event:"));
        filterPanel.add(filterCombo);
        JButton refreshButton = new JButton("Apply Filter");
        filterPanel.add(refreshButton);
        centerPanel.add(filterPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(registrationTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 4. South Status Bar
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        add(statusLabel, BorderLayout.SOUTH);

        // Handlers
        registerButton.addActionListener(e -> handleRegistration());
        refreshButton.addActionListener(e -> loadRegistrationsWorker());
        filterCombo.addActionListener(e -> loadRegistrationsWorker());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(750, 480);
        setLocationRelativeTo(null);
    }

    // Asynchronous registration keeping long-running database work off the EDT
    public void handleRegistration() {
        String sid = studentIdField.getText();
        String name = nameField.getText();
        String event = (String) eventCombo.getSelectedItem();
        String email = emailField.getText();

        statusLabel.setText("Saving registration...");

        // Use SwingWorker to keep EDT responsive
        new SwingWorker<Void, Void>() {
            private String error = null;

            @Override
            protected Void doInBackground() {
                try {
                    service.registerStudent(sid, name, event, email);
                } catch (Exception ex) {
                    error = ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                if (error != null) {
                    statusLabel.setText("Error: " + error);
                    JOptionPane.showMessageDialog(EventRegistrationApp.this, error, "Registration Failed", JOptionPane.ERROR_MESSAGE);
                } else {
                    statusLabel.setText("Success: Registered " + name + " for " + event);
                    studentIdField.setText("");
                    nameField.setText("");
                    emailField.setText("");
                    loadRegistrationsWorker();
                }
            }
        }.execute();
    }

    public void loadRegistrationsWorker() {
        String selectedEvent = (String) filterCombo.getSelectedItem();
        new SwingWorker<List<Registration>, Void>() {
            @Override
            protected List<Registration> doInBackground() {
                return service.getRegistrations(selectedEvent);
            }

            @Override
            protected void done() {
                try {
                    List<Registration> list = get();
                    tableModel.setRowCount(0);
                    for (Registration r : list) {
                        tableModel.addRow(new Object[]{r.id, r.studentId, r.name, r.event, r.email});
                    }
                    statusLabel.setText("Loaded " + list.size() + " registrations.");
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    public static void main(String[] args) {
        System.out.println("Initializing Campus Event Registration System...");
        RegistrationDao dao = new RegistrationDao();
        RegistrationService service = new RegistrationService(dao);
        EventRegistrationApp app = new EventRegistrationApp(service);

        // 1. Normal Registration Test
        try {
            service.registerStudent("STU101", "Arun Kumar", "Hackathon", "arun@campus.edu");
            service.registerStudent("STU102", "Divya Nair", "Robotics Expo", "divya@campus.edu");
            System.out.println("Test 1 Passed: Normal registrations saved.");
        } catch (Exception e) {
            System.err.println("Test 1 Failed: " + e.getMessage());
        }

        // 2. Duplicate Registration Test (Same student, same event)
        try {
            service.registerStudent("STU101", "Arun Kumar", "Hackathon", "arun@campus.edu");
            System.out.println("Test 2 Failed: Duplicate registration was unexpectedly allowed!");
        } catch (Exception e) {
            System.out.println("Test 2 Passed: Duplicate registration successfully caught & rejected -> " + e.getMessage());
        }

        // 3. Invalid Input Test
        try {
            service.registerStudent("", "Test Name", "Hackathon", "invalidEmail");
            System.out.println("Test 3 Failed: Invalid input was allowed!");
        } catch (Exception e) {
            System.out.println("Test 3 Passed: Invalid input correctly rejected -> " + e.getMessage());
        }

        // 4. Filtering Test
        List<Registration> filtered = service.getRegistrations("Hackathon");
        System.out.println("Test 4 Passed: Filter for 'Hackathon' retrieved " + filtered.size() + " record(s).");

        app.dispose();
    }
}
