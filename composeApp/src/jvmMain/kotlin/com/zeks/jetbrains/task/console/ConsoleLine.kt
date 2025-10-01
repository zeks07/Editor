package com.zeks.jetbrains.task.console

import androidx.compose.ui.graphics.Color

sealed class ConsoleLine(val text: String) {
    open val color = Color.White
    class Output(output: String) : ConsoleLine(output)
    class Error(error: String) : ConsoleLine(error) {
        override val color = Color.Red
    }
    class Input(input: String) : ConsoleLine(input) {
        override val color = Color.Green
    }
}