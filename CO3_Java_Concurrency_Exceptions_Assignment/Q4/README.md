# Q4 – Thread Creation and the Thread Life Cycle

## Overview
This task investigates parallel answer-script scanning across four concurrent stations, evaluating `Thread` vs `Runnable`, execution life cycles (`start()`, `sleep()`, `join()`, `isAlive()`), non-deterministic thread scheduling, and daemon thread behavior.

## Files
- `ScanningSimulation.java`: Comprehensive simulation program demonstrating synchronous `run()` vs asynchronous `start()`, parallel thread joining, priority tests, and daemon threads.
- `expected_output.txt`: Captured runtime logs confirming thread execution contexts and grand total validation.

## Compilation and Execution
```bash
javac ScanningSimulation.java
java ScanningSimulation
```

## Summary of Findings & Key Answers
- **Why `run()` Does Not Create a New Thread**: Invoking `run()` directly is a standard synchronous method call on the caller thread (`Thread.currentThread().getName()` remains `main`). Only `start()` requests the JVM and underlying OS kernel to allocate a new call stack and spawn an independent thread of execution.
- **Role of `join()`**: `t.join()` pauses the calling thread (main) until thread `t` transitions to the `TERMINATED` state. Without `join()`, main would read `grandTotal` prematurely while background stations are still processing, causing inconsistent or zero totals.
- **Why Output Order is Non-Deterministic**: The operating system thread scheduler assigns CPU time slices dynamically based on core availability, cache state, and hardware interrupts. Thread priorities (`setPriority()`) are merely hints to the OS; a correct multithreaded program must never depend on the execution order of concurrent worker threads.
