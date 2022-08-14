package com.johannesdoll.timetracking.lines

interface LineSource : AutoCloseable {
    fun lines(): Sequence<String>
}