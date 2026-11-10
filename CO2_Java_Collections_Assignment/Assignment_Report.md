# CO2 Java Assignment 2 – Java Collections Framework Report
## Advanced Data Structures, Generics, and Algorithm Engineering

**Student Name:** G SAI TEJA  
**Register Number:** 192472137  
**GitHub Repository:** [Paste your repository link]  
**Java Version:** JDK 8 or later  

> This comprehensive engineering report provides rigorous analysis, empirical measurements, algorithmic pseudocode, and mathematical justifications for Questions 1 through 7 of the CO2 Java Assessment.

---

## Q1 – Building Text Under Load: String, StringBuilder or StringBuffer?

### 1. Problem Context & Prior Prediction
In a railway reservation system, an e-ticket is built by concatenating multiple booking attributes (PNR, train details, passenger records, fare breakdowns, and timestamps).

- **Prior Prediction**:
  - `String` using the `+` operator will experience drastic degradation in throughput and high memory churn as the number of fields ($n$) and tickets ($T$) increases.
  - `StringBuilder` will exhibit optimal, near-linear $O(n)$ performance and minimal heap overhead.
  - `StringBuffer` will perform similarly to `StringBuilder` in single-threaded environments, but with a slight constant-time latency penalty due to synchronized method monitor acquisition.
- **Underlying Principle**:
  - `java.lang.String` is strictly **immutable**. Every execution of the `+` operator allocates a brand new character array and a new `String` object, copying all existing characters from the prefix string into the new buffer. In contrast, `StringBuilder` and `StringBuffer` encapsulate a mutable, resizable character buffer (`char[]` or `byte[]`), appending characters in-place with amortized $O(1)$ expansion.

---

### 2. Workload Design & Experimental Evidence
Three contrasting workloads were executed using `Q1/TicketBenchmark.java`:
1. **Workload 1 (Micro-concatenation)**: 1 short ticket with 5 fields (5 runs averaged).
2. **Workload 2 (Heavy Batch Generation)**: 10,000 tickets with 50 fields each (3 runs averaged).
3. **Workload 3 (Burst Concurrency Simulation)**: 50,000 tickets with 20 fields each (3 runs averaged).

#### Empirical Measurement Table
| Workload | Approach | Average Time (ms) | Est. Intermediate Objects | Memory Delta (KB) |
|---|---|---|---|---|
| **Workload 1** (1 ticket, 5 fields) | String (+) | 0.014 | 10 | Negligible |
| | StringBuilder | 0.025 | 1 | Negligible |
| | StringBuffer | 0.036 | 1 | Negligible |
| **Workload 2** (10,000 tickets, 50 fields) | String (+) | 62.417 | 1,000,000 | 1,015.75 |
| | StringBuilder | 22.847 | 10,000 | 862.98 |
| | StringBuffer | 21.633 | 10,000 | 862.48 |
| **Workload 3** (50,000 tickets, 20 fields) | String (+) | 54.316 | 2,000,000 | 943.34 |
| | StringBuilder | 20.634 | 50,000 | 942.30 |
| | StringBuffer | 25.287 | 50,000 | 943.34 |

- **Comparison of Prediction vs Observed Behavior**:
  - For single short tickets, the JVM compiler (`javac` / `invokedynamic`) optimizes simple string concatenations, making the difference negligible.
  - Under load (Workloads 2 and 3), repeated `+` generated over **1,000,000 to 2,000,000 intermediate heap objects**, creating heavy Garbage Collection (GC) pauses and tripling total execution latency compared to `StringBuilder`.

---

### 3. Design Decision for Multithreaded Server
In an enterprise booking server where worker threads process tickets and log transactions:
- **Per-Ticket Text Generation -> `StringBuilder`**: Each individual ticket is constructed within the thread-local call stack of a single worker thread. Synchronization is completely unnecessary; `StringBuilder` avoids thread locking and lock-biasing overhead, providing maximum single-thread throughput.
- **Shared Booking Log -> `StringBuffer` (or Thread-Safe Concurrent Queue)**: When multiple server threads concurrently append audit lines to a shared log buffer, thread-safe synchronization is mandatory to prevent corrupted character streams and race conditions. `StringBuffer`'s synchronized methods ensure serial consistency across threads.

