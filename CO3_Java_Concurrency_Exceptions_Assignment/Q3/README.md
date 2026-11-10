# Q3 – Resource Safety, Debugging and Robust Code

## Overview
This task investigates file resource leaks causing `"Too many open files"` OS exceptions in a Daily Sales Report Generator, comparing manual `try-catch-finally` closing against `try-with-resources`.

## Files
- `SalesReportGenerator.java`: Implementation benchmarking manual closing vs automatic suppression-aware closing under injected file corruption.
- `expected_output.txt`: Captured runtime logs confirming open handle retention in manual code vs deterministic auto-closure in `try-with-resources`.

## Compilation and Execution
```bash
javac SalesReportGenerator.java
java SalesReportGenerator
```

## Summary of Findings & Key Questions
- **Why Manual Closing Leaks Resources**: In manual loops, if an exception is thrown mid-loop (e.g. during `readLine()` or processing), control jumps immediately to the `catch`/`finally` block, skipping the inline `reader.close()` call. If another exception occurs inside `finally`, previous references are orphaned and remain held open by the OS.
- **Suppressed Exceptions**: When an exception occurs inside the `try` block, and another exception occurs while closing the resource, `try-with-resources` preserves the primary exception and attaches the close exception as a *suppressed* exception accessible via `e.getSuppressed()`. In manual code, the close exception completely masks/swallows the original business error!
- **When `try-with-resources` is Not Sufficient**: It guarantees OS handle closing, but does NOT guarantee transactional data integrity. If a process fails halfway through writing a 10,000-line report, the file will be closed safely, but the report will be corrupted/incomplete. A robust system requires writing to a temporary file first and performing an atomic rename/move upon complete success.
