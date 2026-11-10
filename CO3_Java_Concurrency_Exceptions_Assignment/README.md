# CO3 Java Assignment 3 – Concurrency and Exception Handling

**Course Outcome:** CO3 – Exception Handling and Multithreading  
**Student A Name:** G SAI TEJA | **Register Number:** 192472137  
**Student B Name:** G SAI TEJA | **Register Number:** 192472137  
**Java Version:** JDK 8 or later  
**Repository Name:** `CSA09-192472137-Assignment3`

---

## Project Contents
- `Assignment_Report.md` – Full pair-work report with clearly demarcated Student A, Student B, and Joint sections, pseudocode, Big-O tables, and design defenses.
- `Q1/ATMSimulation.java` – Bank ATM withdrawal failure simulation (Throwable hierarchy, multi-catch, finally).
- `Q2/StorageDemo.java` – Online examination portal exception propagation and chaining ("Complete the Code").
- `Q3/SalesReportGenerator.java` – Daily sales report generator (try-with-resources vs manual close, file leak analysis).
- `Q4/ScanningSimulation.java` – Parallel answer-script scanning simulation (Thread vs Runnable, join, isAlive, daemon threads).
- `Q5/SeatBooking.java` – Cinema seat booking synchronization ("Complete the Code" atomic booking).
- `Q5/RaceConditionDemo.java` – Demonstration of severe double-booking under unsynchronized concurrent access.
- `Q6/TokenCounter.java` – Producer-consumer token counter ("Complete the Code" with wait/notifyAll).
- `Q6/WalletDeadlock.java` – Hostel wallet transfer deadlock demonstration and global lock ordering prevention.
- `Q7/SemesterResultProcessor.java` – Concurrent, fault-tolerant batch processor for 10,000 files using ExecutorService.
- Each task folder contains a dedicated `README.md` and captured `expected_output.txt`.

---

## How to Compile and Run

### Q1: ATM Simulation
```bash
cd Q1
javac ATMSimulation.java
java ATMSimulation
cd ..
```

### Q2: Examination Storage Exception Chaining
```bash
cd Q2
javac StorageDemo.java
java StorageDemo
cd ..
```

### Q3: Resource Safety & Daily Sales Report
```bash
cd Q3
javac SalesReportGenerator.java
java SalesReportGenerator
cd ..
```

### Q4: Parallel Scanning Life Cycle
```bash
cd Q4
javac ScanningSimulation.java
java ScanningSimulation
cd ..
```

### Q5: Cinema Seat Booking Race Conditions
```bash
cd Q5
javac SeatBooking.java
java SeatBooking

javac RaceConditionDemo.java
java RaceConditionDemo
cd ..
```

### Q6: Token Counter & Deadlock Avoidance
```bash
cd Q6
javac TokenCounter.java
java TokenCounter

javac WalletDeadlock.java
java WalletDeadlock
cd ..
```

### Q7: Fault-Tolerant Semester Result Processor
```bash
cd Q7
javac SemesterResultProcessor.java
java SemesterResultProcessor
cd ..
```

---

## Pre-Submission Verification Checklist
- [x] All 7 questions compile cleanly and match expected outputs.
- [x] Student A and Student B individual responsibilities documented in `Assignment_Report.md`.
- [x] Progressive task commit history verified.