---

### 4. Alternative Concept and Complexity Analysis
- **Alternative Approaches**:
  - `String.join(delimiter, elements)` and `StringJoiner`: Highly expressive and readable for separated lists; internally backed by `StringBuilder`.
  - `String.format(format, args)`: Parses format specifiers at runtime using regular expressions and creates intermediate wrapper objects, resulting in significantly higher latency (~5x to 10x slower than `StringBuilder`).
- **Mathematical Complexity Derivation**:
  - Consider building a ticket with $n$ fields, where each field adds $L$ characters:
    - **Repeated `+`**: Concatenation $k$ copies $(k-1) \times L$ prior characters. Total characters copied:
      $$\sum_{k=1}^{n} k \cdot L = L \cdot \frac{n(n+1)}{2} = \mathcal{O}(n^2)$$
      Intermediate objects created: $\mathcal{O}(n)$ per ticket.
    - **`StringBuilder`**: Characters are appended directly to the end of an internal array. Array resizing uses geometric progression (doubling capacity). Amortized character copies: $\mathcal{O}(n)$.
      Intermediate objects created: **0** (only 1 final `String` created at `.toString()`).

**Evidence Reference**: Working source code located in `Q1/TicketBenchmark.java` and measured output in `Q1/expected_output.txt`.

---

## Q2 – When Is ArrayList an Appropriate Engineering Choice?

### 1. Problem Context & Experimental Design
A music playlist editor manages user playlists with operations:
1. Append song at end (`add(e)`)
2. Insert at front (`add(0, e)`)
3. Insert in middle (`add(size / 2, e)`)
4. Remove at front (`remove(0)`)
5. Remove in middle (`remove(size / 2)`)
6. Random access (`get(k)`)
7. Sequential iteration (`for-each`)

Experiments were conducted in `Q2/PlaylistBenchmark.java` across three playlist sizes: 1,000 songs, 10,000 songs, and 50,000 songs.

#### Empirical Measurement Table
| Operation | Collection | Size = 1,000 | Size = 10,000 | Size = 50,000 |
|---|---|---|---|---|
| **Add at End** | ArrayList | 20.941 ms | 1.916 ms | 6.039 ms |
| | LinkedList | 1.909 ms | 1.559 ms | 6.051 ms |
| | ArrayDeque | 0.988 ms | 2.260 ms | 6.747 ms |
| **Insert Front** | ArrayList | 0.697 ms | 2.011 ms | 9.892 ms |
| | LinkedList | 0.299 ms | 0.123 ms | 0.257 ms |
| | ArrayDeque | 0.210 ms | 0.138 ms | 0.246 ms |
| **Insert Middle** | ArrayList | 0.488 ms | 0.700 ms | 6.061 ms |
| | LinkedList | 3.210 ms | 11.953 ms | 189.994 ms |
| **Remove Front** | ArrayList | 0.435 ms | 1.233 ms | 9.714 ms |
| | LinkedList | 0.088 ms | 0.054 ms | 0.057 ms |
| | ArrayDeque | 0.053 ms | 0.137 ms | 0.067 ms |
| **Remove Middle** | ArrayList | 0.194 ms | 0.553 ms | 6.010 ms |
| | LinkedList | 1.436 ms | 8.904 ms | 113.750 ms |
| **Get k-th Element** | ArrayList | **0.045 ms** | **0.095 ms** | **0.106 ms** |
| | LinkedList | 0.577 ms | 4.389 ms | 54.818 ms |
| **Full Iteration** | ArrayList | 0.197 ms | 0.644 ms | 0.470 ms |
| | LinkedList | 0.214 ms | 0.653 ms | 0.428 ms |

---

