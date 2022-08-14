package com.johannesdoll.timetracking.lines.file

import com.johannesdoll.timetracking.lines.LineSource
import java.io.File

class FileLineSource(private val file : File): LineSource {

    private val reader by lazy { file.bufferedReader() }

    override fun lines(): Sequence<String> = reader.lineSequence()

    override fun close() = reader.close()
}