package com.johannesdoll.timetracking.report

import com.johannesdoll.timetracking.model.TimeEntry

interface ReportBuilder {
    fun build(entries: List<TimeEntry>): Report
}