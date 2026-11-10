# Task 5 – Same Screen, Different Event-Handling Styles: Currency Converter

## Overview
This task compares four distinct Java event-handling styles using a Currency Converter application:
1. Frame itself implements `ActionListener`
2. Named inner class
3. Anonymous inner class
4. Lambda expression (`e -> convert()`)

## Files
- `Converter.java`: Completed "Complete the Code" implementation (lambda version) fulfilling TODO 1 (dual trigger registration) and TODO 2 (robust numeric parsing and formatting).
- `expected_output.txt`: Captured output verifying valid conversions (USD, EUR) and error handling ("Invalid amount").

## Compilation and Execution
```bash
javac Converter.java
java Converter
```

## Comparative Analysis of Event-Handling Styles
| Style | Readability & Conciseness | Access to Frame State | Reusability | Recommendation for Large Applications |
|---|---|---|---|---|
| **(a) Frame implements Listener** | Low (creates monolithic `if-else` chain) | Direct | Low | Discouraged (poor separation) |
| **(b) Named Inner Class** | Medium | Direct | High (can attach to multiple controls) | Good for complex, stateful handlers |
| **(c) Anonymous Inner Class** | Medium-Low (boilerplate code) | Enclosing scope | Low (single-use) | Legacy (superseded by lambdas) |
| **(d) Lambda Expression** | **Highest (1–2 lines)** | Enclosing scope | High (can assign to variable) | **Strongly Recommended** |

- **Alternative Concept (Method References & Actions)**:
  - Using `this::convert` or a shared `Action` (`AbstractAction`) centralizes text, icon, tooltip, enabled state, and keyboard shortcuts into a single object shared across buttons and menus, maximizing code reuse.
