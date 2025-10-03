This is an Editor project built for a JetBrains internship application.

### Features

Editor pane highlights hard keywords always and soft keywords where appropriate.
New highlights are easily added by overriding appropriate methods inside the 
[Annotator](composeApp/src/jvmMain/kotlin/com/zeks/jetbrains/task/editor/Annotator.kt).

Console features color coding for standard and error output and user input. Error messages that include
paths of the currently opened file are clickable and will focus the editor pane on the corresponding line.

When a process is finished or forcefully stopped, the console will show the exit code of the process.

### Video example

[![Video example](https://img.youtube.com/vi/xZzzm27vA24/0.jpg)](https://youtu.be/xZzzm27vA24)


### Screenshots

Hard and soft keywords highlighting and compiler errors:
<img src="images/compilation_errors.png" alt="Ide with syntax highlighting and two compilation errors produced by running the open script.">

User input and runtime error:
<img src="images/input_and_runtime_error.png" alt="Ide with visible user input and runtime error">

Successful exit code:
<img src="images/successful_exit.png" alt="Ide with successful exit code">

### Build and Run on Windows

To build and run the development version on Windows, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:

  ```shell
  .\gradlew run
  ```

### Notes

This projects uses antlr and Kotlin grammar taken from:
https://github.com/Kotlin/kotlin-spec/tree/release