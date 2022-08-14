package com.johannesdoll.timetracking.report.delegating

import com.johannesdoll.timetracking.report.Report

fun sequenceReport(sequenceBuilder: suspend SequenceScope<String>.() -> Unit): Report =
    DelegatingReport { sequence(sequenceBuilder) }