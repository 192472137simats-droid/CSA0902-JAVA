# Task B1 – Implement and Instrument the File Store

## Overview
This task implements the bus-pass CSV record store designed in Task A1, validates error-tolerance against damaged lines, and instruments empirical benchmarks comparing unbuffered vs buffered reading and linear scan search growth.

## Files
- `PassStore.java`: Completed "Complete the Code" implementation fulfilling TODO 1 (parsing lines with `BufferedReader`, skipping malformed rows, and returning target record).
- `BufferingBenchmark.java`: Instrumentation harness measuring OS system call overhead (unbuffered vs buffered) and search latency across 1,000 to 50,000 records.
- `expected_output.txt`: Captured output verifying pass retrieval and null handling for missing/damaged records.

## Compilation and Execution
```bash
# 1. Main PassStore Program
javac PassStore.java
java PassStore

# 2. Buffering and Growth Benchmarking
javac -cp . BufferingBenchmark.java
java -cp . BufferingBenchmark
```

## Summary of Findings & Key Answers
- **Why the Damaged Line Does Not Stop the Search**: Line 2 contains `"P1002,DAMAGED LINE"` which has only 2 fields. `find()` validates `fields.length == 5`; upon detecting an invalid column count, it executes `continue`, skipping the malformed row without throwing an unhandled exception, successfully reaching `"P1003"` on line 3.
- **Buffering Speedup**: Unbuffered `FileInputStream.read()` invokes a native operating system kernel transition for every single byte (over 1,000,000 OS context switches per megabyte). `BufferedInputStream` loads 8,192 bytes in a single hardware block transfer, achieving an **$\approx 89\times$ performance acceleration**.
- **Alternative Concept (In-Memory Map vs Disk Scan)**:
  - Performing $q$ searches on an $n$-record file via disk scan costs $\mathcal{O}(q \times n)$ file I/O time.
  - Loading records once into a `HashMap<String, String[]>` costs $\mathcal{O}(n)$ startup time and enables instantaneous $\mathcal{O}(1)$ queries ($\mathcal{O}(n + q)$ total time).
