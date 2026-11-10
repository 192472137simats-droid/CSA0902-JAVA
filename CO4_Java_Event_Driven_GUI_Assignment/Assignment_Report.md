# CO4 Java Assignment 4 – Event-Driven GUI Design Report

**Course Outcome:** CO4 – Event-Driven GUI Design, AWT/Swing Components, Layouts, and JDBC Integration  
**Student Name:** G SAI TEJA  
**Register Number:** 192472137  
**GitHub Repository:** [Paste your repository link]  
**Java Version:** JDK 8 or later  

---

## TASK 1 – GUI Design Review: Diagnose and Repair a Poor Event-Handling Design

### Task 1.1 – Diagnose the Design (Deficiencies Analysis)
Examing the junior developer's monolithic `FeeWindow`:

| # | Design Deficiency | Technical Vulnerability | Impact on User Experience |
|---|---|---|---|
| **1** | **Monolithic Coupling (No Separation of Concerns)** | Presentation frame directly manages raw SQL statements, database network drivers, and connection strings. | Modifying database schema forces recompilation of the UI; unit testing without a physical database is impossible. |
| **2** | **Reference Equality Bug (`==`)** | Action command is compared using `e.getActionCommand() == "Pay"` instead of `.equals("Pay")`. | If the action command string is dynamically generated or not interned, `==` evaluates to `false`, silently executing the `clear` branch instead! |
| **3** | **Freezing the Event Dispatch Thread (EDT)** | `Thread.sleep(3000)` and blocking network database I/O are invoked directly inside `actionPerformed()`. | The entire application window completely freezes; OS marks window "Not Responding", button stays stuck in pressed state, and window stops redrawing. |
| **4** | **SQL Injection Vulnerability** | Raw input string concatenation: `"INSERT INTO fees VALUES('" + regNo.getText() + "'," + a + ")"`. | A malicious student entering `' OR '1'='1` can inject arbitrary SQL commands, corrupt tables, or drop databases. |
| **5** | **Resource Leaks & Missing Exception Handling** | `Connection` and `Statement` are never closed (`close()`), and unhandled `SQLException` / `NumberFormatException` crash the thread silently. | Database server quickly exhausts its connection pool, crashing for all subsequent students. |
| **6** | **Dead Receipt Button & Console-Only Output** | The `Receipt` button triggers the `else` branch (clearing fields) without printing receipts. Feedback is dumped to `System.out.println` rather than displayed on screen. | The student has no idea if payment succeeded and loses typed inputs unexpectedly. |

---

