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

package com.johannesdoll.timetracking.report.task

import arrow.core.NonEmptyList
import com.johannesdoll.timetracking.ext.alignToLength
import com.johannesdoll.timetracking.model.TimeEntry
import com.johannesdoll.timetracking.model.duration
import com.johannesdoll.timetracking.report.DailyReportEntry
import com.johannesdoll.timetracking.report.Report
import com.johannesdoll.timetracking.report.ReportBuilder
import com.johannesdoll.timetracking.report.delegating.sequenceReport
import com.johannesdoll.timetracking.report.toDecimalHours
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

class TaskReportBuilder : ReportBuilder {
    override fun build(entries: List<TimeEntry>): Report = sequenceReport {
        entries.groupByTask()
            .filter { it.value.isNotEmpty() }
            .forEach { (task, entries) ->
                val decimalFormat = DecimalFormat("#0.0#")

                yield(
                    listOfNotNull(
                        task.key,
                        task.description.takeUnless { it == task.key },
                    ).joinToString(" - ")
                )
                entries.forEach { (_, entry) ->
                    val day = entry.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                    val durationInHours = entry.duration.toDecimalHours()
                    val durationText = decimalFormat.format(durationInHours).alignToLength(2, 5)
                    yield("$durationText - $day")
                }
                yield(buildString { repeat(10) { append("-") } })
                yield("")
            }
    }

    private class Task(val key: String, val description: String?) {
        override fun equals(other: Any?): Boolean =
            (other as? Task)?.key == key

        override fun hashCode(): Int =
            key.hashCode()
    }

    private fun List<TimeEntry>.groupByTask(): Map<Task, Map<DayOfWeek, DailyReportEntry>> = this
        .groupBy { it.id }
        .mapKeys { (key, value) ->
            val description = value.asSequence().mapNotNull { it.description }.firstOrNull()
            Task(key, description)
        }.mapValues { entry ->
            entry.value
                .groupBy { it.dayOfWeek }
                .mapValues { NonEmptyList.fromListUnsafe(it.value) }
                .mapValues { it.value.toReportEntry() }
        }

    private fun NonEmptyList<TimeEntry>.toReportEntry(): DailyReportEntry {
        val totalDuration = map { it.duration }.reduce { acc, duration -> acc.plus(duration) }
        return DailyReportEntry(
            dayOfWeek = head.dayOfWeek,
            duration = totalDuration,
            id = head.id,
            key = firstNotNullOfOrNull { it.key },
            description = firstNotNullOfOrNull { it.description },
            comment = firstNotNullOfOrNull { it.comment }
        )
    }
}
