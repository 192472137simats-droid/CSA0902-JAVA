# Task 4 – Event-Handling Forensics: DrawPad Defect Analysis and Repair

## Overview
This task conducts a forensic investigation into two bugs in a custom drawing canvas (`DrawPad`):
1. **Uncalled Handler Bug**: The listener method was misspelled as `mouseclicked` instead of `mouseClicked`. Because `MouseAdapter` provides empty default implementations, the misspelled method was treated as an uncalled new method.
2. **Transient Rendering Bug**: The original code drew directly to `getGraphics()`. In AWT/Swing, direct drawing onto the graphics context is erased whenever the OS repaints or minimizes the window.

## Files
- `DrawPad.java`: Repaired implementation fulfilling TODO 1 (storing points, updating label, requesting repaint) and TODO 2 (drawing centered circles in `paint()`).
- `expected_output.txt`: Captured test run proving that points are recorded and survive repainting.

## Compilation and Execution
```bash
javac DrawPad.java
java DrawPad
```

## Summary of Findings & Compile-Time Prevention
- **Role of `@Override`**: If `@Override` had been specified, the Java compiler would have checked `MouseAdapter` for `void mouseclicked(MouseEvent e)`. Because the method does not exist in the superclass, the compiler would have rejected it with:
  ```text
  error: method does not override or implement a method from a supertype
  ```
- **Alternative Concept (Off-Screen BufferedImage)**:
  - Repainting by iterating over $n$ points takes $\mathcal{O}(n)$ time.
  - Storing a raster `BufferedImage` and drawing each dot once to the image buffer allows `paint(Graphics g)` to simply blit the image in $\mathcal{O}(1)$ time regardless of whether 10 dots or 100,000 dots were drawn, at the cost of fixed image memory ($\text{width} \times \text{height} \times 4\text{ bytes}$).
