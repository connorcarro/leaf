# Leaf

![Leaf banner](.github/assets/leaf-banner.svg)

![Java](https://img.shields.io/badge/Java-22-2f6f5e?style=for-the-badge)
![UI](https://img.shields.io/badge/UI-Swing-daa84f?style=for-the-badge)
![Platforms](https://img.shields.io/badge/Platforms-Windows%20%7C%20macOS%20%7C%20Linux-31493f?style=for-the-badge)

Leaf is a desktop text editor built with Java Swing. It focuses on a practical editing workflow: opening and saving files, undo/redo, cut/copy/paste, find and replace, date/time insertion, basic Java keyword highlighting, font preferences, and a lightweight file tree.

## Features

- Open, edit, and save local text files.
- Save As support with safe cancel handling.
- Undo and redo through Swing's `UndoManager`.
- Cut, copy, paste, and column-selection support.
- Find and replace dialog with wraparound search behavior.
- Date and time insertion in short and long formats.
- Basic Java syntax highlighting mode.
- File manager tree for browsing opened paths.
- Font preference storage in the correct per-user config location for each OS.

## Cross-Platform Behavior

Leaf avoids hardcoded machine paths and Windows-only separators. Runtime settings are stored outside the source tree:

- Windows: `%APPDATA%\Leaf`
- macOS: `~/Library/Application Support/Leaf`
- Linux: `$XDG_CONFIG_HOME/leaf` or `~/.config/leaf`

## Requirements

- Java 22 or newer
- A desktop environment for launching the Swing UI

## Run

Compile the source into `out`, then launch the editor.

### macOS / Linux

```sh
mkdir -p out
javac --release 22 -d out $(find src -name "*.java")
java -cp out editor.Main
```

### Windows PowerShell

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$sourceFiles = Get-ChildItem -Path src -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
javac --release 22 -d out $sourceFiles
java -cp out editor.Main
```

## Project Structure

```text
src/
  converter/       Date and time formatting helpers
  editor/          Swing editor UI, settings, caret, and find/replace logic
  treeNodes/       File tree node helpers
.github/
  assets/          README banner artwork
```
