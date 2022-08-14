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

