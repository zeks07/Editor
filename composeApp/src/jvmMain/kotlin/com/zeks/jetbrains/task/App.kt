package com.zeks.jetbrains.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

import com.zeks.jetbrains.task.console.ConsolePanel
import com.zeks.jetbrains.task.console.Console
import com.zeks.jetbrains.task.editor.EditorPanel
import com.zeks.jetbrains.task.file.FileManager
import com.zeks.jetbrains.task.toolbar.Toolbar

@Composable
@Preview
fun App() {
    val fileManager = remember { FileManager() }
    val console = remember { Console() }

    val focusRequester = remember { FocusRequester() }

    MaterialTheme {
        Column (
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Toolbar(fileManager, console)
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    EditorPanel(
                        fileManager,
                        focusRequester,
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    ConsolePanel(
                        console,
                        focusRequester,
                    )
                }
            }
        }
    }
}