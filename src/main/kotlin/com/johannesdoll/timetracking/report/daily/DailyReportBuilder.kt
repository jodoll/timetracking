package com.johannesdoll.timetracking.report.daily

import arrow.core.NonEmptyList
import com.johannesdoll.timetracking.ext.alignToLength
import com.johannesdoll.timetracking.model.TimeEntry
import com.johannesdoll.timetracking.model.duration
import com.johannesdoll.timetracking.report.Report
import com.johannesdoll.timetracking.report.ReportBuilder
import com.johannesdoll.timetracking.report.delegating.sequenceReport
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.Duration
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

    private fun Duration.toDecimalHours(): BigDecimal {
        val fullHours = toHours() * 100
        val fractalHour = (toMinutesPart() * 100) / 60
        return BigDecimal.valueOf(fullHours + fractalHour, 2)
    }
}

private data class DailyReportEntry(
    val dayOfWeek: DayOfWeek,
    val duration: Duration,
    val id: String,
    val key: String?,
    val description: String?,
    val comment: String?
)