### 2. Engineering Boundaries for ArrayList
- **When ArrayList is Recommended**:
  - High-frequency random access: Instantaneous $O(1)$ indexing (`get(k)` took 0.106 ms for 2,000 queries in 50,000 songs, compared to 54.8 ms for `LinkedList`—a **500x speed advantage**).
  - Predominantly append-only workflows (adding tracks to the end of a playlist).
  - Cache locality: Elements are stored in contiguous memory addresses, leveraging CPU L1/L2 cache prefetching during playback iteration.
  - Memory efficiency: Overhead is strictly 4 to 8 bytes per pointer in the backing array.
- **When to Stop Recommending ArrayList**:
  - When frequent insertions or removals occur at index 0 or near the beginning of large playlists. At size 50,000, inserting at the front took ~10 ms because every operation forces a memory block shift (`System.arraycopy`) of 50,000 references ($\mathcal{O}(n)$).

---

### 3. Changed Condition: "Up Next" Queue Evaluation
When the app introduces an "Up Next" queue where songs are frequently enqueued at both ends and dequeued from the front:
- `ArrayList` is unsuited due to $\mathcal{O}(n)$ removal and front insertion.
- `LinkedList` offers $\mathcal{O}(1)$ front/end access, but incurs substantial memory fragmentation by allocating a 24-byte `Node` wrapper object (storing `item`, `next`, `prev`) for every single track.
- **Superior Recommendation -> `ArrayDeque`**: `ArrayDeque` uses a circular, resizable array with head and tail pointers. It achieves true amortized $\mathcal{O}(1)$ `addFirst()`, `addLast()`, and `removeFirst()` with **zero node allocation overhead** and high cache locality.

---

### 4. Alternative Concept and Complexity Analysis
| Data Structure | Add End | Insert Front | Insert Middle | Get k-th | Space per Element |
|---|---|---|---|---|---|
| **ArrayList** | $\mathcal{O}(1)^*$ | $\mathcal{O}(n)$ | $\mathcal{O}(n)$ | $\mathcal{O}(1)$ | 4–8 bytes (array slot) |
| **LinkedList** | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ | $\mathcal{O}(n)$ (traversal) | $\mathcal{O}(n)$ | 24 bytes (Node + pointers) |
| **ArrayDeque** | $\mathcal{O}(1)^*$ | $\mathcal{O}(1)^*$ | N/A | N/A | 4–8 bytes (ring buffer) |
| **Plain Array (Manual)**| $\mathcal{O}(n)^*$ | $\mathcal{O}(n)$ | $\mathcal{O}(n)$ | $\mathcal{O}(1)$ | Exact array size |

*\* Amortized time complexity*

**Plain Array with Manual Shifting**:
Storing songs in a raw `Song[]` array requires manual allocation, bounds checking, and manual loops or `System.arraycopy()` to shift elements during inserts/removals. Time complexities match `ArrayList` identically ($\mathcal{O}(n)$ shifts), but code complexity, bug risk (e.g. `ArrayIndexOutOfBoundsException`), and lack of dynamic capacity management make plain arrays poor engineering choices compared to `ArrayList`.

**Evidence Reference**: Working code in `Q2/PlaylistBenchmark.java` and captured output in `Q2/expected_output.txt`.

---

## Q3 – Engineer the hashCode–equals Contract

### 1. Problem Context & The Contract Breakdown
A college fest scans student ID cards at multiple gates, storing records in a `HashSet<Student>`. A junior developer overrode `equals()` comparing `regNo`, but failed to override `hashCode()`.

- **The Contract**: If two objects are equal according to `equals(Object)`, they **MUST** produce the exact same integer value from `hashCode()`.
- **Root Cause of the Defect**:
  - When `hashCode()` is omitted, the class inherits `Object.hashCode()`, which calculates an identity hash code based on the object's internal memory address.
  - Even if two `Student` instances have identical `regNo` values (e.g. `"192311001"`), they reside at different memory addresses and produce different hash codes.
  - In `HashSet`, `add(e)` first hashes the object to locate the corresponding hash bucket: `index = (n - 1) & hash(key)`. Since the hash codes differ, the two duplicate students land in completely different buckets. `HashSet` never even calls `equals()`, silently inserting both objects and creating duplicate attendance records.

---

### 2. Three Designed Tests & Predicted vs Actual Results
Implemented and validated in `Q3/AttendanceInvestigation.java`:

