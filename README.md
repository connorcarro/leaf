# text-editor
A text editor made in Java 22.

# Requirements
- Java 22 or newer

# Run
Compile the source into `out`, then launch the editor.

## macOS / Linux
```sh
mkdir -p out
javac --release 22 -d out $(find src -name "*.java")
java -cp out editor.Main
```

## Windows PowerShell
```powershell
New-Item -ItemType Directory -Force out | Out-Null
$sourceFiles = Get-ChildItem -Path src -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
javac --release 22 -d out $sourceFiles
java -cp out editor.Main
```

# CI
GitHub Actions runs a smoke-test build on Ubuntu, macOS, and Windows for every push and pull request.
