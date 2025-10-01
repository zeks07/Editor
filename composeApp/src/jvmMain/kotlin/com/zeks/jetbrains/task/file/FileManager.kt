package com.zeks.jetbrains.task.file

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class FileManager {
    var currentFile by mutableStateOf(FileSnapshot.empty())
        private set

    fun load(directory: String, fileName: String) {
        currentFile = FileSnapshot(directory, fileName)
    }

    fun setName(directory: String, name: String) {
        currentFile.directory = directory
        currentFile.name = name
    }
}