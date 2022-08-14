package com.johannesdoll.timetracking.report.delegating

import com.johannesdoll.timetracking.report.Report

class DelegatingReport(private val provideReport: () -> Sequence<String>): Report {
    override fun lineSequence(): Sequence<String> = provideReport()
}