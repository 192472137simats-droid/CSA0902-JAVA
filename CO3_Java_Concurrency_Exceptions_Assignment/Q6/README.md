# Q6 – Deadlock and Inter-Thread Communication

## Overview
This task investigates two core concurrency challenges:
- **Part A**: Deadlock conditions and lock-ordering prevention in account fund transfers.
- **Part B**: Producer-Consumer inter-thread communication (`wait()`, `notify()`, `notifyAll()`) using a bounded circular buffer (mess token counter).

## Files
- `TokenCounter.java`: Completed "Complete the Code" program implementing bounded-buffer token generation and consumption.
- `WalletDeadlock.java`: Demonstration of deadlock avoidance via deterministic global lock acquisition ordering.
- `expected_output.txt`: Program output matching `Sum of tokens collected: 210 (expected 210)`.

## Compilation and Execution
```bash
# 1. Main TokenCounter Solution
javac TokenCounter.java
java TokenCounter

# 2. Wallet Transfer Deadlock Prevention
javac WalletDeadlock.java
java WalletDeadlock
```

## Summary of Findings & Key Answers
- **Why `wait()` Must Be in a `while` Loop**:
  1. **Spurious Wakeups**: The operating system may awaken a waiting thread even if no signal was dispatched.
  2. **State Invalidation**: In a multi-consumer setup, when `notifyAll()` wakes several threads, the first thread to re-acquire the lock consumes the item. If remaining threads used `if`, they would proceed without re-checking, causing buffer underflow or index out-of-bounds errors.
- **The Four Coffman Conditions for Deadlock**:
  1. *Mutual Exclusion* (exclusive lock ownership).
  2. *Hold and Wait* (holding lock A while waiting for lock B).
  3. *No Preemption* (locks cannot be forcefully confiscated).
  4. *Circular Wait* (Thread 1 holds A waiting for B; Thread 2 holds B waiting for A).
- **Deadlock Resolution**: Breaking the *Circular Wait* condition by enforcing a strict global lock hierarchy (e.g. `firstLock = min(acc1.id, acc2.id)`).
- **Alternative Concept**: Using `ArrayBlockingQueue<Integer>` handles blocking, condition variables, and reentrancy internally, eliminating error-prone manual lock management.
