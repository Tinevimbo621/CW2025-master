
# Tetris Game – COMP2042 Coursework

A full **JavaFX-based Tetris game** built for COMP2042, featuring multiple game modes, adaptive difficulty, ghost pieces, leaderboards, and a highly modular **MVC architecture**.

---

##  Table of Contents

* **[Introduction](#-tetris-game--comp2042-coursework)**
* **[Environment Setup](#-environment-setup)**
* **[Project Setup](#️-project-setup-intellij)**
* **[Compile & Run](#️-compile--run)**
* **[ Features Summary](#️-features-summary)**
* **[ Refactoring Summary](#-refactoring-summary)**
* **[Unexpected Problems & Solutions](#-unexpected-problems--solutions)**
* **[How to Run Tests](#-how-to-run-tests)**
* **[Author](#-author)**

## Recommended IDEs
* IntelliJ IDEA 2023 3+
* IntelliJ IDEA Community Edition 2023 3+
* Eclipse 2023-12+
* VS Code with Java Extension Pack

##  Environment Setup

### 1. Install Java (JDK 21+ Recommended)

Ensure you have a modern Java Development Kit installed.

* **Download JDK:** [Adoptium](https://adoptium.net) or [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html)
* **Verify installation:**
    ```bash
    java -version
    ```

### 2. Install JavaFX SDK

The project uses JavaFX for the graphical interface.

* **Step 1 — Download JavaFX**
  Download JavaFX SDK for your OS: [GluonHQ JavaFX Downloads](https://gluonhq.com/products/javafx/)
* **Step 2 — Extract the SDK**
  Extract the ZIP file to a simple directory, for example:
    ```bash
    C:/javafx-sdk/
    ```

---

## Project Setup

1.  **Clone or import the project:**
    ```bash
    git clone <https://github.com/Tinevimbo621/CW2025-master>
    ```
2. ### IDE Setup & Configuration

This project uses Maven for dependency management, build execution, and running JavaFX. All IDEs can import it automatically.

### 1️ IntelliJ IDEA (Recommended)
1.  * Open IntelliJ.
    * Go to File → Open...
    * Select the project folder and click OK.
2.  **Download Maven dependencies:**
    * Open the right sidebar → Maven panel.
    * Click the **Reload All Maven Projects** button (circular arrow icon) to download dependencies (`mockito`, `junit5`, `JavaFX Maven Plugin`).
3.  **Run Configuration:**
    * Run the `com.comp2042.Main` class once to create a temporary configuration.
    * Edit the configuration (`Run` → `Edit Configurations`).
    * In **VM Options**, paste the following (adjust the path to your JavaFX SDK):
        ```
        --module-path "C:/path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml
        ```

### 2️ Eclipse IDE
1.  **Import the MavenProject:**
    * Go to **File** → **Import...**
    * Select **Maven** → **Existing Maven Projects**.
    * Browse to the project folder and click **Finish**.
2.  **Configure JavaFX VM Arguments:**
    * Right-click `com.comp2042.Main` → **Run As** → **Java Application**.
    * (If the run fails) Go to **Run** → **Run Configurations**.
    * Select your `Main` configuration.
    * Navigate to the **Arguments** tab.
    * In the **VM arguments** box, add:
        ```
        --module-path "C:/path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml
        ```

### 3 Visual Studio Code (VS Code)
1.  **Requirements:** Install the Microsoft **Extension Pack for Java** 
2.  **Open Project:** Open the project folder in VS Code. VS Code will automatically detect and import Maven.
3.  **Configure Launch (`launch.json`):**
    * Go to the **Run and Debug** tab → click **create a launch.json file** (if one doesn't exist).
    * Locate the configuration for `com.comp2042.Main` and add the `vmArgs` property:
    ```json
    {
        
    "type": "java",
    "name": "Run Main Class",
    "request": "launch",
    "mainClass": "com.comp2042.Main",
    "vmArgs": "--module-path \"C:/path/to/javafx-sdk/lib\" --add-modules javafx.controls,javafx.fxml,javafx.media"


    }
    ```
    
## Compile & Run

### 1. Run with Maven (Recommended)

This is the simplest and cleanest way because the JavaFX Maven Plugin handles all modules automatically.

* **Clean and Run:**
    ```bash
    mvn clean javafx:run
    ```

### 2. Run via IntelliJ IDEA

If running directly inside the IDE, the JavaFX module path must be provided as a VM Option in the run configuration.

* **Setup:** Ensure you have already configured the JavaFX SDK as a library (see [ IDE Setup](#-ide-setup-configuration)).
* **Steps:**
    1.  Go to `Run` → `Edit Configurations...`
    2.  Select the **Application** configuration for `com.comp2042.Main`.
    3.   Add VM Options:
        ```
        --module-path "C:/path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml,javafx.media
        ```

### 3. Run via Eclipse IDE

Eclipse requires the JavaFX VM arguments to be set in the run configuration's arguments tab.

* **Steps:**
    1.  Go to `Run` → `Run Configurations...`
    2.  Go to the Arguments tab
    3.  Enter into VM arguments:
        ```
        --module-path "C:/path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml,javafx.media
        ```

### 4. Run via Visual Studio Code (VS Code)

VS Code uses the `launch.json` file to manage run configurations, where the JavaFX arguments must be defined under `vmArgs`.

* **Steps:**
    1.  Open .vscode/launch.json
    2.  Ensure VM args are included:
        ```json
        "vmArgs" : "--module-path \"C:/path/to/javafx-sdk/lib\" --add-modules javafx.controls,javafx.fxml,javafx.media"
        ```
## Project Structure

##  Features Summary

Below is a clear, assessable breakdown of which features are implemented.

### Implemented & Working Properly

####  Core Gameplay


* **Hard drop + multiplier:** Instantly drops the brick to its ghost position and applies score multiplier.
* **Combo scoring:**  Awards extra points for clearing multiple rows consecutively.
* **Next-brick preview (x3):**  Displays the next three bricks in queue.
* **Hold system:**  Lets players store one brick for later use (with swap restrictions).
* **Pause & Resume:**  Suspends gravity, input, and timers.
* **Timer display:** Shows countdowns for Sprint and Ultra modes.
* **LeaderboardSystem:** Track high scores with persistent storage
* **Progressive Difficulty:** Speed increases with level
#### Game Modes
* **Sprint Mode**
    * **Level progression:** Player advances to the next level after clearing required rows.
    *  **Line requirements:** Each level has a target number of rows that must be cleared.
    *  **Adaptive speed:** Falling speed increases as levels go up.
    *  **Level-up notification:** Short popup animation signals level increase.
    *  **120-second timer:** Limits each level to 2 minutes.
* **Ultra Mode**
    * **Score attack:** Player tries to get the highest score before the timer expires.
    *  **Countdown timer:** 120-second global timer for the entire session.
    * **Continuous play:** No line targets; gameplay flows until timer ends.
* **Marathon Mode**
* **Continuous play:** No line targets; gameplay flows until timer ends.
*  **Adaptive speed:** Falling speed increases as levels go up.
*  **Level increase:** Level increases with every 1000 points earned.

####  UI & Rendering

*  **Ghost piece rendering:** Shows where the current brick will land.
* **Held brick renderer:** Renders the stored brick in the hold box.
*  **Preview renderer:** Draws up to 3 upcoming bricks.
* **Dynamic bindings:** Score, level, and timer update automatically in the UI.
* **Error feedback:** UI highlights invalid inputs or unexpected actions.
* **Combo pop-ups:** Temporary floating messages when combos occur.
* **Level-up pop-ups:** Animated UI indicator for each level progression.

####  Architecture
* **MVC structure:** Separates game logic, rendering, and UI event control.
* **Modular components:** Dedicated classes handle movement, ghost logic, timers.
* **Javadoc documentation:** Key classes fully documented for readability.

####  Unit Testing
* **GhostPieceManager tests:** Validates ghost landing logic for all shapes.
*  **SimpleBoard tests:** Covers row clearing, spawning, merging, and board logic.
*  **BrickMover tests:** Ensures movement rules and collision detection behave correctly.
*  **GameController tests:** High-level behaviour tested via mocks and stubs.
*  **40+ test cases:** Comprehensive coverage across major subsystems.

---


###  Not Implemented

These features were not required by coursework specifications and have not been added:


* **Online leaderboard:** No persistent data saving or global scoring comparison.
* **Themes/skins:** Default visual design only.
* **T-Spin detection:** Advanced T-spin scoring and recognition are not implemented.
* **DAS/ARR:** No fast Auto Repeat Rate or Delayed Auto Shift movement buffering.
* **Controller support:** Input is restricted to the keyboard.

##  Refactoring & Architecture Summary

### Summary of Refactoring Process

A primary goal of this project was to establish a highly modular, decoupled **Model-View-Controller (MVC)** architecture. The initial project shell provided basic UI infrastructure, but the core **Model** and **Logic** were missing. The refactoring process involved:

1.  **Extraction**: Pulling complex responsibilities (movement, ghost calculation, timing) out of the main controller.
2.  **Abstraction**: Introducing interfaces (`Board`, `InputEventListener`) to allow for dependency injection and mocking.
3.  **Decoupling**: Ensuring game logic is entirely separate from JavaFX/UI rendering.

The successful implementation of the game required the creation and integration of several core service classes, which are detailed below.

---

### 1. Newly Created Classes (Core Implementation)

These classes did **not** exist in the original project structure and were created from scratch to implement core features, services, and architectural separation.

####  Gameplay & Logic Services
* **`BrickMover`**: Encapsulates all active brick manipulation logic (left, right, rotate, drop, hold) by delegating to the `Board`. **Goal:** Removes movement responsibility from `GameController`.
* **`GhostPieceManager`**: Calculates the exact ghost landing height for the active brick. **Goal:** Isolates complex, iterative collision math, improving performance and testability.
* **`GameTimerManager`**: Central timer system managing the gravity loop, mode countdowns, and adaptive speed logic. Includes global `pause`/`resume` control. **Goal:** Provides a single point for timer control and fixes race conditions.
* **`ScoreEntry`**: Represents a single player's saved entry (name, score, mode).
* **`LeaderboardManager`**: Handles the persistence layer: saving and loading scores via file storage. **Goal:** Decouples file I/O from game logic.

#### Rendering, UI, & Navigation
* **`GameRenderer`**: A dedicated rendering utility that draws all game elements (board, active brick, ghost, preview, hold) onto the JavaFX `Canvas`. **Goal:** Removes all rendering code from `GuiController`.
* **`SceneNavigator`**: Manages all scene switching (Main Menu → Game → Leaderboard). **Goal:** Centralizes Stage transitions, preventing duplicate loading logic.
* **`MainMenuController` / `LeaderboardController`**: Dedicated UI controllers for their respective screens, handling navigation and display logic.
* **`InputController`**: Centralizes raw keyboard event handling and maps keys to abstract game actions (`MoveEvent`). **Goal:** Decouples input logic from the UI thread and prevents "sticky" input.

####  Audio
* **`SoundManager`**: A new audio service handling background music, sound effects, mute toggle, and volume. **Goal:** Modularizes audio to avoid mixing sound concerns with UI logic.

---

### 2. ✔ Classes  Heavily Modified & Extended

These classes existed but were massively refactored and extended to implement the game's core functionality:

* **`GameController`**: The "brain" of the game. Heavily extended to implement **all game modes** (Sprint/Ultra), level progression, hard drop scoring, the **combo system**, and the **hold mechanism**.
* **`GuiController`**: Refactored to focus purely on the **View**. Its new role is to bind score/level/timer properties, show notification pop-ups, and update the UI based on `ViewData`.
* **`SimpleBoard`** (Model): Updated to support the next queue, hold logic, game over check, safe merging, and score-aware line clearing.
* **`ViewData`**: Extended to include all required display information: current brick state, ghost position, held brick, and the next 3 bricks in the queue.

---

### 3.  Additional Notes: Why New Classes Were Necessary

The creation of dedicated service classes was required to achieve clean **Separation of Concerns** (SRP) and fix latent structural issues.

| New Class | Why it was required |
| :--- | :--- |
| **`BrickMover`** | Movement rules were duplicated and inconsistent. |
| **`GhostPieceManager`** | Ghost logic was too complex and iterative to embed in the `GameController`, which would violate SRP. |
| **`GameTimerManager`** | Timer code scattered across the UI caused race conditions, update failures, and complicated pause/resume logic. |
| **`SoundManager`** | Required clean, global audio management separate from UI controllers. |
| **`SceneNavigator`** | Navigation logic was messy and copied across different menu classes. |
| **`GameRenderer`** | Rendering code was mixed with `GuiController` logic, making the view component un-testable. |
| **`InputController`** | Input logic was tightly coupled to the UI thread, causing unresponsiveness and sticky input issues. |
| **`LeaderboardManager`** | Required persistent, external file I/O for score saving. |

### 4.  Bugs Fixed by the Refactor

The process of decoupling the codebase successfully eliminated several key issues present in the initial structure:

* **Hard drop double-scoring** was fixed by isolating the scoring logic into a single method in `GameController`.
* **Timer not updating properly** was solved by centralizing control in `GameTimerManager`.
* **Ghost piece too shallow / glitchy** was resolved by dedicating `GhostPieceManager` to the calculation.
* **UI freezing** was eliminated by moving long-running logic out of the `GuiController`.

###  Result of the Refactoring

The final codebase:
* **Follows MVC** with clear separation of concerns.
* Is **fully testable** (with over 40 unit tests) due to logic being decoupled from the UI.
* Is modular and highly **maintainable** and supports multiple game modes.

## Unexpected Problems & Solutions

This section documents key issues encountered during development and the solutions implemented, often as a result of the refactoring process.

| Problem | Cause | Fix Implemented |
| :--- | :--- | :--- |
| **Timer stopped working** | The JavaFX `Timeline` was being replaced without proper stopping and restarting logic, causing race conditions. | Added explicit `GameTimerManager.startGameLoop()` and robust `pause`/`resume` synchronization methods. |
| **Hard drop awarding bonus twice** | The score logic for hard drop was duplicated and executed in both the movement method and the merge method. | Logic was split into `executeHardDropMovementAndScore()` and `processHardDropPlacementCompletion()` within the `GameController`. |
| **Ghost piece off by 1 row** | The collision check loop included the surface row in its bounds evaluation. | Adjusted the collision loop in `GhostPieceManager` to break earlier, accurately reflecting the landing position. |
| **Ultra mode timer reset issue** | The visual timer in `GuiController` sometimes failed to refresh its count after a "New Game" button press in Ultra Mode. | Explicitly called `viewGuiController.resetTimer()` when a game restarts to ensure synchronization. |
| **First brick rotation unresponsive** | The GUI scene was not consistently focused on spawn, causing input events to be consumed before the brick was fully initialized. | Added `Platform.runLater(board.createNewBrick)` and a subsequent `request focus` call to ensure the event pipeline was ready. |
| **Mockito not importing** | An IntelliJ dependency indexing issue prevented the IDE from recognizing the testing libraries. | Ensured the correct `mockito-core` and `mockito-junit-jupiter` dependencies were explicitly defined in `pom.xml` and forced a Maven reload. |

---

## How to Run Tests

The project includes over 40 unit tests covering core subsystems (`BrickMover`, `SimpleBoard`, `GhostPieceManager`, `GameController`).

### Using Maven (Command Line)
Execute the following command in the project's root directory:
```bash
mvn test
```
## Author
Florence Tinevimbo Chigwida

COMP2042 Coursework — 2025

University of Nottingham Malaysia