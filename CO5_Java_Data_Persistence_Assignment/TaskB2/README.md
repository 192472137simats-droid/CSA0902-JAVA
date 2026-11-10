# Task B2 – Engineer an Adversarial Input

## Overview
This task investigates system vulnerability under deliberately crafted adversarial inputs, demonstrating:
1. SQL injection exploits via unescaped string concatenation vs parameter binding immunity in `PreparedStatement`.
2. Fault isolation when ingesting damaged file records containing invalid field counts, empty lines, and non-numeric numbers.

## Files
- `AdversarialInputDemo.java`: Testing harness executing SQL injection payloads and corrupted CSV input streams.
- `expected_output.txt`: Captured test execution output.

## Compilation and Execution
```bash
javac AdversarialInputDemo.java
java AdversarialInputDemo
```

## Summary of Findings & Key Answers
- **Why Dynamic Concatenation Fails**: Passing `"admin' OR '1'='1' --"` alters the SQL parser's Abstract Syntax Tree (AST). The tautology `'1'='1'` evaluates to `true` for all rows, and the trailing `--` comments out the password predicate, authenticating attackers without a valid password.
- **Why PreparedStatement is Immune**: `PreparedStatement` pre-compiles the SQL template (`SELECT * FROM users WHERE id = ? AND pwd = ?`) with fixed AST nodes. Parameter values bound to placeholders are transmitted as literal string data; the SQL compiler never parses them as SQL commands or operators.
- **Alternative Concept (Regex / Escaping vs PreparedStatement)**:
  - Attempting to sanitize inputs by escaping single quotes or using regular expressions is fragile and prone to bypasses (e.g., Unicode variants, multi-byte encoding attacks).
  - `PreparedStatement` provides 100% architectural immunity at zero runtime regex validation overhead.
