# Task 3 – From Sketch to Layout: Campus Library Search Screen

## Overview
This task designs and implements a responsive nested layout for a Campus Library Search interface adhering to resizing constraints: the center results area must expand and shrink dynamically while search rows and bottom buttons retain their fixed preferred heights.

## Files
- `LibrarySearchScreen.java`: Swing implementation using nested `BorderLayout`, `FlowLayout`, and `JScrollPane`.
- `expected_output.txt`: Runtime dimensions verification.

## Compilation and Execution
```bash
javac LibrarySearchScreen.java
java LibrarySearchScreen
```

## Layout Strategy & Resizing Behavior
1. **Root Frame (`BorderLayout`)**:
   - `NORTH`: Fixed-height nested panel containing title label and `FlowLayout` search controls.
   - `CENTER`: `JScrollPane` enclosing `JTextArea`. `BorderLayout` allocates 100% of excess horizontal and vertical space to the `CENTER` component during window resizing.
   - `SOUTH`: Fixed-height panel holding `FlowLayout` pagination buttons and footer status label.
2. **Alternative Concept (GridBagLayout vs Absolute Positioning)**:
   - `setLayout(null)` (absolute coordinates) breaks completely across different OS resolutions and window resizing, leaving empty margins and clipped components.
   - `GridBagLayout` supports fine-grained weight distributions (`weightx`, `weighty`), but requires verbose constraints code.
   - **Nested `BorderLayout` + `FlowLayout`** provides the cleanest, most maintainable architecture.
