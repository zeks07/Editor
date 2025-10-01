package com.zeks.jetbrains.task.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zeks.jetbrains.task.console.Console
import com.zeks.jetbrains.task.file.FileManager
import java.awt.FileDialog
import java.awt.Frame

fun openFile(fileManager: FileManager) {
    val frame = Frame()
    frame.isVisible = false

    val fileDialog = FileDialog(frame, "Select a file to open", FileDialog.LOAD)
    fileDialog.isVisible = true

    val selectedFile = fileDialog.file
    val directory = fileDialog.directory

    if (selectedFile != null && directory != null) {
        fileManager.load(directory, selectedFile)
    }

    frame.dispose()
}

fun saveFile(fileManager: FileManager): Boolean {
    if (fileManager.currentFile.name == null) {
        val frame = Frame()
        frame.isVisible = false

        val fileDialog = FileDialog(frame, "Select a folder to save", FileDialog.SAVE)
        fileDialog.isVisible = true

        val selectedFile = fileDialog.file
        val directory = fileDialog.directory

        if (selectedFile != null && directory != null) {
            fileManager.setName(directory, selectedFile)
        } else {
            return false
        }
    }

    fileManager.currentFile.save()
    return true
}

fun runScript(fileManager: FileManager, console: Console) {
    if (!saveFile(fileManager)) {
        console.addInfo("File must be saved before running.")
        return
    }
    console.startProcess(fileManager.currentFile)
}

fun stopProcess(console: Console) {
    console.stopProcess()
}

@Composable
fun Toolbar(
    fileManager: FileManager,
    console: Console,
) {
    Button(
        onClick = { openFile(fileManager) },
        modifier = Modifier
            .padding(2.dp),
    ) {
        Text("Load file")
    }
    Button(
        onClick = { saveFile(fileManager) },
        modifier = Modifier
            .padding(2.dp)
    ) {
        Text("Save file")
    }

    if (console.isRunning) {
        Button(
            onClick = { stopProcess(console) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
            ),
            modifier = Modifier
                .padding(2.dp),
        ) {
            Text("Stop")
        }
    } else {
        Button(
            onClick = { runScript(fileManager, console) },
            modifier = Modifier
                .padding(2.dp),
        ) {
            Text("Run")
        }
    }
}