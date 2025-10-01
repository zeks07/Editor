package com.zeks.jetbrains.task

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Editor",
    ) {
        App()
    }
}