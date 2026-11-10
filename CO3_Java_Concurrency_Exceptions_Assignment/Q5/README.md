# Q5 – Race Conditions in a Cinema Seat-Booking System

## Overview
This task investigates race conditions and synchronization mechanisms in a cinema ticketing scenario where 100 concurrent threads compete for 10 seats.

## Files
- `SeatBooking.java`: Completed "Complete the Code" implementation enforcing atomic `book()` check-then-act execution.
- `RaceConditionDemo.java`: Diagnostic harness demonstrating severe double-booking (selling 20–30 seats for a 10-seat hall) when unsynchronized.
- `expected_output.txt`: Verified program output strictly selling exactly 10 seats.

## Compilation and Execution
```bash
# 1. Main Synchronized Solution
javac SeatBooking.java
java SeatBooking

# 2. Race Condition Demonstration
javac RaceConditionDemo.java
java RaceConditionDemo
```

## Summary of Findings & Engineering Answers
- **Why Unsynchronized Code Sells More Than 10 Seats**: Check-then-act (`if (!booked[seat]) { booked[seat] = true; bookingsMade++; }`) is non-atomic. Multiple threads concurrently evaluate `booked[seat] == false` before any thread commits its write. Consequently, multiple threads proceed into the critical section for the exact same seat, double-selling tickets and arbitrarily corrupting the non-atomic `bookingsMade++` counter.
- **Synchronization Strategy for 20-Screen Cinema**:
  - Do NOT synchronize statically on the class or across the entire cinema, which would unnecessarily force users booking Screen 1 to block users booking Screen 20!
  - Instead, use **fine-grained instance-level or seat-level locking** (e.g., an array of `ReentrantLock` per screen, or `AtomicBoolean[]` per seat using `compareAndSet(false, true)`). This isolates contention strictly to users competing for the same physical seat, maximizing multi-core throughput.
