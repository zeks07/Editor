package com.zeks.jetbrains.task.console

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.zeks.jetbrains.task.file.FileSnapshot


@Composable
fun ConsolePanel(
    console: Console,
    focusRequester: FocusRequester,
) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    var input by remember { mutableStateOf("") }
    val annotatedText by remember (console.output) {
        derivedStateOf { annotateConsole(console, input, focusRequester) }
    }

    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SolidColor(Color.DarkGray))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { focusRequester.requestFocus() },
    ) {
        Box(
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScrollState)
                    .horizontalScroll(horizontalScrollState),
            ) {
                SelectionContainer {
                    Text(
                        text = annotatedText,
                        modifier = Modifier
                            .wrapContentSize(),
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                        )
                    )
                }

                BasicTextField(
                    value = input,
                    onValueChange = {
                        if (console.isRunning) input = it
                    },
                    modifier = Modifier
                        .alpha(0f)
                        .focusable()
                        .focusRequester(focusRequester)
                        .onPreviewKeyEvent { event ->
                            if (!console.isRunning) return@onPreviewKeyEvent false
                            if (event.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                            when (event.key) {
                                Key.Enter, Key.NumPadEnter -> {
                                    console.write(input)
                                    input = ""
                                }
                                Key.Backspace -> {
                                    if (input.isNotEmpty()) {
                                        input = input.dropLast(1)
                                    }
                                }
                            }
                            true
                        },
                )
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

fun annotateConsole(
    console: Console,
    input: String,
    focusRequester: FocusRequester
) = buildAnnotatedString {
    val output = console.output
    output.forEach { line ->
        withStyle(SpanStyle(color = line.color)) {
            append(line.text)
        }

        if (line is ConsoleLine.Error) {
            val file = console.fileSnapshot
            val directory = (file?.directory ?: return@forEach).replace("\\", "\\\\")
            val fileName = file.name ?: return@forEach

            val lineLength = line.text.length
            val lineOffset = length - lineLength

            val fullPathRegex = Regex("""$directory$fileName:(\d+):(\d+)""")
            val fullPathMatch = fullPathRegex.findAll(line.text)
            val nameRegex = Regex("""$fileName:(\d+)""")
            val nameMatches = nameRegex.findAll(line.text)

            if (!fullPathMatch.none()) {
                for (match in fullPathMatch) {
                    val (lineNum, colNum) = match.destructured

                    val startOffset = lineOffset + match.range.first
                    val endOffset = lineOffset + match.range.last + 1

                    val event = LinkAnnotation.Clickable(
                        tag = "FILE_POSITION",
                        linkInteractionListener = LinkInteractionListener {
                            console.fileSnapshot?.cursorPosition = FileSnapshot.CursorPosition(
                                maxOf(lineNum.toInt() - 1, 0),
                                maxOf(colNum.toInt() - 1, 0))
                            focusRequester.requestFocus()
                        }
                    )

                    addLink(event, startOffset, endOffset)
                    addStyle(SpanStyle(color = Color.Blue), startOffset, endOffset)
                }
            } else if (!nameMatches.none()) {
                for (match in nameMatches) {
                    val (lineNum) = match.destructured

                    val startOffset = lineOffset + match.range.first
                    val endOffset = lineOffset + match.range.last + 1

                    val event = LinkAnnotation.Clickable(
                        tag = "FILE_POSITION",
                        linkInteractionListener = LinkInteractionListener {
                            console.fileSnapshot?.cursorPosition = FileSnapshot.CursorPosition(maxOf(lineNum.toInt() - 1, 0), 0)
                            focusRequester.requestFocus()
                        }
                    )

                    addLink(event, startOffset, endOffset)
                    addStyle(SpanStyle(color = Color.Blue), startOffset, endOffset)
                }
            }
        }
        append("\n")
    }
    if (console.isRunning) {
        val annotatedInput = ConsoleLine.Input(input)
        withStyle(SpanStyle(color = annotatedInput.color)) {
            append(annotatedInput.text)
        }
    }
}