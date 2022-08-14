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

package com.johannesdoll.timetracking.reader.csv

import com.johannesdoll.timetracking.ext.mapIndexedValue
import com.johannesdoll.timetracking.lines.LineSource

class CsvReader(private val source: LineSource) : AutoCloseable {
    fun rowSequence(): Sequence<Row> = source
        .lines()
        .withIndex()
        .mapIndexedValue { it.value.trim() }
        .filterNot { it.value.startsWith("#") }
        .filterNot { it.value.isBlank() }
        .map { it.parseRow() }

    private fun IndexedValue<String>.parseRow(): Row =
        Row(index + 1, value.split(",").map { it.trim() })

    override fun close() {
        source.close()
    }
}

