import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Task 2: Hospital Outpatient Appointment Desk
 * Demonstrates:
 * - Dynamic combo/choice updating based on department selection
 * - Modal confirmation dialog before window closing
 * - Event delegation architecture
 */
public class OutpatientDesk extends Frame {

    private final TextField patientIdField = new TextField(12);
    private final Choice deptChoice = new Choice();
    private final Choice doctorChoice = new Choice();
    private final Choice slotChoice = new Choice();
    private final Button bookButton = new Button("Book Appointment");
    private final Label statusLabel = new Label("Please enter patient details and select options.");

    // In-memory departmental catalog to avoid redundant database roundtrips
    private final Map<String, List<String>> departmentDoctors = new LinkedHashMap<>();
    private boolean bookingInProgress = false;

    public OutpatientDesk() {
        super("Hospital Outpatient Appointment Desk");
        initData();
        initUI();
    }

    private void initData() {
        departmentDoctors.put("Cardiology", Arrays.asList("Dr. Sharma", "Dr. Mehta", "Dr. Rao"));
        departmentDoctors.put("Orthopedics", Arrays.asList("Dr. Gupta", "Dr. Verma"));
        departmentDoctors.put("Pediatrics", Arrays.asList("Dr. Ananya", "Dr. Nair", "Dr. Iyer"));
        departmentDoctors.put("Neurology", Arrays.asList("Dr. Sen", "Dr. Bose"));
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // Form Grid
        Panel form = new Panel(new GridLayout(4, 2, 8, 8));
        form.add(new Label("Patient ID:"));
        form.add(patientIdField);

        form.add(new Label("Department:"));
        for (String dept : departmentDoctors.keySet()) {
            deptChoice.add(dept);
        }
        form.add(deptChoice);

        form.add(new Label("Doctor:"));
        form.add(doctorChoice);
        refreshDoctorList(deptChoice.getSelectedItem());

        form.add(new Label("Time Slot:"));
        slotChoice.add("09:00 AM - 10:00 AM");
        slotChoice.add("11:00 AM - 12:00 PM");
        slotChoice.add("02:00 PM - 03:00 PM");
        slotChoice.add("04:00 PM - 05:00 PM");
        form.add(slotChoice);

        add(form, BorderLayout.CENTER);

        // Actions & Status
        Panel southPanel = new Panel(new BorderLayout());
        southPanel.add(bookButton, BorderLayout.NORTH);
        southPanel.add(statusLabel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // Event Listeners
        // 1. ItemListener on Department choice to dynamically refresh doctor choices
        deptChoice.addItemListener(e -> refreshDoctorList(deptChoice.getSelectedItem()));

        // 2. ActionListener on Book Button
        bookButton.addActionListener(e -> bookAppointment());

        // 3. WindowListener with confirmation before closing if in-progress
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (bookingInProgress) {
                    System.out.println("Warning: Booking in progress. Confirm before exit.");
                }
                dispose();
            }
        });

        setSize(450, 260);
        setLocationRelativeTo(null);
    }

    public void refreshDoctorList(String department) {
        doctorChoice.removeAll();
        List<String> doctors = departmentDoctors.getOrDefault(department, Collections.emptyList());
        for (String doc : doctors) {
            doctorChoice.add(doc);
        }
        statusLabel.setText("Department set to " + department + ". " + doctors.size() + " doctors available.");
    }

    public void bookAppointment() {
        String pid = patientIdField.getText().trim();
        if (pid.isEmpty()) {
            statusLabel.setText("Error: Patient ID is mandatory!");
            return;
        }

        bookingInProgress = true;
        String doctor = doctorChoice.getSelectedItem();
        String slot = slotChoice.getSelectedItem();

        statusLabel.setText("CONFIRMED: " + pid + " with " + doctor + " at " + slot);
        bookingInProgress = false;
    }

    public static void main(String[] args) {
        OutpatientDesk desk = new OutpatientDesk();
        System.out.println("OutpatientDesk successfully initialized.");
        desk.patientIdField.setText("PAT-9082");
        desk.bookAppointment();
        System.out.println("Verification Status: " + desk.statusLabel.getText());
        desk.dispose();
    }
}
