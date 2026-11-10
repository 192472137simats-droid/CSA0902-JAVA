# Q2 – User-Defined Exceptions and Propagation in an Online Examination Portal

## Overview
This task models multi-layered exception propagation, custom exception hierarchies, and exception chaining across Controller, Service, and Storage tiers.

## Files
- `StorageDemo.java`: Completed program wrapping low-level `IOException` into high-level `SubmissionStorageException` while preserving root cause.
- `expected_output.txt`: Captured output showing user-facing error message and underlying disk cause.

## Compilation and Execution
```bash
javac StorageDemo.java
java StorageDemo
```

## Summary of Findings & Key Answers
- **What Happens if `IOException` is Omitted from Constructor**:
  - If `new SubmissionStorageException("Could not store " + fileName)` were called without passing `e`, `e.getCause()` would return `null`. In `StorageDemo.main()`, calling `e.getCause().getMessage()` would immediately trigger a `NullPointerException`!
- **Why Domain Exceptions are Superior to Raw IOExceptions**:
  - Exposing raw `IOException` to the presentation layer leaks infrastructural implementation details (e.g., file paths, disk quotas) and breaks encapsulation. Wrapping into domain-specific exceptions (`SubmissionStorageException`) allows the controller to present a friendly user message while keeping root cause diagnostic details in the logs.
- **Alternative Concept (Unchecked Exceptions)**:
  - Making custom exceptions extend `RuntimeException` avoids cluttering intermediate method signatures with `throws` declarations. However, it removes compile-time enforcement, risking unhandled exceptions escaping to the top level.
