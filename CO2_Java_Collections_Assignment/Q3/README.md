# Q3 – Engineer the hashCode–equals Contract

## Overview
This task investigates the fundamental contract between `equals()` and `hashCode()` in Java collections using an Event Attendance De-duplication scenario (`HashSet<Student>`).

## Files
- `Attendance.java`: Completed "Complete the Code" program fulfilling TODO 1.
- `AttendanceInvestigation.java`: Deep-dive demonstration testing all 3 required scenarios (same reference, distinct instances with same key, and key mutation after insertion).
- `expected_output.txt`: Output of the completed program matching the question specification.

## How to Compile and Run
```bash
# 1. Main Completed Code
javac Attendance.java
java Attendance

# 2. Challenge Investigation Scenarios
javac AttendanceInvestigation.java
java AttendanceInvestigation
```

## Summary of Findings
- **The Contract**: If two objects are equal according to `equals(Object)`, they MUST produce the identical integer result from `hashCode()`.
- **The Bug**: When `hashCode()` was omitted, `Object.hashCode()` assigned unique identity hash codes based on memory address. As a result, two separate `Student` instances with the same `regNo` were placed into different hash buckets, causing `HashSet.contains()` / `add()` to never invoke `equals()`, resulting in false duplicate entries (size 3 instead of 2).
- **The Fix**: Overriding `hashCode()` to delegate to `regNo.hashCode()` guarantees consistent bucket placement, restoring true $O(1)$ de-duplication.
- **Immutability Warning**: Keys placed in hash-based collections must be immutable (e.g. `final` fields). Mutating `regNo` after insertion creates a "phantom" element that can neither be found via `contains()` nor removed via `remove()`.
