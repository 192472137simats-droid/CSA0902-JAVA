# Q6 – Priority, Order and Fairness in a Queue

## Overview
This task evaluates queue structures for a Hospital Emergency Triage system where patients possess severity levels from 1 (critical) to 5 (minor). The physician must see the lowest severity number first, with ties broken strictly by earliest arrival.

## Files
- `Triage.java`: Completed "Complete the Code" program fulfilling TODO 1.
- `TriageInvestigation.java`: In-depth investigation showing:
  - Difference between raw heap array iteration (`toString()`) and priority order extraction (`poll()`).
  - Hand-verified 8-patient correctness test.
  - Growth investigation across 1,000, 10,000, and 100,000 queue sizes.
- `expected_output.txt`: Program output matching the question specification.

## How to Compile and Run
```bash
# 1. Main Triage Program
javac Triage.java
java Triage

# 2. Comprehensive Investigation Suite
javac -cp . TriageInvestigation.java
java -cp . TriageInvestigation
```

## Summary of Findings & Deployment Decision
- **Why Plain Queue Fails**: A FIFO `Queue` (`LinkedList` / `ArrayDeque`) cannot prioritize severity without performing an $O(n)$ linear scan or full re-sorting on every admission.
- **Why Direct Printing Fails**: `PriorityQueue` implements a binary min-heap stored in an array. The array is partially ordered according to the heap property ($parent \le children$), not sorted. Sequential iteration prints raw array indices. Only successive `poll()` calls extract elements in true priority order ($O(\log n)$ per removal).
- **Tie-Breaking with Arrival Counter**: `PriorityQueue` is non-stable. Stamping an auto-incrementing `counter` on each patient breaks ties deterministically, guaranteeing FIFO order among identical severities.
- **Starvation Risk**: If critical patients (S1–S3) arrive continuously, a severity-5 patient will wait indefinitely (starvation). A fair-queueing aging mechanism is required to escalate waiting patients.
- **Deployment Decision (5,000 patients/day)**: Fully capable. The heap operations require $O(\log n)$ time. 5,000 admissions and removals take less than 5 milliseconds of total CPU time, easily handling hospital peak loads.
