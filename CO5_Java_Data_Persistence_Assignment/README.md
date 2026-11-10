# CO5 Java Assignment 5 – Data Persistence Engineering

**Course Outcome:** CO5 – Java I/O (Streams, Buffering, Serialization) and JDBC (Statements, Transactions, Batches, Metadata)  
**Student Name:** G SAI TEJA  
**Register Number:** 192472137  
**Java Version:** JDK 8 or later  
**Repository Name:** `CSA09-192472137-Assignment5`

---

## Project Contents
- `Assignment_Report.md` – Full 50-mark comprehensive report covering:
  - **Part A (Design & Pseudocode)**: Record layouts, buffering analysis, generic `Dao<T, K>` pattern, serialization & transient fields, and ACID transaction/SQL injection design.
  - **Part B (Implementation & Investigation)**: Working source code, empirical buffering measurements, adversarial input tests, batch benchmarks, and 12-campus production decision.
- `TaskB1/PassStore.java` – "Complete the Code" bus-pass file store with fault-tolerant parsing.
- `TaskB1/BufferingBenchmark.java` – Empirical measurements of unbuffered vs buffered I/O (~89x speedup) and search growth.
- `TaskB2/AdversarialInputDemo.java` – SQL injection exploit demonstration vs PreparedStatement parameter binding defense and damaged CSV handling.
- `TaskB3/PassLoader.java` – "Complete the Code" batch JDBC loader with transactional commit and rollback.
- `TaskB3/BatchBenchmark.java` – 10,000-row batching simulation and rollback recovery verification.
- `TaskB4/MigrationProgram.java` – Full migration pipeline reading legacy CSV, validating records, batching into DB, and inspecting `DatabaseMetaData`.
- Each task folder contains a dedicated `README.md` and captured `expected_output.txt`.

---

## How to Compile and Run

### Task B1: File Store & Buffering Benchmark
```bash
cd TaskB1
javac PassStore.java
java PassStore

javac -cp . BufferingBenchmark.java
java -cp . BufferingBenchmark
cd ..
```

### Task B2: Adversarial Input Analysis
```bash
cd TaskB2
javac AdversarialInputDemo.java
java AdversarialInputDemo
cd ..
```

### Task B3: Batch Processing & Transaction Rollback
```bash
cd TaskB3
javac PassLoader.java BatchBenchmark.java
java BatchBenchmark
cd ..
```

### Task B4: Migration Pipeline & Database Inspection
```bash
cd TaskB4
javac MigrationProgram.java
java MigrationProgram
cd ..
```

---

## Pre-Submission Verification Checklist
- [x] All Part B programs compile cleanly and pass validation tests.
- [x] Complete the Code programs (PassStore, PassLoader) match expected behavior.
- [x] Part A complete with SIMATS-compliant pseudocode, class diagrams, and security analysis.
- [x] Progressive task commit history verified.
