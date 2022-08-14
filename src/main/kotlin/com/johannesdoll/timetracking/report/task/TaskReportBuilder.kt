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

                yield(listOfNotNull(task.key, task.comment).joinToString(" - "))
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

    private class Task(val key: String, val comment: String?) {
        override fun equals(other: Any?): Boolean =
            (other as? Task)?.key == key

        override fun hashCode(): Int =
            key.hashCode()
    }

    private fun List<TimeEntry>.groupByTask(): Map<Task, Map<DayOfWeek, DailyReportEntry>> = this
        .groupBy { Task(it.id, it.comment) }
        .mapValues { entry ->
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
