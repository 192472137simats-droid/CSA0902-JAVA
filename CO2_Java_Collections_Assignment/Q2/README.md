# Q2 – When Is ArrayList an Appropriate Engineering Choice?

## Overview
This task compares `ArrayList`, `LinkedList`, and `ArrayDeque` for a Music Playlist Editor across multiple playlist sizes (1,000, 10,000, and 50,000 songs).

## Files
- `PlaylistBenchmark.java`: Comprehensive benchmarking suite measuring operations: Add at End, Insert/Remove at Front, Insert/Remove in Middle, Get k-th element, and Iteration.
- `expected_output.txt`: Captured experimental results and execution times.

## Compilation and Execution
```bash
javac PlaylistBenchmark.java
java PlaylistBenchmark
```

## Summary of Findings & Operating Boundaries
- **When ArrayList is recommended:**
  - Fast $O(1)$ random access (`get(k)`), critical for jumping directly to any song.
  - Appending songs at the end ($O(1)$ amortized).
  - Memory efficiency: stores references in a contiguous array without object wrapper overhead.
  - High cache locality during sequential playback/iteration.
- **When ArrayList should NOT be recommended:**
  - High-frequency insertions or deletions at index 0 or near the front, which trigger $O(n)$ memory shifts.
- **Changed Condition ("Up Next" Queue):**
  - For double-ended queue operations (frequent additions at ends, removals from front), **`ArrayDeque`** is the superior engineering choice over `LinkedList`. It provides $O(1)$ amortized operations using a resizable ring buffer without allocating separate 24-byte `Node` objects on the heap.
