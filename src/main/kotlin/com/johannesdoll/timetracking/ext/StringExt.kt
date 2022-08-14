/*
 * Copyright (C) 2022. Johannes Doll
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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