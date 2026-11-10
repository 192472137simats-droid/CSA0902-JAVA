# Task B3 – Statement vs PreparedStatement vs Batch

## Overview
This task evaluates database ingestion mechanisms across raw `Statement`, individual `PreparedStatement`, and batched `PreparedStatement.addBatch()` with transaction management.

## Files
- `PassLoader.java`: Completed "Complete the Code" implementation fulfilling TODO 1 (parameter setting and 1,000-row batching), TODO 2 (`commit()`), and TODO 3 (`rollback()` on exception).
- `BatchBenchmark.java`: Simulation harness testing 10,000 records under auto-commit vs transactional rollback upon halfway failure.
- `expected_output.txt`: Captured runtime logs confirming network roundtrip reduction and clean 0-row rollback upon failure.

## Compilation and Execution
```bash
javac PassLoader.java BatchBenchmark.java
java BatchBenchmark
```

## Summary of Findings & Key Answers
- **Network Roundtrips**:
  - Individual statements execute 1 network roundtrip per row (10,000 roundtrips for 10,000 rows).
  - Batching in chunks of 1,000 bundles rows into a single network buffer, requiring only 10 roundtrips (a **1,000x network traffic reduction**).
- **Auto-Commit vs Transaction with Rollback**:
  - With auto-commit ON, a failure at row 500 leaves 500 orphan rows committed to the database, leaving the database corrupted and requiring manual recovery.
  - With a single transaction (`setAutoCommit(false)`), calling `con.rollback()` in the `catch` block completely undoes all 500 rows, restoring the database to a clean, consistent state (ACID Atomicity).
- **Engineering Decision**:
  - For nightly import of 100,000 passes: Use **Batched `PreparedStatement` with transactions** in chunks of 1,000–5,000 rows.
  - For a single pass issued at the counter: Use a standard individual **`PreparedStatement`** (batching 1 record provides no benefit).
