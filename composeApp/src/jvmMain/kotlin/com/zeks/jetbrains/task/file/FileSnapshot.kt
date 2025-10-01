package com.zeks.jetbrains.task.file

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.io.File

class FileSnapshot(var directory: String?, var name: String?) {
    data class CursorPosition(val line: Int, val column: Int)

    var cursorPosition by mutableStateOf(CursorPosition(0, 0))

    val text: String = if (directory == null || name == null) {
        """
            fun main() {
                print("Hello World!")
            }
        """.trimIndent()
    } else {
        File("$directory$name").readText()
    }

    var newText: String = text

    fun save() {
        if (name == null) return
        File("$directory$name").writeText(newText)
    }

    companion object {
        fun empty() = FileSnapshot(null, null)
    }
}