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

package com.johannesdoll.timetracking.cli.report

import arrow.core.Invalid
import arrow.core.Validated
import arrow.core.right
import com.johannesdoll.timetracking.ext.ensure
import com.johannesdoll.timetracking.ext.orThrow
import com.johannesdoll.timetracking.lines.file.FileLineSource
import com.johannesdoll.timetracking.model.TimeEntry
import com.johannesdoll.timetracking.parser.csv.timeEntryParser
import com.johannesdoll.timetracking.parser.csv.useTimeEntries
import com.johannesdoll.timetracking.reader.csv.csvReader
import com.johannesdoll.timetracking.report.Report
import com.johannesdoll.timetracking.report.daily.DailyReportBuilder
import com.johannesdoll.timetracking.report.task.TaskReportBuilder
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import java.io.File

@OptIn(ExperimentalCli::class)
class ReportCommand : Subcommand("report", "Calculate report from timesheet") {
    private val reportBuilder by argument(ArgType.Choice(
        listOf(TaskReportBuilder(), DailyReportBuilder()),
        {
            when (it) {
                "daily" -> DailyReportBuilder()
                "task" -> TaskReportBuilder()
                else -> throw IllegalArgumentException("Unsupported report type $it")
            }
        },
        {
            when (it) {
                is DailyReportBuilder -> "daily"
                is TaskReportBuilder -> "task"
                else -> throw IllegalStateException("Unsupported report builder $it")
            }
        }
    ))
    private val path by argument(ArgType.String, "timesheet", "Path pointing to a timesheet")

    override fun execute() {
        val timeSheet = File(path).right()
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

        reportBuilder.build(entries).print()
    }

    private fun Report.print() {
        lineSequence().forEach { println(it) }
    }
}