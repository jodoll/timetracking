package com.johannesdoll.timetracking.report

interface Report {
    fun lineSequence(): Sequence<String>
}