package com.johannesdoll.timetracking.ext

import java.text.DecimalFormatSymbols

fun String.align(
    index: Int,
    atChar: Char = DecimalFormatSymbols.getInstance().decimalSeparator,
    paddingChar: Char = ' '
): String {
    val currentAlignment = indexOf(atChar).takeUnless { it == -1 } ?: return this
    val difference = (index - currentAlignment).coerceAtLeast(0)
    return this.padStart(difference + length, paddingChar)

}

fun String.alignToLength(
    index: Int,
    totalLength: Int,
    atChar: Char = DecimalFormatSymbols.getInstance().decimalSeparator,
    paddingChar: Char = ' ',
): String =
    align(index, atChar, paddingChar).padEnd(totalLength)