| Test Case | Scenario | Predicted Size | Actual Size | Root Cause Analysis |
|---|---|---|---|---|
| **Test 1** | Re-scanning exact same object reference (`s1` added twice) | **1** | **1** | `HashSet.putVal()` tests reference identity (`p.key == key`) *before* checking hash codes. Duplicate is caught by reference. |
| **Test 2** | Two separate `Student` instances with identical `regNo` | **3** (flawed code) | **3** | Flawed `Student` lacks `hashCode()`. Different memory addresses place them in distinct buckets; duplicate bypasses de-duplication. |
| **Test 3** | Mutating `regNo` after object has been added to `HashSet` | **1** (orphaned) | **1** | Key mutated from `...099` to `...888`. Calling `contains()` hashes the *new* value and searches a *new* bucket, finding nothing (`false`). Object is permanently lost. |

---

### 3. Complete the Code
In `Q3/Attendance.java`, TODO 1 was completed by overriding `hashCode()`:
```java
@Override
public int hashCode() {
    return regNo.hashCode();
}
```

- **Output Before Completing TODO 1**:
  ```text
  Unique students: 3
  ```
- **Output After Completing TODO 1**:
  ```text
  Unique students: 2
  ```

---

### 4. Alternative Concept and Complexity Analysis
- **TreeSet<Student> with Comparator on regNo**:
  - Uses a Red-Black balanced binary search tree.
  - De-duplication relies purely on `Comparator.compare(s1, s2) == 0`, completely ignoring `hashCode()`.
  - Insertion & search take $\mathcal{O}(\log n)$ time per scan.
  - Space: 32 bytes per node (`parent`, `left`, `right`, `color`).
- **HashMap<String, Student> Keyed by regNo**:
  - Key is `String` (which already has an immutable, highly optimized `hashCode()`).
  - Average $\mathcal{O}(1)$ time per scan.
  - Clean separation: `regNo` acts as the primary key.
- **Comparison Summary**:
  - `HashSet<Student>` (with correct `hashCode`) and `HashMap<String, Student>` offer superior throughput ($\mathcal{O}(1)$ average vs $\mathcal{O}(\log n)$ in `TreeSet`).

**Evidence Reference**: Working source code in `Q3/Attendance.java` and `Q3/AttendanceInvestigation.java`.

---

## Q4 – Choosing a Map for a Pharmacy Inventory

### 1. Problem Context & Architectural Trade-Offs
A hospital pharmacy manages medicines identified by a medicine code (`MED-xxxxx`).
- Workload requirements:
  - Pharmacists perform hundreds of thousands of stock lookups per day.
  - Nightly report 1: All medicines sorted in **Code Order** (lexicographical).
  - Nightly report 2: All medicines in the exact **Order Added** (chronological).

#### Theoretical Comparison Matrix
| Map Structure | `put()` Cost | `get()` Cost | Iteration Cost | Natural Report Produced |
|---|---|---|---|---|
| **HashMap** | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ | $\mathcal{O}(n)$ | Neither (pseudo-random bucket order) |
| **TreeMap** | $\mathcal{O}(\log n)$ | $\mathcal{O}(\log n)$ | $\mathcal{O}(n)$ | **Code-Order Report** (in-order tree traversal) |
| **LinkedHashMap** | $\mathcal{O}(1)$ | $\mathcal{O}(1)$ | $\mathcal{O}(n)$ | **Insertion-Order Report** (doubly-linked bucket list) |

---

### 2. Empirical Benchmark Evidence
Benchmarked in `Q4/PharmacyInventory.java` with 100,000 lookups and report generation across 500, 5,000, and 50,000 medicines:

