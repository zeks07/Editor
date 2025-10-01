package com.zeks.jetbrains.task.editor

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.zeks.jetbrains.task.file.FileManager
import com.zeks.jetbrains.task.file.FileSnapshot

@Composable
@Preview
fun EditorPanel(
    fileManager: FileManager,
    focusRequester: FocusRequester,
) {
    var viewportWidth by remember { mutableStateOf(0) }
    var viewportHeight by remember { mutableStateOf(0) }

    var rawText by remember (fileManager.currentFile) {
        mutableStateOf(fileManager.currentFile.text)
    }

    var textFieldValue by remember (rawText) {
        val (cursorX, cursorY) = fileManager.currentFile.cursorPosition
        mutableStateOf(
            TextFieldValue(
                Highlighter.annotateText(rawText),
                TextRange(rawText.cursorOffsetFromLineCol(cursorX, cursorY))
            )
        )
    }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    var cursorOffset = remember (fileManager.currentFile.cursorPosition) {
        fileManager.currentFile.cursorPosition
    }

    LaunchedEffect(textFieldValue.text) {
        fileManager.currentFile.newText = textFieldValue.text
    }

    LaunchedEffect(fileManager.currentFile.cursorPosition.line, fileManager.currentFile.cursorPosition.column) {
        val (cursorX, cursorY) = fileManager.currentFile.cursorPosition
        textFieldValue = textFieldValue.copy(selection = TextRange(
            textFieldValue.text.cursorOffsetFromLineCol(cursorX, cursorY),
        ))
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { focusRequester.requestFocus() },
    ) {
        Box (
            modifier = Modifier.padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged {
                        viewportWidth = it.width
                        viewportHeight = it.height
                    }
                    .verticalScroll(verticalScrollState)
                    .horizontalScroll(horizontalScrollState)
            ) {
                Box {
                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = {
                            textFieldValue = it.copy(annotatedString = Highlighter.annotateText(it.text))
                        },
                        onTextLayout = {
                            val rect = it.getCursorRect(textFieldValue.selection.end)
                            cursorOffset = FileSnapshot.CursorPosition(rect.left.toInt(), rect.top.toInt())
                        },
                        modifier = Modifier
                            .wrapContentSize()
                            .focusable()
                            .focusRequester(focusRequester),
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                        ),
                        cursorBrush = SolidColor(Color.Black)
                    )

                    LaunchedEffect(cursorOffset.line, cursorOffset.column) {
                        val (cursorX, cursorY) = cursorOffset

                        if (cursorX < horizontalScrollState.value) {
                            horizontalScrollState.scrollTo(cursorX)
                        } else if (cursorX > horizontalScrollState.value + viewportWidth) {
                            horizontalScrollState.scrollTo(cursorX - viewportWidth)
                        }

                        if (cursorY < verticalScrollState.value) {
                            verticalScrollState.scrollTo(cursorY)
                        } else if (cursorY > verticalScrollState.value + viewportHeight) {
                            verticalScrollState.scrollTo(viewportHeight - cursorY)
                        }
                    }
                }
            }
        }
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(verticalScrollState),
            modifier = Modifier.align(Alignment.CenterEnd)
        )
        HorizontalScrollbar(
            adapter = rememberScrollbarAdapter(horizontalScrollState),
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
}

fun String.cursorOffsetFromLineCol(line: Int, column: Int): Int {
    val lines = lines()
    if (line >= lines.size) return length
    val lineStartOffset = lines.take(line).sumOf { it.length + 1 } // +1 for '\n'
    return minOf(lineStartOffset + column, length)
}