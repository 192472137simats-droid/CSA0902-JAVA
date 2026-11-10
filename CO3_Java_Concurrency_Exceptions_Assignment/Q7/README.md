# Q7 – Design a Concurrent, Fault-Tolerant Processor from First Principles

## Overview
This task designs and validates a high-throughput, fault-tolerant batch processor for 10,000 semester score files using `ExecutorService`, `Callable<ProcessedRecord>`, `Future`, and atomic collectors.

## Files
- `SemesterResultProcessor.java`: Multi-threaded pipeline testing thread pools of 1, 2, 4, and 8 threads with injected corrupted/missing files.
- `expected_output.txt`: Measured throughput, latency scaling, and fault categorization data.

## Compilation and Execution
```bash
javac SemesterResultProcessor.java
java SemesterResultProcessor
```

## Summary of Findings & Defense
- **Fault-Tolerant Architecture**: By encapsulating processing logic within `Callable<ProcessedRecord>` and handling file-level errors inside task boundaries, corrupt or missing files produce error records without throwing uncaught exceptions to the pool worker threads, allowing the remaining 9,800 files to complete uninterrupted.
- **Thread Pool Scaling**:
  - Moving from 1 thread (106.5 ms) to 4 threads (7.25 ms) yields a **14.7x speedup**.
  - Moving to 8 threads plateaus (~12.2 ms) due to CPU core saturation, context-switching overhead, and work queue contention (Amdahl's Law).
- **Alternative Concept (1 Thread Per File)**: Spawning 10,000 individual OS threads would require $10,000 \times 1\text{ MB} = 10\text{ GB}$ of native stack memory, crashing the JVM with `OutOfMemoryError: unable to create new native thread`. A fixed thread pool of 4–8 worker threads provides optimal resource boundedness.
