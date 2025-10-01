package com.zeks.jetbrains.task.editor

import com.zeks.jetbrains.task.grammar.KotlinLexer

object KotlinLanguage {
    val hardKeywords = listOf(
        KotlinLexer.AS,
        KotlinLexer.AS_SAFE,
        KotlinLexer.BooleanLiteral,
        KotlinLexer.BREAK,
        KotlinLexer.CLASS,
        KotlinLexer.CONTINUE,
        KotlinLexer.DO,
        KotlinLexer.ELSE,
        KotlinLexer.FOR,
        KotlinLexer.FUN,
        KotlinLexer.IF,
        KotlinLexer.IN,
        KotlinLexer.NOT_IN,
        KotlinLexer.IS,
        KotlinLexer.NOT_IS,
        KotlinLexer.NullLiteral,
        KotlinLexer.OBJECT,
        KotlinLexer.PACKAGE,
        KotlinLexer.RETURN,
        KotlinLexer.SUPER,
        KotlinLexer.THIS,
        KotlinLexer.THROW,
        KotlinLexer.TRY,
        KotlinLexer.TYPE_ALIAS,
        KotlinLexer.TYPEOF,
        KotlinLexer.VAL,
        KotlinLexer.VAR,
        KotlinLexer.WHEN,
        KotlinLexer.WHILE,
    )
}