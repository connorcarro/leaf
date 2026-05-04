<p align="center">
  <img src=".github/assets/leaf-banner.svg" alt="Leaf banner">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-8%2B-2f6f5e?style=for-the-badge" alt="Java 8+">
  <img src="https://img.shields.io/badge/UI-Swing-daa84f?style=for-the-badge" alt="Swing UI">
  <img src="https://img.shields.io/badge/Platforms-Windows%20%7C%20macOS%20%7C%20Linux-31493f?style=for-the-badge" alt="Windows, macOS, and Linux">
</p>

<h1 align="center">Leaf</h1>

Leaf is a lightweight desktop text editor built with Java Swing. It has basic local editing features such as opening and saving files, find and replace, date/time insertion, font preferences, line and column status, column selection, a file manager tree, and simple Java keyword highlighting.

I originally made Leaf years ago while learning Java and left it sitting outside GitHub for a long time. Building a desktop editor was a practical way to learn Swing, file I/O, event handling, user preferences, text documents, and cross-platform behavior.

Leaf is in no way a production-ready editor, or anything near it. It is an old learning project that has been cleaned up enough to build, run, test, and share publicly.

## Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Controls](#controls)
- [How It Works](#how-it-works)
- [Development](#development)
- [Releases](#releases)
- [Project Structure](#project-structure)
- [Troubleshooting](#troubleshooting)
- [License](#license)

## Features

- Open, edit, and save local text files.
- Create a new untitled document from the File menu.
- Save new files with Save As and save existing files in place.
- Prompt before discarding unsaved changes when opening, creating, or exiting.
- Undo and redo document edits through Swing's `UndoManager`.
- Cut, copy, and paste through the system clipboard.
- Search text with a Find / Replace dialog.
- Replace the current match from the find dialog.
- Insert formatted date and time values in short or long formats.
- Toggle a file manager panel for browsing files from an opened path.
- Open files from the file manager tree.
- Track cursor position with a status bar that shows line and column.
- Show selected character count in the status bar.
- Change editor font family, style, and size.
- Persist font settings in the correct per-user config folder for the operating system.
- Switch between normal text mode and Java highlighting mode.
- Highlight common Java keywords while editing in Java mode.
- Use start/end selection for selecting text between two caret positions.
- Use column selection mode for rectangular text selections.
- Use Swing's cross-platform look and feel.

## Requirements

- Java 8 or newer.
- A desktop operating system with a graphical environment.
- A terminal or command prompt for launching the release JAR or compiling from source.

Leaf is a Swing desktop application, so it needs a GUI session to run normally. Headless environments can compile the project and run self-checks, but they cannot display the editor window.

### Check Java

Run:

```sh
java -version
javac -version
```

Both commands should resolve to Java 8 or newer. If your system has multiple Java versions installed, make sure `java` and `javac` point to the same JDK when building from source.

## Installation

The easiest way to run Leaf is to download the release JAR. You can also clone the repository, compile the Java files into an `out` directory, then launch the main class.

### Option 1: Run The Release JAR

Download `leaf.jar` from the [latest GitHub release](https://github.com/connorcarro/leaf/releases/latest), then run:

```sh
java -jar leaf.jar
```

### Option 2: Build From Source

#### 1. Clone The Repository

```sh
git clone https://github.com/connorcarro/leaf.git
cd leaf
```

If you downloaded the ZIP from GitHub instead, extract it and open a terminal in the extracted project folder.

#### 2. Compile

##### macOS / Linux

```sh
mkdir -p out
javac -source 8 -target 8 -d out $(find src -name "*.java")
```

##### Windows PowerShell

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$sourceFiles = Get-ChildItem -Path src -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
javac -source 8 -target 8 -d out $sourceFiles
```

#### 3. Run

##### macOS / Linux

```sh
java -cp out editor.Main
```

##### Windows PowerShell

```powershell
java -cp out editor.Main
```

### Optional: Compile With Tests

The repository includes smoke tests for the core non-visual behavior.

#### macOS / Linux

```sh
mkdir -p out
javac -source 8 -target 8 -d out $(find src tests -name "*.java")
java -cp out editor.Main --self-test
java -cp out ci.SmokeTests
```

#### Windows PowerShell

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$sourceFiles = Get-ChildItem -Path src, tests -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
javac -source 8 -target 8 -d out $sourceFiles
java -cp out editor.Main --self-test
java -cp out ci.SmokeTests
```

## Usage

Launch Leaf with `java -cp out editor.Main`. The editor opens with an untitled document.

Use the File menu to create, open, save, save as, or exit. When a document has unsaved changes, Leaf marks the window title and asks whether to save before actions that would replace or close the current document.

Use the Edit menu for undo, redo, clipboard actions, find and replace, date/time insertion, start/end selection, and column selection mode. Use the View menu to show or hide the file manager. Use the Format menu to change the font or switch between normal text and Java highlighting.

Font preferences are saved outside the repository:

- Windows: `%APPDATA%\Leaf`
- macOS: `~/Library/Application Support/Leaf`
- Linux: `$XDG_CONFIG_HOME/leaf` or `~/.config/leaf`

## Controls

### Keyboard Shortcuts

| Shortcut | Action |
| --- | --- |
| `Ctrl+S` | Save |
| `Ctrl+Shift+S` | Save As |
| `Ctrl+Z` | Undo |
| `Ctrl+Shift+Z` | Redo |
| `Ctrl+F` | Open Find / Replace |

### Menu Controls

| Menu | Actions |
| --- | --- |
| File | New, Open, Save, Save As, Exit |
| Edit | Undo, Redo, Cut, Copy, Paste, Find, Start/End Select, Select in Columns Mode, Insert Date and Time |
| View | Show File Manager |
| Format | Font, Normal Text File, Java |

### Selection Modes

Start/End Select lets you choose a starting caret position, move somewhere else, and select the text between those two positions.

Select in Columns Mode switches the editor to a custom caret that supports rectangular selections across multiple lines. Copying in this mode collects the selected text from each highlighted row.

## How It Works

Leaf is organized around a Swing `JFrame` containing a `JTextPane`, menu bar, optional file tree, and status bar.

- `editor.Main` starts the application and provides a small self-test mode.
- `editor.TextEdit` builds the main window, menus, editor pane, file handling, status updates, font dialog, and formatting behavior.
- `editor.Find` provides the Find / Replace window and search logic.
- `editor.Settings` loads and saves font preferences in an operating-system-specific config directory.
- `editor.ColumnSelectCaret` implements rectangular selection behavior.
- `converter.TimeFormat`, `converter.DayConverter`, and `converter.MonthConverter` format inserted date and time values.
- `treeNodes.FileNode` and `treeNodes.CreateChildNodes` build the file manager tree.

Java highlighting is implemented with a styled document. When Java mode is enabled, common Java keywords are detected and styled while text is inserted.

## Development

Use the same compile command from the installation section after making source changes:

```sh
javac -source 8 -target 8 -d out $(find src -name "*.java")
```

For a broader local check, compile the source and tests together and run both smoke checks:

```sh
javac -source 8 -target 8 -d out $(find src tests -name "*.java")
java -cp out editor.Main --self-test
java -cp out ci.SmokeTests
```

On Windows PowerShell:

```powershell
$sourceFiles = Get-ChildItem -Path src, tests -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
javac -source 8 -target 8 -d out $sourceFiles
java -cp out editor.Main --self-test
java -cp out ci.SmokeTests
```

To check that the GUI startup path constructs successfully, run this from a desktop session:

```sh
java -cp out editor.Main --startup-test
```

Development notes:

- Keep source files under `src`.
- Keep test-only checks under `tests`.
- Do not commit compiled `.class` files or generated `out` / `build` directories.
- Keep the app Java 8-compatible so the release JAR works with older desktop Java installs.
- Keep user settings outside the source tree.

## Releases

GitHub Actions builds the runnable `leaf.jar` release artifact. To publish a release, push a version tag:

```sh
git tag v1.0.0
git push origin v1.0.0
```

The release workflow compiles the app, runs the smoke tests, packages `leaf.jar`, verifies `java -jar leaf.jar --self-test`, runs `java -jar leaf.jar --startup-test` under a virtual display, and attaches the JAR to the GitHub release.

## Project Structure

```text
src/
  converter/
    DayConverter.java       Day name formatting helper
    MonthConverter.java     Month name formatting helper
    TimeFormat.java         Short and long date/time formatting
  editor/
    ColumnSelectCaret.java  Rectangular selection caret
    Find.java               Find / Replace dialog and search logic
    Main.java               Application entry point
    Settings.java           Font preference storage
    TextEdit.java           Main Swing editor window
  treeNodes/
    CreateChildNodes.java   File tree loading worker
    FileNode.java           File tree display node
tests/
  ci/
    SmokeTests.java         Non-visual smoke tests
.github/
  assets/
    leaf-banner.svg         README banner artwork
```

## Troubleshooting

### `javac: invalid flag: --release`

Leaf no longer uses `--release` in the documented build commands. Use the current README commands, or make sure `javac -version` reports Java 8 or newer.

### `A Java Exception Has Occurred`

Your computer may be opening the JAR with an older Java runtime than the one you use in the terminal. The release JAR is built for Java 8 or newer so it can be launched by common Windows `.jar` file associations.

### `Could not find or load main class editor.Main`

The project has not been compiled into `out`, or the compile step failed. Run the compile command again and check for errors before launching.

### The Editor Window Does Not Open

Make sure you are running in a desktop session, not a headless terminal or server environment. Swing needs access to a graphical display.

### Font Settings Do Not Save

Check that your user account can write to the config directory for your operating system. Leaf stores settings in the user config folder, not in the project directory.

### File Manager Does Not Show Files Immediately

The file tree is populated from the selected path. Open a file first, then enable Show File Manager from the View menu.

## License

Leaf is released under the MIT License. See [LICENSE](LICENSE).
