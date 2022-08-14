package com.johannesdoll.timetracking.report

import java.time.DayOfWeek
import java.time.Duration

internal data class DailyReportEntry(
    val dayOfWeek: DayOfWeek,
    val duration: Duration,
    val id: String,
    val key: String?,
    val description: String?,
    val comment: String?
)