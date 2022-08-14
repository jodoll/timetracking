package com.johannesdoll.timetracking.model

import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime

data class TimeEntry(
    val dayOfWeek: DayOfWeek,
    val start: LocalTime,
    val end: LocalTime,
    val id: String,
    val key: String?,
    val description: String?,
    val comment: String?
)

val TimeEntry.duration: Duration
    get() = Duration.between(start, end)