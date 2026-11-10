# Task B4 – Final Engineering Decision

## Overview
This task evaluates architecture and persistence strategy for a 100x scale expansion serving 12 university campuses (800,000 passes/year) across concurrent ticket counters.

## Files
- `MigrationProgram.java`: Robust data migration utility transferring legacy CSV records into a relational database with transactional batching, error auditing, and `DatabaseMetaData` inspection.
- `expected_output.txt`: Captured migration run summary.

## Compilation and Execution
```bash
javac MigrationProgram.java
java MigrationProgram
```

## Summary of Findings & Production Decision
- **Why Text Files Fail at Scale**: Concurrent writes from multiple campus counters cause file-locking conflicts, corrupted overwrites, and race conditions. Querying requires an $\mathcal{O}(n)$ linear scan through 800,000 lines ($\approx 60\text{ MB}$).
- **Why Serialization Fails at Scale**: Deserializing an 800,000-object graph consumes hundreds of megabytes of RAM on every query, lacks ad-hoc filtering (e.g., query by route number), and breaks schema evolution across software releases.
- **Production Decision -> Shared Relational Database (RDBMS/MySQL)**:
  - **ACID Transactions**: Multiple counters can issue passes simultaneously with row-level locking without file corruption.
  - **Indexing**: B-Tree indices on `pass_id` and `route_no` reduce lookups from $\mathcal{O}(n)$ (hundreds of milliseconds) to $\mathcal{O}(\log n)$ ($< 1\text{ ms}$).
  - **Centralized Data Integrity**: Enforces foreign keys, unique constraints, and automated transaction rollback.
