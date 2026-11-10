# Q7 – Day-4 Design Challenge: Diagnose, Redesign and Validate

## Overview
This task conducts an architectural post-mortem and redesign of the **Hospital Emergency Triage System (Q6)**. 

### Bottleneck Diagnosis
1. **Starvation Vulnerability**: In standard `PriorityQueue`, when high-priority emergencies (S1–S3) arrive continuously, low-priority patients (S5) remain at the bottom of the binary heap indefinitely (unbounded starvation).
2. **Dynamic Re-keying Limitation**: Java's standard `PriorityQueue` cannot update priorities in-place efficiently without an $O(n)$ linear search and heap re-sift ($O(n \log n)$ across the queue).
3. **Logarithmic Tree Sifting**: Every admission and removal performs $O(\log n)$ comparisons and array element swaps.

### The Redesign: Multi-Level Aging Queue (5 FIFO Buckets + Aging Promotion)
- Employs 5 dedicated `ArrayDeque<Patient>` FIFO queues—one per severity level.
- **Admission**: strictly $O(1)$ amortized push to `buckets[severity - 1].addLast()`.
- **Extraction**: strictly $O(1)$ constant-time lookup across the 5 top pointers.
- **Fair Aging**: Periodically checks patient wait times and promotes overdue patients to the next higher severity bucket in $O(1)$ per patient, eliminating starvation completely.

## Files
- `TriageRedesign.java`: Side-by-side comparative simulation and benchmark testing starvation avoidance and throughput at 50,000 patients.
- `expected_output.txt`: Measured experimental evidence.

## Compilation and Execution
```bash
javac TriageRedesign.java
java TriageRedesign
```

## Summary of Results
- **Starvation Eliminated**: In Experiment 1, a minor patient who is completely ignored and starved by `PriorityQueue` is aged and successfully treated in the Redesign.
- **5x Throughput Improvement**: At 50,000 operations, admission improved from 10.9 ms to 2.09 ms, and extraction improved from 25.1 ms to 4.61 ms.
