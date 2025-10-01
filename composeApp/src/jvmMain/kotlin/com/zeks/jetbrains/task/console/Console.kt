package com.zeks.jetbrains.task.console

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import com.zeks.jetbrains.task.file.FileSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class Console() {
    private var processScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    var process: Process? by mutableStateOf(null)
    var fileSnapshot: FileSnapshot? by mutableStateOf(null)
    var isRunning by mutableStateOf(false)
    private var isForciblyStopped = false

    val output = mutableStateListOf<ConsoleLine>()

    fun startProcess(fileSnapshot: FileSnapshot) {
        if (process?.isAlive == true) return

        this.fileSnapshot = fileSnapshot
        val path = "${fileSnapshot.directory}\\${fileSnapshot.name}"

        output.clear()
        output.add(ConsoleLine.Output("Executing 'run'..."))

        process = ProcessBuilder("cmd.exe", "/c", "kotlinc", "-script", path)
            .redirectErrorStream(false)
            .start()

        isRunning = true
        isForciblyStopped = false

        val stdReader = processScope.launch(Dispatchers.IO) {
            val reader = process?.inputStream?.bufferedReader() ?: return@launch
            try {
                while (!isForciblyStopped) {
                    val line = try {
                        reader.readLine() ?: break
                    } catch (_: IOException) {
                        break
                    }
                    withContext(Dispatchers.Main) {
                        if (!isForciblyStopped) output.add(ConsoleLine.Output(line))
                    }
                }
            } finally {
                withContext(Dispatchers.Main) {
                    process = null
                }
            }
        }

        val errReader = processScope.launch(Dispatchers.IO) {
            val reader = process?.errorStream?.bufferedReader() ?: return@launch
            try {
                while (!isForciblyStopped) {
                    val line = try {
                        reader.readLine() ?: break
                    } catch (_: IOException) {
                        break
                    }
                    withContext(Dispatchers.Main) {
                        if (!isForciblyStopped) output.add(ConsoleLine.Error(line))
                    }
                }
            } finally {
                reader.close()
            }
        }

        processScope.launch(Dispatchers.IO) {
            val exitCode = try {
                process?.waitFor() ?: -1
            } catch (_: InterruptedException) {
                -1
            }

            stdReader.join()
            errReader.join()

            withContext(Dispatchers.Main) {
                output.add(ConsoleLine.Output("\nProcess finished with exit code $exitCode"))
                isRunning = false
            }
        }
    }

    fun write(input: String) {
        processScope.launch(Dispatchers.IO) {
            if (!isRunning || isForciblyStopped) return@launch
            val writer = process?.outputStream?.bufferedWriter() ?: return@launch
            try {
                writer.write(input)
                writer.newLine()
                writer.flush()
                withContext(Dispatchers.Main) {
                    output.add(ConsoleLine.Input(input))
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun stopProcess() {
        isForciblyStopped = true
        process?.outputStream?.close()
        process?.toHandle()?.descendants()?.forEach { it.destroyForcibly() }
        process?.destroyForcibly()
    }

    fun addInfo(info: String) {
        output.add(ConsoleLine.Output(info))
    }
}