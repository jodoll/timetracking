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

package com.johannesdoll.timetracking.report.daily

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

class DailyReportBuilder: ReportBuilder {
    override fun build(entries: List<TimeEntry>): Report = sequenceReport {
        entries.groupByDay()
            .filter { it.value.isNotEmpty() }
            .forEach { (day, entries) ->
                val decimalFormat = DecimalFormat("#0.0#")

                yield(day.getDisplayName(TextStyle.FULL, Locale.getDefault()))
                entries.forEach { (_, entry) ->
                    val durationInHours = entry.duration.toDecimalHours()
                    val label = listOfNotNull(entry.key, entry.description, entry.comment).joinToString(" - ")
                    val durationText = decimalFormat.format(durationInHours).alignToLength(2, 5)
                    yield("$durationText - $label")
                }
                yield(buildString { repeat(10) { append("-") } })

                val totalDuration = entries.map { it.value.duration }
                    .reduce { acc, duration -> acc.plus(duration) }
                    .toDecimalHours()
                val totalDurationText = decimalFormat.format(totalDuration).alignToLength(2, 5)
                yield("$totalDurationText - Total")
                yield("")
            }
    }

    private fun List<TimeEntry>.groupByDay(): Map<DayOfWeek, Map<String, DailyReportEntry>> = this
        .groupBy { it.dayOfWeek }
        .mapValues { entry ->
            entry.value
                .groupBy { it.id }
                .mapValues { NonEmptyList.fromListUnsafe(it.value) }
                .mapValues { it.value.toDailyReportEntry() }
        }

    private fun NonEmptyList<TimeEntry>.toDailyReportEntry(): DailyReportEntry {
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

