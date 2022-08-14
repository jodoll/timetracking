package com.johannesdoll.timetracking.parser.csv

import com.johannesdoll.timetracking.reader.csv.CsvReader

fun CsvReader.timeEntryParser() = CsvTimeEntryParser(this)