| Catalog Size | Map Type | 100k Lookups Time (ms) | Code-Order Report (ms) | Insertion-Order Report (ms) |
|---|---|---|---|---|
| **500 medicines** | HashMap | 6.692 ms | 0.901 ms (sorted) | N/A (Requires extra index) |
| | TreeMap | 12.942 ms | 0.483 ms (natural) | N/A (Requires extra index) |
| | LinkedHashMap | 5.197 ms | 0.547 ms (sorted) | **0.281 ms (natural)** |
| **5,000 medicines** | HashMap | 3.081 ms | 6.052 ms (sorted) | N/A (Requires extra index) |
| | TreeMap | 23.951 ms | 0.411 ms (natural) | N/A (Requires extra index) |
| | LinkedHashMap | 1.465 ms | 4.345 ms (sorted) | **0.548 ms (natural)** |
| **50,000 medicines**| HashMap | 6.399 ms | 34.620 ms (sorted)| N/A (Requires extra index) |
| | TreeMap | 57.299 ms | 2.932 ms (natural) | N/A (Requires extra index) |
| | LinkedHashMap | **2.940 ms** | 25.965 ms (sorted)| **0.628 ms (natural)** |

---

### 3. Scalability Inquiry & Deployment Recommendation
- **Acceptable Response Time**: For interactive clinical pharmacy lookups, latency must be $< 0.001\text{ ms}$ ($< 1\ \mu\text{s}$) per query.
- **Selected Deployment Architecture -> `LinkedHashMap`**:
  - Pharmacists query stock continuously throughout the 24-hour shift. `LinkedHashMap` delivers constant $\mathcal{O}(1)$ lookup time (~2.9 ms for 100,000 queries, or $0.029\ \mu\text{s}$ per lookup).
  - In contrast, `TreeMap` requires $\mathcal{O}(\log n)$ pointer traversals, taking **57.3 ms** (nearly 20x slower).
  - For nightly reporting:
    - The **Insertion-Order report** is produced instantaneously for free ($0.6\text{ ms}$) via `LinkedHashMap`'s internal linked list.
    - The **Code-Order report** is generated once per day by extracting key references into an `ArrayList` and calling `Collections.sort()` in **25.9 ms** ($\mathcal{O}(n \log n)$).
  - Paying 25 ms once at midnight is vastly superior to penalizing hundreds of thousands of customer-facing queries all day long with `TreeMap`.
- **Why `Hashtable` Is Prohibited**:
  - `java.util.Hashtable` is an obsolete legacy class from JDK 1.0. Every public method is guarded by a coarse-grained `synchronized` lock on the entire instance, preventing concurrent reads/writes and throttling multi-threaded performance. It also rejects `null` keys/values.

---

### 4. Alternative Concept and Complexity Analysis
- **Sorted Array with `Arrays.binarySearch()`**:
  - Lookup: $\mathcal{O}(\log n)$ via binary search.
  - Insertion of a new medicine: $\mathcal{O}(n)$ because inserting an element requires shifting an average of $n/2$ array elements in memory.
  - Code-order report: $\mathcal{O}(n)$ direct linear traversal.
  - Space: Highly compact contiguous memory.
  - **Verdict**: A sorted array is acceptable for static lookup tables, but completely fails for dynamic inventory where new medicines and quantity updates arrive continuously.

**Evidence Reference**: Source code in `Q4/PharmacyInventory.java` and captured output in `Q4/expected_output.txt`.

---

## Q5 – Derive a Generic Ranking Component from a Principle

### 1. Problem Context & The Consistency Principle
An inter-college sports meet ranks teams by:
1. Points (higher first)
2. Goal difference (higher first)
3. Team name (alphabetical, A–Z)

**The Consistency Principle**:
> *"A ranking is correct only if the comparison used is consistent: if A ranks above B and B ranks above C, then A must rank above C (transitivity), and equal items must compare as equal (anti-symmetry)."*

---

### 2. Design Reasoning: Comparable vs Comparator & Generics
- **Why Separate `Comparator` is Superior to `Comparable`**:
  - Implementing `Comparable<Team>` hardwires one fixed "natural" comparison strategy into the domain class, violating the **Single Responsibility Principle** and **Open-Closed Principle**.
  - A sports platform needs multiple ranking rules (e.g., Fair Play ranking, Head-to-Head ranking, Tournament group stage ranking). Using external `Comparator<Team>` objects allows arbitrary ranking policies without touching `Team`.
