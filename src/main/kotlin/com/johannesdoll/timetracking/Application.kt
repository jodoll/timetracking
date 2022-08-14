package com.johannesdoll.timetracking

import arrow.core.Invalid
import arrow.core.Validated
import arrow.core.toOption
import com.johannesdoll.timetracking.ext.ensure
import com.johannesdoll.timetracking.ext.orThrow
import com.johannesdoll.timetracking.lines.file.FileLineSource
import com.johannesdoll.timetracking.model.TimeEntry
import com.johannesdoll.timetracking.parser.csv.timeEntryParser
import com.johannesdoll.timetracking.parser.csv.useTimeEntries
import com.johannesdoll.timetracking.reader.csv.csvReader
import com.johannesdoll.timetracking.report.Report
import com.johannesdoll.timetracking.report.daily.DailyReportBuilder
import java.io.File
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    val timeSheet = args.firstOrNull().toOption()
        .toEither { "Path to time sheet must be provided" }
        .map { File(it) }
        .ensure({ "Time sheet file not exist: ${it.absolutePath}" }, { it.exists() })
        .ensure({ "Time sheet is not a file" }, { it.isFile })
        .orThrow { IllegalArgumentException(it) }

    val parsedResults = FileLineSource(timeSheet)
        .csvReader()
        .timeEntryParser()
        .useTimeEntries { it.toList() }

    val errors = parsedResults.filterIsInstance<Invalid<*>>()
    if (errors.isNotEmpty()) {
        errors.forEach { println(it.value) }
        throw IllegalArgumentException("Time sheet contains errors. Please consult the logs and fix them.")
    }

    val entries = parsedResults
        .filterIsInstance<Validated.Valid<TimeEntry>>()
        .map { it.value }


    DailyReportBuilder().build(entries).print()
}


fun Report.print(){
    lineSequence().forEach { println(it) }
}
