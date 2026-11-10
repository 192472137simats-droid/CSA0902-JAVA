# Q1 – Building Text Under Load: String, StringBuilder or StringBuffer?

## Overview
This task investigates performance, memory footprint, and object allocation when building text blocks (e.g. Railway e-tickets) under load using:
1. `String` (with `+` operator concatenation)
2. `StringBuilder` (unsynchronized, mutable character buffer)
3. `StringBuffer` (synchronized, thread-safe character buffer)

## Files
- `TicketBenchmark.java`: Benchmarking harness with 3 contrasting workloads.
- `expected_output.txt`: Measured execution times, intermediate object counts, and memory deltas.

## Compilation and Execution
```bash
javac TicketBenchmark.java
java TicketBenchmark
```

## Summary of Findings
- For small text concatenation (Workload 1), the difference is negligible.
- Under heavy load (Workloads 2 and 3), repeated `+` creates millions of intermediate `String` objects, triggering substantial GC overhead and higher execution latency ($O(n^2)$ total character copies).
- `StringBuilder` is optimal for per-ticket single-threaded generation ($O(n)$ character copies).
- `StringBuffer` is recommended for shared thread-safe logs across multiple booking server threads.