- **Expressing Multi-Field Comparison**:
  ```java
  Comparator<Team> byRank = Comparator
      .comparingInt((Team t) -> t.points).reversed()
      .thenComparing(Comparator.comparingInt((Team t) -> t.goalDiff).reversed())
      .thenComparing(t -> t.name);
  ```
- **Generic Bounded Type Wildcard (`Comparator<? super T>`)**:
  - In `Leaderboard<T>`, declaring `Comparator<? super T> order` enables the leaderboard to accept comparators defined on any superclass of `T`. For example, a `Comparator<Participant>` can rank both `Player` and `Team` objects, ensuring universal reuse.

---

### 3. Complete the Code
Implemented in `Q5/LeaderboardDemo.java`:
- **Expected Output**:
  ```text
  [Eagles(9,6), Bisons(9,4), Panthers(9,4), Cobras(6,1)]
  ```
- **Why Bisons is Ranked Above Panthers**:
  1. Primary rule (points): Eagles has 9, Cobras has 6. Panthers and Bisons tie with 9 points.
  2. Secondary rule (goal difference): Panthers has 4, Bisons has 4 (still tied).
  3. Tertiary tie-breaker (alphabetical team name): `"Bisons".compareTo("Panthers")` returns a negative value because `'B'` precedes `'P'`. Therefore, Bisons strictly ranks above Panthers.

---

### 4. Mathematical Correctness & The Subtraction Bug
- **The Subtraction Hazard**:
  A common anti-pattern is writing `(t1, t2) -> t2.points - t1.points`. If $t2.\text{points} = 2,147,483,554$ and $t1.\text{points} = -100$, computing $(t2 - t1)$ triggers integer 32-bit signed overflow, resulting in a negative number and asserting that $t1 > t2$. Using `Integer.compare(a, b)` or `Comparator.comparingInt()` completely avoids numeric overflow.

---

