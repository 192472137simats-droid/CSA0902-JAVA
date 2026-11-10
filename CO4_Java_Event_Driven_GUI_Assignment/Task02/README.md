# Task 2 – From Requirement to Event-Driven Design: Hospital Outpatient Appointment Desk

## Overview
This task models and implements an event-driven appointment scheduling GUI for a hospital outpatient reception desk.

## Files
- `OutpatientDesk.java`: Working event-driven GUI featuring dynamic doctor list updates, form validation, and window lifecycle listeners.
- `expected_output.txt`: Captured execution logs verifying component instantiation and booking confirmation.

## Compilation and Execution
```bash
javac OutpatientDesk.java
java OutpatientDesk
```

## Key Deliverables & Event Models
- **Event Modeling Table**:
  | Event Source | Event Object | Listener Interface | Action Taken |
  |---|---|---|---|
  | `deptChoice` (Choice) | `ItemEvent` | `ItemListener` | Clears and refreshes doctor dropdown via `refreshDoctorList()` |
  | `bookButton` (Button) | `ActionEvent` | `ActionListener` | Validates patient ID and registers appointment slot |
  | `OutpatientDesk` (Frame) | `WindowEvent` | `WindowListener` | Checks `bookingInProgress` flag and disposes window |
- **Alternative Concept (Preloading vs On-Demand DB Queries)**:
  - Querying the database on every department click introduces network latency ($\approx 50\text{–}200\text{ ms}$) on the UI thread.
  - Preloading departments and doctors once into a `Map<String, List<String>>` requires negligible memory ($\approx 5\text{ KB}$) and makes dropdown switching instantaneous ($< 0.1\text{ ms}$).
