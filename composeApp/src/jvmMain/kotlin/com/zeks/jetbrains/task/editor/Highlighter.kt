package com.zeks.jetbrains.task.editor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import com.zeks.jetbrains.task.grammar.KotlinLexer
import com.zeks.jetbrains.task.grammar.KotlinParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeWalker

object Highlighter {
    fun annotateText(text: String): AnnotatedString {
        val stream = CharStreams.fromString(text)
        val lexer = KotlinLexer(stream)
        val tokens = CommonTokenStream(lexer)
        tokens.fill()

        val builder = AnnotatedString.Builder()

        for (token in tokens.tokens) {
            if (token.type == KotlinLexer.EOF) break
            when (token.type) {
                in KotlinLanguage.hardKeywords -> {
                    builder.withStyle(SpanStyle( color = Color.Red)) {
                        append(token.text)
                    }
                }
                else -> builder.append(token.text)
            }
        }

        val parser = KotlinParser(tokens)
        parser.removeErrorListeners()

        val tree = parser.script()

        val walker = ParseTreeWalker()
        walker.walk(Annotator(builder), tree)

        return builder.toAnnotatedString()
    }
}