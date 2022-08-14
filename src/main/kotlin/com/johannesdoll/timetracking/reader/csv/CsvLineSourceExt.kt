package com.johannesdoll.timetracking.reader.csv

import com.johannesdoll.timetracking.lines.LineSource

fun LineSource.csvReader() = CsvReader(this)