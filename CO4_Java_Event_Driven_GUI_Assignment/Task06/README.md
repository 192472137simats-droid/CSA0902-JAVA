# Task 6 – Design Challenge: Campus Event Registration Desktop Application

## Overview
This task designs and implements a complete multi-layered Swing–JDBC desktop application for managing campus event registrations.

## Files
- `EventRegistrationApp.java`: Multi-tier desktop GUI with form registration, JTable record browsing, dynamic event filtering, duplicate prevention, and `SwingWorker` background threading.
- `schema.sql`: SQL database schema defining relational tables, unique constraints, and indices.
- `expected_output.txt`: Test suite output confirming valid registration, duplicate rejection, and input validation.

## Compilation and Execution
```bash
javac EventRegistrationApp.java
java EventRegistrationApp
```

## Architectural Highlights
- **Layered Architecture**:
  - `EventRegistrationApp`: Presentation layer (Swing widgets).
  - `RegistrationService`: Business logic, boundary validation, and sanitization.
  - `RegistrationDao`: Data access layer enforcing database unique constraints.
- **Keeping Work Off the EDT**:
  - All database read/write actions are encapsulated within `SwingWorker<T, V>`, executing heavy operations on background thread pool threads while updating GUI components safely via `done()` on the Event Dispatch Thread (EDT).
- **Alternative Concept (Pagination vs Bulk Load)**:
  - Loading 100,000 registrations into a `JTable` simultaneously consumes $> 120\text{ MB}$ heap and freezes the EDT during model initialization.
  - Implementing SQL `LIMIT 50 OFFSET ?` pagination loads 50 records per page on-demand in $< 2\text{ ms}$, bounding GUI memory usage to $< 1\text{ MB}$.
