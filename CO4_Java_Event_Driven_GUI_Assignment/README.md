# CO4 Java Assignment 4 – Event-Driven GUI Design

**Course Outcome:** CO4 – Event-Driven GUI Design, AWT/Swing Components, Layouts, and JDBC Integration  
**Student Name:** G SAI TEJA  
**Register Number:** 192472137  
**Java Version:** JDK 8 or later  
**Repository Name:** `CSA09-192472137-Assignment4`

---

## Project Contents
- `Assignment_Report.md` – Full 50-mark comprehensive report covering Tasks 1 through 6, containment trees, event models, pseudocode, and resizing proofs.
- `Task01/FeePaymentRefactored.java` – Refactored student fee payment window with separated `FeeService` and background threading.
- `Task02/OutpatientDesk.java` – Hospital appointment desk with dynamic doctor choice refresh and modal close protection.
- `Task03/LibrarySearchScreen.java` – Library search screen with responsive nested `BorderLayout` and results area resizing.
- `Task04/DrawPad.java` – "Complete the Code" forensics fixing misspelled `mouseClicked` and transient canvas drawing.
- `Task05/Converter.java` – "Complete the Code" currency converter implementing lambda-based multi-control action dispatch.
- `Task06/EventRegistrationApp.java` – Layered Swing-JDBC desktop application with input validation, duplicate rejection, and `SwingWorker`.
- `Task06/schema.sql` – Database DDL script with tables and unique constraints.
- Each task folder contains a dedicated `README.md` and captured `expected_output.txt`.

---

## How to Compile and Run

### Task 1: Fee Payment Refactoring
```bash
cd Task01
javac FeePaymentRefactored.java
java FeePaymentRefactored
cd ..
```

### Task 2: Outpatient Appointment Desk
```bash
cd Task02
javac OutpatientDesk.java
java OutpatientDesk
cd ..
```

### Task 3: Campus Library Search Screen
```bash
cd Task03
javac LibrarySearchScreen.java
java LibrarySearchScreen
cd ..
```

### Task 4: Event-Handling Forensics (DrawPad)
```bash
cd Task04
javac DrawPad.java
java DrawPad
cd ..
```

### Task 5: Event-Handling Styles (Currency Converter)
```bash
cd Task05
javac Converter.java
java Converter
cd ..
```

### Task 6: Design Challenge (Event Registration System)
```bash
cd Task06
javac EventRegistrationApp.java
java EventRegistrationApp
cd ..
```

---

## Pre-Submission Verification Checklist
- [x] All 6 task programs compile cleanly and pass validation tests.
- [x] Complete the Code programs (Task 4, Task 5) match required behavior.
- [x] Comprehensive `Assignment_Report.md` prepared with full diagrams, pseudocode, and Big-O tables.
- [x] Progressive task commit history verified.