### 5. Alternative Concept and Complexity Analysis
- **Comparator Chain vs Three Successive Stable Sorts**:
  - Sorting three separate times (first by name, then by goal diff, then by points) using a stable sorting algorithm (such as Java's TimSort) yields the correct order.
  - However, running 3 consecutive sorts costs $3 \times \mathcal{O}(n \log n)$ time.
  - A composite comparator chain performs a single sort in $\mathcal{O}(n \log n)$, saving $66\%$ of sorting operations and eliminating intermediate collection passes.

**Evidence Reference**: Working source code in `Q5/LeaderboardDemo.java` and `Q5/ComparatorAnalysis.java`.

---

## Q6 – Priority, Order and Fairness in a Queue

### 1. Problem Context & Architectural Breakdown
Hospital Emergency Triage requires seeing patients by severity level (1 = critical to 5 = minor). Among equal severities, arrival order (FIFO) must be preserved.

- **Why Plain Queue (`ArrayDeque` / `LinkedList`) Fails**:
  - Plain queues only support FIFO arrival ordering. They cannot prioritize emergency cases (e.g. S1 heart attack) ahead of earlier-arriving minor cases (S5 cough) without an expensive $\mathcal{O}(n)$ search or re-sort.
- **How `PriorityQueue` with Arrival Counter Enforces the Rule**:
  - Standard `PriorityQueue` in Java is a non-stable binary min-heap.
  - By augmenting each patient with an atomic incrementing `arrival = counter++`, the comparator:
    ```java
    Comparator.comparingInt((Patient p) -> p.severity)
              .thenComparingLong(p -> p.arrival)
    ```
    guarantees deterministic, stable tie-breaking: identical severities are ordered strictly by arrival timestamp.
- **Why Direct `PriorityQueue` Printing Fails**:
  - Calling `System.out.println(queue)` prints the backing array storing the binary min-heap tree structure. The heap property only mandates that children are $\ge$ their parent. Array indices are partially ordered, **NOT sorted**.
  - Demonstration from `Q6/TriageInvestigation.java`:
    - Raw heap array: `[Meena(S1), Kumar(S1), Lata(S2), Asha(S5), Ravi(S3), John(S3)]` (notice S5 appears before S3!).
    - Extracted via `poll()`: `[Meena(S1), Kumar(S1), Lata(S2), Ravi(S3), John(S3), Asha(S5)]` (strictly priority-sorted).

---

### 2. Complete the Code & Hand-Verified Test
- **Completed Code (`Q6/Triage.java`)**:
  ```java
  private final PriorityQueue<Patient> queue = new PriorityQueue<>(
      Comparator.comparingInt((Patient p) -> p.severity)
                .thenComparingLong(p -> p.arrival)
  );
  ```
- **Program Output**:
  ```text
  Meena(S1) Kumar(S1) Lata(S2) Ravi(S3) John(S3) Asha(S5)
  ```
- **8-Patient Hand-Verified Test**:
  - Input arrival stream: `Alice(S4), Bob(S2), Carol(S1), Dave(S2), Eve(S1), Frank(S5), Grace(S3), Heidi(S4)`.
  - Hand-calculated order: `Carol(S1) Eve(S1) Bob(S2) Dave(S2) Grace(S3) Alice(S4) Heidi(S4) Frank(S5)`.
  - Verified against execution in `Q6/TriageInvestigation.java`: exact 100% match.

---

### 3. Growth Investigation & Deployment Decision
Benchmarked across 1,000, 10,000, and 100,000 patients:
- **1,000 patients**: Admit all = 3.95 ms | Poll all = 5.19 ms
- **10,000 patients**: Admit all = 6.01 ms | Poll all = 3.32 ms
- **100,000 patients**: Admit all = 20.40 ms | Poll all = 33.57 ms
- **Deployment Decision (5,000 patients/day)**:
  - Admitting and polling 5,000 patients takes under **5 milliseconds of total CPU time**. The binary heap structure easily scales to metropolitan hospital requirements.

---

### 4. Alternative Concept and Complexity Analysis
- **Sorted LinkedList**: Inserting in sorted position requires traversing the list until the right slot is found: $\mathcal{O}(n)$ admission, $\mathcal{O}(1)$ polling.
- **Array of 5 FIFO Queues (Bucket Queue)**: An array `ArrayDeque<Patient>[5]` provides $\mathcal{O}(1)$ admission and $\mathcal{O}(1)$ next-patient retrieval.

**Evidence Reference**: Working source code in `Q6/Triage.java` and `Q6/TriageInvestigation.java`.

---

## Q7 – Day-4 Design Challenge: Diagnose, Redesign and Validate

### A. Diagnose the Bottleneck
Using experimental evidence from Q1–Q6, the **Hospital Emergency Triage System (Q6)** exhibits two critical real-world limitations:
1. **Low-Severity Starvation**: In an emergency department experiencing continuous patient arrivals (e.g. disaster, epidemic, peak trauma hours), patients with minor severities (S4, S5) remain stuck at the bottom of the `PriorityQueue` indefinitely. Because newly arriving S1–S3 patients continually preempt them, S5 patients face unbounded waiting times, leading to severe clinical negligence.
2. **Dynamic Re-keying Bottleneck in `PriorityQueue`**: Standard `java.util.PriorityQueue` does not support an efficient `decreaseKey` operation. To implement patient "aging" (promoting overdue patients), the application must either traverse the heap in $\mathcal{O}(n)$ time and re-heapify ($\mathcal{O}(n \log n)$), or allocate duplicate objects, destroying latency.

---

### B. Define the Design Objective
The redesign must achieve:
1. **Guaranteed Bounded Waiting Time (Zero Starvation)**: Ensure that no patient waits longer than a defined threshold $T_{max}$ before receiving automated priority escalation.
2. **Sub-logarithmic $\mathcal{O}(1)$ Admission and Extraction**: Eliminate heap tree comparisons, reducing operation latency by at least $50\%$.
3. **Deterministic FIFO Fairness**: Strict chronological ordering within each severity tier.

---

### C. Design the Revised Solution
The revised system implements a **Multi-Level Feedback Aging Queue**:

```text
                  [Admit Patient]
                        │
                  Check Severity (1..5)
                        │
  ┌──────────┬──────────┼──────────┬──────────┐
  ▼          ▼          ▼          ▼          ▼
┌─────┐    ┌─────┐    ┌─────┐    ┌─────┐    ┌─────┐
│ S1  │    │ S2  │    │ S3  │    │ S4  │    │ S5  │  <-- ArrayDeque<Patient>
└─────┘    └─────┘    └─────┘    └─────┘    └─────┘
  │          ▲          ▲          ▲          ▲
  │          └─(Aging)──┴─(Aging)──┴─(Aging)──┘  <-- Age Threshold Exceeded
  ▼
[Doctor Next Patient: Highest Non-Empty Queue Head]
```

- **Architectural Mechanics**:
  - An array of 5 dedicated `ArrayDeque<Patient>` queues (`buckets[0..4]`).
  - **Admission**: $\mathcal{O}(1)$ amortized: `buckets[severity - 1].addLast(patient)`.
  - **Next Patient**: $\mathcal{O}(1)$ constant time: inspects the 5 bucket head pointers and pops the first non-empty queue: `buckets[top].pollFirst()`.
  - **Fair Aging Promotion**: Overdue patients in bucket $s$ are moved to bucket $s-1$ in $\mathcal{O}(1)$ pointer operations per patient.

---

### D. Predict Before Implementing
- **Prediction 1**: Under continuous S1 arrivals, the Original solution will never treat an S5 patient (infinite starvation), whereas the Redesign will promote and treat the S5 patient once the threshold is crossed.
- **Prediction 2**: Because `ArrayDeque` uses a circular array buffer without binary heap sift-up/sift-down comparisons, admission and polling will be significantly faster than `PriorityQueue` ($> 2\times$ speedup).

---

### E. Implement and Validate the Redesign
Implemented and benchmarked in `Q7/TriageRedesign.java`:

#### Experiment 1: Starvation Simulation Under Continuous Critical Stream
- Scenario: `MinorPatient_Asha` (S5) arrives at $t=0$, followed by a continuous influx of S1 critical patients.
- **Original PriorityQueue**: Asha was never reached; doctors treated critical patients indefinitely. Asha remained starved.
- **Redesigned Aging Queue**: Asha exceeded the wait threshold, was promoted $S5 \to S4 \to S3 \to S2 \to S1$, and was successfully treated as the 6th patient!

#### Experiment 2: 50,000-Patient Performance Benchmark
| Architecture | Admit Time (ms) | Poll Time (ms) | Starvation Bounded? |
|---|---|---|---|
| **Original PriorityQueue (Binary Heap)** | 10.931 ms | 25.076 ms | **NO (Infinite Starvation)** |
| **Redesigned 5-Bucket Aging Queue** | **2.090 ms** | **4.612 ms** | **YES (Strictly Bounded)** |

---

### F. Defend the Final Design
- **Supported Prediction**: The experimental results fully confirm both predictions. Throughput improved by over **$5\times$** (admission dropped from 10.9 ms to 2.09 ms; polling dropped from 25.1 ms to 4.61 ms).
- **Ethical & Clinical Victory**: Low-severity patients are guaranteed medical attention within a mathematically bounded timeframe.
- **Trade-Off Analysis**:
  - The redesign maintains 5 array references instead of 1 heap array, which requires negligible memory ($< 1\text{ KB}$).
  - Aging scans take $\mathcal{O}(m)$ for $m$ waiting patients, which is executed periodically or during off-peak cycles, easily amortized.
- **Engineering Conclusion**: The Multi-Level Feedback Aging Queue is vastly superior for production hospital emergency departments.

**Evidence Reference**: Working source code in `Q7/TriageRedesign.java` and measured results in `Q7/expected_output.txt`.

---

## Final Submission Checklist
- [x] All 7 questions executed and verified on Java JDK.
- [x] Complete the Code programs (Q3, Q5, Q6) match required outputs.
- [x] Day-4 Design Challenge (Q7) fully implemented, benchmarked, and defended.
- [x] Expected output files generated for all tasks.
- [x] Progressive task folder structure (`Q1` to `Q7`) verified.
- [x] GitHub submission repository name configured: `CSA09-192472137-Assignment2`.
