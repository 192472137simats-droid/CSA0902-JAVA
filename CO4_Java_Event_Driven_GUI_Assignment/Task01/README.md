# Task 1 – GUI Design Review: Diagnose and Repair a Poor Event-Handling Design

## Overview
This task analyzes and repairs a defective monolithic AWT student fee-payment window authored by a junior developer.

## Files
- `FeePaymentRefactored.java`: Fully refactored solution separating presentation (`FeeWindow`) from business logic and database access (`FeeService`).
- `expected_output.txt`: Execution log verifying architectural initialization.

## Compilation and Execution
```bash
javac FeePaymentRefactored.java
java FeePaymentRefactored
```

## Key Architectural Diagnoses
1. **Lack of Separation of Concerns**: Monolithic class mixing UI widgets, database network connections, and business validation.
2. **String Equality Bug**: Comparing action commands with `==` (`e.getActionCommand() == "Pay"`) compares string reference identity instead of value content (`.equals()`).
3. **Event Dispatch Thread (EDT) Freezing**: Calling `Thread.sleep(3000)` and performing blocking database I/O directly inside `actionPerformed()` blocks the EDT, causing the entire GUI window to freeze, stop redrawing, and become unresponsive.
4. **SQL Injection Vulnerability**: String concatenation (`"INSERT INTO fees VALUES('" + regNo.getText() + ...`) allows arbitrary SQL injection. Replaced with parameterized `PreparedStatement`.
5. **Resource Leaks**: Missing `Connection.close()` and unhandled `SQLException` / `NumberFormatException`.
