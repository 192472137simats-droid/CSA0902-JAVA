# Q5 – Derive a Generic Ranking Component from a Principle

## Overview
This task designs a reusable, type-safe generic ranking component (`Leaderboard<T>`) based on the fundamental consistency principle:
> *"A ranking is correct only if the comparison used is consistent: if A ranks above B and B ranks above C, then A must rank above C, and equal items must compare as equal."*

## Files
- `LeaderboardDemo.java`: Completed "Complete the Code" program fulfilling TODO 1 and TODO 2.
- `ComparatorAnalysis.java`: Generic reuse demonstration for quiz players and mathematical investigation of subtraction-based comparator overflow bugs.
- `expected_output.txt`: Program output matching the question specification.

## How to Compile and Run
```bash
# 1. Main Leaderboard Demo
javac LeaderboardDemo.java
java LeaderboardDemo

# 2. Generics & Bug Analysis
javac -cp . ComparatorAnalysis.java
java -cp . ComparatorAnalysis
```

## Summary of Findings
- **Why Bisons is Ranked Above Panthers**:
  - Eagles has 9 points, goal diff 6 -> ranks 1st.
  - Bisons and Panthers both have 9 points and 4 goal diff (tied on the first two criteria).
  - The third comparator rule breaks ties by team name alphabetically (A–Z). Since `"Bisons".compareTo("Panthers") < 0`, Bisons precedes Panthers.
- **Why Separate `Comparator` is Superior to `Comparable`**:
  - `Comparable<T>` enforces a single hardcoded "natural" ordering inside the class definition, violating Single Responsibility and Open-Closed principles.
  - Separate `Comparator<T>` instances allow flexible ranking strategies (e.g. Fair Play ranking, Head-to-Head ranking) without modifying the entity class.
- **Generic Bounded Wildcards**:
  - `Comparator<? super T>` allows `Leaderboard<T>` to accept comparators defined on superclasses of `T`, maximizing reusability across inheritance hierarchies.
- **The Subtraction Hazard**:
  - Comparing numeric fields via subtraction `(b - a)` can produce integer overflow/underflow when comparing large positive numbers with negative numbers (e.g. $2147483642 - (-100)$ overflows to a negative integer). Always use `Integer.compare(a, b)` or `Comparator.comparingInt()`.