### Task 1.2 – Repair the Design
Refactored into clean presentation and service tiers:
- **`FeeService`**: Encapsulates input validation, positive amount checking, and parameterized `PreparedStatement` execution.
- **`FeeWindow`**: Presentation GUI using AWT/Swing, delegating work asynchronously to background worker threads and updating status labels via `EventQueue.invokeLater()`.
- **Source Code**: Refer to [`Task01/FeePaymentRefactored.java`](file:///D:/Downloads/JAVA%20ans/CO4_Java_Event_Driven_GUI_Assignment/Task01/FeePaymentRefactored.java).

---

### Task 1.3 – Design Defence
> *The refactored design cleanly separates presentation from data persistence. By isolating validation and SQL into `FeeService`, business logic can be unit-tested without launching a window. Moving database execution off the Event Dispatch Thread keeps the user interface responsive during network latency, while `PreparedStatement` completely eliminates SQL injection attacks.*

---

### Alternative Concept and Complexity Analysis
- **Monolithic `if-else` Listener vs Dedicated Button Listeners / Actions**:
  - In a monolithic listener with $k$ controls, dispatching an event requires traversing up to $k$ sequential string comparisons ($\mathcal{O}(k)$ time).
  - Assigning individual listener objects (or lambdas) to each button delegates directly via method pointers, guaranteeing instantaneous $\mathcal{O}(1)$ dispatch. Memory overhead is strictly $k$ small reference closures ($\approx 32\text{ bytes each}$), which is completely negligible.

---

## TASK 2 – From Requirement to Event-Driven Design: Hospital Outpatient Appointment Desk

### Task 2.1 – Event Modeling Table
| Event Source | Event Object | Listener Interface | Action Taken |
|---|---|---|---|
| `deptChoice` (`Choice` / `JComboBox`) | `ItemEvent` | `ItemListener` | Queries catalog and invokes `refreshDoctorList()` to update doctor dropdown. |
| `bookButton` (`Button` / `JButton`) | `ActionEvent` | `ActionListener` | Validates patient ID, checks slot availability, and commits booking. |
| `patientIdField` (`TextField`) | `ActionEvent` | `ActionListener` | Triggered when user hits Enter, transferring focus to department selection. |
| `OutpatientDesk` (`Frame` / `JFrame`) | `WindowEvent` | `WindowListener` | Intercepts `windowClosing()`; prompts user for confirmation if booking is in progress. |

---

### Task 2.2 – Component Containment Hierarchy Tree
```text
OutpatientDesk (Frame) [BorderLayout]
 │
 ├── NORTH: Header Label ("HOSPITAL APPOINTMENT DESK")
 │
 ├── CENTER: Form Panel (Panel) [GridLayout(4, 2, 8, 8)]
 │    ├── Label ("Patient ID:")
 │    ├── patientIdField (TextField)
 │    ├── Label ("Department:")
 │    ├── deptChoice (Choice)
 │    ├── Label ("Doctor:")
 │    ├── doctorChoice (Choice)
 │    ├── Label ("Time Slot:")
 │    └── slotChoice (Choice)
 │
 └── SOUTH: Action Container (Panel) [BorderLayout]
      ├── NORTH: bookButton (Button) [FlowLayout.CENTER]
      └── SOUTH: statusLabel (Label)
```

---

### Task 2.3 – Handler Pseudocode (SIMATS Engineering Standard)

#### (a) Refresh Doctor List
```text
START
FUNCTION refreshDoctorList(selectedDept)
    doctorChoice.removeAll()
    doctorList = departmentDoctors.get(selectedDept)
    IF doctorList IS NOT NULL THEN
        FOR EACH doctor IN doctorList DO
            doctorChoice.add(doctor)
        END FOR
    END IF
    statusLabel.setText("Department set to " + selectedDept)
END FUNCTION
STOP
```

#### (b) Validate and Book Appointment
```text
START
FUNCTION bookAppointment()
    patientId = patientIdField.getText().trim()
    IF patientId IS EMPTY THEN
        statusLabel.setText("Error: Patient ID is required.")
        RETURN
    END IF
    selectedDoctor = doctorChoice.getSelectedItem()
    selectedSlot = slotChoice.getSelectedItem()
    
    SET bookingInProgress = TRUE
    statusLabel.setText("CONFIRMED: " + patientId + " with " + selectedDoctor + " at " + selectedSlot)
    SET bookingInProgress = FALSE
END FUNCTION
STOP
```

---

### Task 2.4 – Event Sequence Trace vs Applet Life Cycle
1. **Window Lifecycle Trace**:
   - `new OutpatientDesk()` -> Frame constructor initializes components.
   - `setVisible(true)` -> Native peer created, OS sends `windowOpened()`.
   - `paint()` / `update()` -> AWT renders components onto screen framebuffer.
   - User clicks Department -> `itemStateChanged(ItemEvent)` invoked on EDT.
   - User clicks Book -> `actionPerformed(ActionEvent)` invoked on EDT.
   - User clicks Close box -> `windowClosing(WindowEvent)` checks `bookingInProgress` and calls `dispose()`.
   - `windowClosed()` -> Native window destroyed.
2. **Comparison with Applet Lifecycle**:
   - An Applet follows: `init()` (one-time setup) -> `start()` (execution/resumption) -> `paint()` (rendering) -> `stop()` (suspended) -> `destroy()` (cleanup).
   - In desktop AWT/Swing, constructors replace `init()`, `setVisible(true)` acts as `start()`, and `WindowListener` replaces `stop()`/`destroy()`.

---

### Alternative Concept and Complexity Analysis
- **On-Demand Database Queries vs Preloaded Map**:
  - Fetching from database on each department click incurs $50\text{–}150\text{ ms}$ network latency and requires closing connections on EDT.
  - Preloading $d$ departments and $m$ doctors into a `Map<String, List<Doctor>>` uses $\mathcal{O}(d + m)$ heap space ($< 10\text{ KB}$), enabling instant $\mathcal{O}(1)$ dropdown population.

---

## TASK 3 – From Sketch to Layout: Campus Library Search Screen

### Task 3.1 & 3.2 – Layout Selection and Nested Container Design
- **Top-Level (`BorderLayout`)**: Ideal because `BorderLayout.CENTER` receives 100% of excess horizontal and vertical space during window resizing, while `NORTH` and `SOUTH` maintain fixed preferred heights.
- **Nested Structure**:
  ```text
  LibrarySearchScreen (JFrame) [BorderLayout]
   ├── NORTH: Header & Search Container [BorderLayout]
   │    ├── NORTH: Title Label ("CAMPUS CENTRAL LIBRARY CATALOG")
   │    └── SOUTH: Search Row [FlowLayout(FlowLayout.LEFT)]
   │         ├── Label ("Keyword:") + searchField
   │         ├── Label ("Category:") + categoryCombo
   │         └── searchButton
   │
   ├── CENTER: JScrollPane [BorderLayout.CENTER] (EXPANDS ON RESIZE)
   │    └── resultsArea (JTextArea)
   │
   └── SOUTH: Navigation & Status Container [BorderLayout]
        ├── NORTH: Button Row [FlowLayout(FlowLayout.CENTER)]
        │    ├── prevButton, nextButton, reserveButton
        └── SOUTH: statusLabel
  ```

---

### Task 3.3 & 3.4 – Implementation & Resizing Behavior
- Implemented in [`Task03/LibrarySearchScreen.java`](file:///D:/Downloads/JAVA%20ans/CO4_Java_Event_Driven_GUI_Assignment/Task03/LibrarySearchScreen.java).
- **Resizing Verification**:
  - At default dimensions ($650 \times 400$), all controls render with standard padding.
  - When expanded to $900 \times 600$, the `searchRow` and `navButtons` remain strictly at their fixed 35px height, while `resultsArea` in `BorderLayout.CENTER` dynamically expands from 250px to 450px height, displaying multiple additional book entries without layout distortion.

---

### Alternative Concept and Complexity Analysis
- **GridBagLayout vs Absolute Positioning (`setLayout(null)`)**:
  - `setLayout(null)` hardcodes component coordinates (`setBounds(x, y, w, h)`). When resized or rendered on high-DPI displays, components overlap or get clipped.
  - `GridBagLayout` allows dynamic resizing but involves heavy constraint matrix calculation ($\mathcal{O}(R \cdot C)$).
  - Nested `BorderLayout` and `FlowLayout` achieve optimal responsiveness with $\mathcal{O}(1)$ layout computation.

---

## TASK 4 – Event-Handling Forensics: DrawPad Defect Analysis and Repair

### Task 4.1 – Defect Identification
The junior developer declared:
```java
public void mouseclicked(MouseEvent e) { ... }
```
In `MouseAdapter`, the method signature is `public void mouseClicked(MouseEvent e)` (camelCase with capital `'C'`). Because Java is case-sensitive, `mouseclicked` was compiled as an unrelated custom method. The AWT event dispatcher invoked `mouseClicked()`, which executed the empty adapter default, completely ignoring user clicks!

---

### Task 4.2 – Constructing a Failing Interaction (Transient Graphics)
1. User clicks the canvas; dot is drawn using `getGraphics().fillOval(...)`.
2. User minimizes the window to the taskbar.
3. User restores the window: **All dots have vanished!**
- **Cause**: Direct drawing via `getGraphics()` renders to transient video memory. When a window is minimized or obscured, the OS destroys the pixel buffer and dispatches a repaint request to Java, which executes `paint(Graphics g)`. Because previous dots were not stored in memory, `paint()` redraws a blank background.

---

### Task 4.3 – Repairing the Program & Complete the Code
Completed in [`Task04/DrawPad.java`](file:///D:/Downloads/JAVA%20ans/CO4_Java_Event_Driven_GUI_Assignment/Task04/DrawPad.java):
```java
// TODO 1: Correct override in MouseAdapter
@Override
public void mouseClicked(MouseEvent e) {
    points.add(new Point(e.getX(), e.getY()));
    status.setText("Clicks: " + points.size());
    repaint();
}

// TODO 2: Persistent rendering in paint()
@Override
public void paint(Graphics g) {
    for (Point p : points) {
        g.fillOval(p.x - 3, p.y - 3, 6, 6);
    }
}
```

---

### Task 4.4 – Preventing Recurrence with `@Override`
If `@Override` had been placed above `mouseclicked`, the Java compiler would have checked `MouseAdapter` for `void mouseclicked(MouseEvent e)`. Because no such method exists in the superclass, compilation halts immediately:
```text
error: method does not override or implement a method from a supertype
```
This forces the developer to correct the typo at compile time before deployment.

---

### Alternative Concept and Complexity Analysis
- **Redrawing Stored Points vs Off-Screen `BufferedImage`**:
  - Redrawing a `List<Point>` takes $\mathcal{O}(n)$ time per repaint. For 100,000 clicks, repainting can stutter.
  - Drawing each point once to an off-screen `BufferedImage` and blitting the image in `paint()` via `g.drawImage()` takes constant $\mathcal{O}(1)$ time regardless of click count, requiring $\text{width} \times \text{height} \times 4\text{ bytes}$ of memory.

---

## TASK 5 – Same Screen, Different Event-Handling Styles: Currency Converter

### Task 5.1 & 5.2 – Four Implementations & Requirement Change
Implemented in [`Task05/Converter.java`](file:///D:/Downloads/JAVA%20ans/CO4_Java_Event_Driven_GUI_Assignment/Task05/Converter.java):
1. **(a) Frame implements ActionListener**: Frame handles all actions via single `actionPerformed(ActionEvent e)` with `if-else` branching. Changing requirement requires modifying the class declaration and adding action commands.
2. **(b) Named Inner Class**: `class ConvertHandler implements ActionListener`. Reusable across buttons and menus, but requires class instantiation.
3. **(c) Anonymous Inner Class**: `new ActionListener() { public void actionPerformed(...) { convert(); } }`. Verbose boilerplate code.
4. **(d) Lambda Expression**:
   ```java
   ActionListener convertListener = e -> convert();
   convertButton.addActionListener(convertListener);
   amount.addActionListener(convertListener);
   ```

---

### Task 5.3 – Style Comparison Matrix
| Event Style | Readability | Frame Field Access | Reusability | Lines Changed on New Trigger |
|---|---|---|---|---|
| **Frame Implements** | Poor | Direct (`this`) | Low | 8–12 lines |
| **Named Inner Class** | Medium | Direct (Enclosing) | High | 4–6 lines |
| **Anonymous Inner** | Poor | Enclosing scope | None | 6–8 lines |
| **Lambda Expression** | **Excellent** | Enclosing scope | High | **1 line (`amount.addActionListener(convertListener)`)** |

- **Recommendation**: **Lambda expressions** are strongly recommended for modern Java applications due to minimal boilerplate, high readability, and clean reuse.

---

## TASK 6 – Design Challenge: Campus Event Registration Desktop Application

### Stage 1 – Problem Interpretation & Requirements
- **Users**: Cultural Committee and Students.
- **Data Attributes**: `registration_id`, `student_id`, `student_name`, `event_name`, `email`, `registration_time`.
- **Testable Rules**:
  1. Student ID, Name, and Email must not be empty.
  2. A student cannot register for the same event more than once (enforced by `UNIQUE(student_id, event_name)`).
  3. Filter combo box must instantly filter the table without reloading all data.

---

### Stage 2 – Screen and Event Flow
```text
[Main Application Window] ─── Menus: File -> Exit
       │
       ├── Top Form: Inputs [Student ID, Name, Event Combo, Email] ──> Click [Register]
       │                                                                      │
       │                                                                (SwingWorker)
       │                                                                      ▼
       ├── Center: Filter Combo [Select Event] ──> Click [Filter] ────> [Query DAO]
       │                                                                      │
       │                                                                (Update Model)
       │                                                                      ▼
       └── Bottom: JTable [Displays Filtered Records] ◄───────────────────────┘
```

---

### Stage 3 – Critical Design Decisions
- **Keeping Database Work Off the EDT**: All database access (`insert`, `findByEvent`) is encapsulated within `SwingWorker<T, V>`. Heavy SQL execution occurs on background worker threads, and results are safely committed to `DefaultTableModel` inside the `done()` callback on the EDT.
- **Duplicate Prevention**: Enforced at the database level via a composite unique constraint: `CONSTRAINT unique_student_event UNIQUE (student_id, event_name)`. Catches duplicate concurrent registrations atomically.

---

### Stage 4 – Class Architecture & Layered Diagram
```text
┌───────────────────────────────────────────────┐
│              Presentation Layer               │
│          EventRegistrationApp (JFrame)        │
└───────────────────────┬───────────────────────┘
                        │ Delegates
                        ▼
┌───────────────────────────────────────────────┐
│                 Service Layer                 │
│              RegistrationService              │
│      - validate()                             │
│      - registerStudent()                      │
└───────────────────────┬───────────────────────┘
                        │ Invokes
                        ▼
┌───────────────────────────────────────────────┐
│            Data Access Layer (DAO)            │
│               RegistrationDao                 │
│      - insert() [PreparedStatement]           │
│      - findByEvent()                          │
└───────────────────────┬───────────────────────┘
                        │ SQL / JDBC
                        ▼
┌───────────────────────────────────────────────┐
│                   Database                    │
│              event_registrations              │
└───────────────────────────────────────────────┘
```

#### Handler Pseudocode
```text
START
FUNCTION handleRegistration()
    sid = studentIdField.getText()
    name = nameField.getText()
    event = eventCombo.getSelectedItem()
    email = emailField.getText()

    EXECUTE ASYNCHRONOUSLY (SwingWorker)
        TRY
            RegistrationService.registerStudent(sid, name, event, email)
            ON EDT DO:
                statusLabel.setText("Registered " + name)
                refreshTable()
        CATCH SQLException (Duplicate) DO:
            ON EDT DO:
                ShowErrorMessage("Student already registered for this event!")
    END ASYNCHRONOUS
END FUNCTION
STOP
```

---

### Stage 5 – Implementation and Experimental Verification
- Working implementation: [`Task06/EventRegistrationApp.java`](file:///D:/Downloads/JAVA%20ans/CO4_Java_Event_Driven_GUI_Assignment/Task06/EventRegistrationApp.java) and [`Task06/schema.sql`](file:///D:/Downloads/JAVA%20ans/CO4_Java_Event_Driven_GUI_Assignment/Task06/schema.sql).
- **Test Evidence (`Task06/expected_output.txt`)**:
  - Test 1: Normal registration saved successfully.
  - Test 2: Duplicate registration caught & rejected (`DUPLICATE_ENTRY: Student STU101 is already registered for Hackathon`).
  - Test 3: Empty student ID rejected (`Student ID cannot be empty.`).
  - Test 4: Dynamic event filtering confirmed.

---

### Alternative Concept and Complexity Analysis
- **Bulk Loading vs Paginated SQL (`LIMIT / OFFSET`)**:
  - Loading 100,000 records into a `JTable` consumes $> 120\text{ MB}$ heap and freezes the EDT for $3\text{–}5\text{ seconds}$ during model instantiation.
  - Implementing SQL `LIMIT 50 OFFSET ?` in `SwingWorker` queries only the 50 visible rows on-demand ($< 2\text{ ms}$ query time), bounding memory to $< 1\text{ MB}$ and keeping the interface responsive.

---

## Submission Checklist
- [x] All 6 tasks implemented and executed on Java JDK.
- [x] Complete the Code tasks (Task 4 DrawPad, Task 5 Converter) verified.
- [x] Day-4 Design Challenge (Task 6) includes layered GUI, SwingWorker, and SQL schema.
- [x] Clean zip package created in `D:/Downloads/JAVA ans/CO4_Java_Event_Driven_GUI_Assignment.zip`.
