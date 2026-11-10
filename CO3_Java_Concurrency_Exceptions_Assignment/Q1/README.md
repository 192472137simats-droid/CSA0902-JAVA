# Q1 – Handling Failure in a Bank ATM Simulation

## Overview
This task models failure scenarios in a bank ATM cash withdrawal workflow, examining the `Throwable` hierarchy, checked vs unchecked exceptions, catch block ordering, and the guaranteed execution of `finally`.

## Files
- `ATMSimulation.java`: Implementation supporting 5 distinct test paths (valid input, format error, negative amount, insufficient funds, and missing card reader configuration).
- `expected_output.txt`: Execution log demonstrating catch-block resolution and guaranteed finally block execution.

## Compilation and Execution
```bash
javac ATMSimulation.java
java ATMSimulation
```

## Summary of Findings
- **Finally Guarantee**: Regardless of whether a transaction completes normally, encounters an input error, or throws a hardware failure, the `finally` block runs unconditionally, guaranteeing that the user's card is ejected and session resources are closed.
- **Catch Block Hierarchy**: Specific subclasses (`FileNotFoundException`, `NumberFormatException`) must precede broader types (`IllegalArgumentException`, `Exception`); otherwise, a compiler error (`unreachable catch block`) occurs.
- **Catching `Exception e` Inquiry**: Catching a broad `Exception` is acceptable only at the outermost UI/boundary layer as a fallback safety net. At internal layers, it swallows distinct failure semantics, preventing contextual recovery and precise audit logging.
