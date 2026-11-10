# CO2 Java Assignment 2 – Java Collections Framework

**Student Name:** G SAI TEJA  
**Register Number:** 192472137  
**Java Version:** JDK 8 or later  
**Repository Name:** `CSA09-192472137-Assignment2`

## Project Contents
- `Assignment_Report.md` – Comprehensive engineering answers and analyses for Questions 1 through 7.
- `Q1/TicketBenchmark.java` – Railway e-ticket text generation benchmark (String vs StringBuilder vs StringBuffer).
- `Q2/PlaylistBenchmark.java` – Music playlist editor benchmark (ArrayList vs LinkedList vs ArrayDeque).
- `Q3/Attendance.java` – Event attendance de-duplication ("Complete the Code" with hashCode-equals contract).
- `Q3/AttendanceInvestigation.java` – Investigation into reference equality, duplicate exposure, and key mutation.
- `Q4/PharmacyInventory.java` – Hospital pharmacy catalog lookup & evening reports (HashMap vs TreeMap vs LinkedHashMap).
- `Q5/LeaderboardDemo.java` – Sports leaderboard generic ranking ("Complete the Code" with multi-field comparator).
- `Q5/ComparatorAnalysis.java` – Generics reusability test and integer subtraction overflow demonstration.
- `Q6/Triage.java` – Emergency room patient priority triage ("Complete the Code" with PriorityQueue and arrival counter).
- `Q6/TriageInvestigation.java` – Binary heap inspection, 8-patient hand verification, and growth analysis.
- `Q7/TriageRedesign.java` – Day-4 Design Challenge: Multi-level aging queue solving emergency triage starvation.
- Each task folder contains its own `README.md` and captured `expected_output.txt`.

---

## How to Compile and Run

Open a terminal or command prompt in the project root directory.

### Question 1: String, StringBuilder or StringBuffer Benchmark
```bash
cd Q1
javac TicketBenchmark.java
java TicketBenchmark
cd ..
```

### Question 2: ArrayList, LinkedList & ArrayDeque Benchmark
```bash
cd Q2
javac PlaylistBenchmark.java
java PlaylistBenchmark
cd ..
```

### Question 3: Event Attendance De-duplication (hashCode–equals Contract)
```bash
cd Q3
javac Attendance.java
java Attendance

# Optional deep-dive investigation:
javac AttendanceInvestigation.java
java AttendanceInvestigation
cd ..
```

### Question 4: Pharmacy Inventory Stock Lookup & Reports
```bash
cd Q4
javac PharmacyInventory.java
java PharmacyInventory
cd ..
```

### Question 5: Generic Leaderboard & Multi-Field Comparator
```bash
cd Q5
javac LeaderboardDemo.java
java LeaderboardDemo

# Optional generics reuse & subtraction bug analysis:
javac -cp . ComparatorAnalysis.java
java -cp . ComparatorAnalysis
cd ..
```

### Question 6: Hospital Emergency Triage Priority Queue
```bash
cd Q6
javac Triage.java
java Triage

# Optional heap structure & 8-patient verification:
javac -cp . TriageInvestigation.java
java -cp . TriageInvestigation
cd ..
```

### Question 7: Day-4 Design Challenge (Triage Redesign & Validation)
```bash
cd Q7
javac TriageRedesign.java
java TriageRedesign
cd ..
```

---

## Before Submitting
1. Replace `G SAI TEJA` and `192472137` in this file and in `Assignment_Report.md`.
2. Run all Java programs on your system to confirm expected outputs match.
3. Push to your GitHub repository named `CSA09-192472137-Assignment2`.
4. Include output screenshots for each task in your GitHub repository and report.
