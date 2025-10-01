package com.zeks.jetbrains.task.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import com.zeks.jetbrains.task.grammar.KotlinParser
import com.zeks.jetbrains.task.grammar.KotlinParserBaseListener

class Annotator(
    private val builder: AnnotatedString.Builder,
) : KotlinParserBaseListener() {

    override fun enterImportHeader(importHeader: KotlinParser.ImportHeaderContext?) {
        val keyword = importHeader?.IMPORT() ?: return
        val start = keyword.symbol.startIndex
        val end = keyword.symbol.stopIndex

        builder.addStyle(
            SpanStyle(color = Color.Red),
            start,
            end + 1
        )
    }

    override fun enterModifier(modifier: KotlinParser.ModifierContext?) {
        val start = modifier?.start?.startIndex ?: return
        val end = modifier.stop?.stopIndex ?: return

        builder.addStyle(
            SpanStyle(color = Color.Red),
            start,
            end + 1
        )
    }

    override fun enterCatchBlock(catchBlock: KotlinParser.CatchBlockContext?) {
        val keyword = catchBlock?.CATCH() ?: return
        val start = keyword.symbol.startIndex
        val end = keyword.symbol.stopIndex

        builder.addStyle(
            SpanStyle(color = Color.Red),
            start,
            end + 1
        )
    }

}