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
