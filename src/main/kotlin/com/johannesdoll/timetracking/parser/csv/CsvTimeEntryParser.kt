package com.johannesdoll.timetracking.parser.csv

import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.validNel
import arrow.core.zip
import com.johannesdoll.timetracking.ext.getTextOrNull
import com.johannesdoll.timetracking.model.TimeEntry
import com.johannesdoll.timetracking.reader.csv.CsvReader
import com.johannesdoll.timetracking.reader.csv.Row
import java.lang.IllegalStateException
import java.time.DateTimeException
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CsvTimeEntryParser(private val reader: CsvReader): AutoCloseable{

    fun timeEntrySequence(): Sequence<ValidatedNel<String, TimeEntry>> = reader
        .rowSequence()
        .toTimeEntries()

    private fun Sequence<Row>.toTimeEntries() = map { it.toTimeEntry() }

    private fun Row.toTimeEntry(): ValidatedNel<String, TimeEntry> {
        if (cells.size < 4) "Not enough values in Row $number. At least 4 are needed.".invalidNel()
        val day = get(0).parseDayOfWeek().atLine(number)
        val start = get(1).parseLocalTime().atLine(number)
        val end = get(2).parseLocalTime().atLine(number)

        val labels = listOfNotNull(get(3), getOrNull(4))
            .firstNotNullOfOrNull { it.takeUnless { it.isBlank() } }
            ?.validNel()
            ?: "Either key or description need to be provided".invalidNel()

        return labels.zip(day, start, end) { validatedLabel, validatedDay, validatedStart, validatedEnd ->
            TimeEntry(
                dayOfWeek = validatedDay,
                start = validatedStart,
                end = validatedEnd,
                id = validatedLabel,
                key = getTextOrNull(3),
                description = getTextOrNull(4),
                comment = getTextOrNull(5)
            )
        }
    }
    private fun <T> ValidatedNel<String, T>.atLine(number: Int) =
        mapLeft { it.map { "Error at line $number: $it" } }


    private fun String.parseLocalTime(): ValidatedNel<String, LocalTime> {
        val timePattern = DateTimeFormatter.ofPattern("Hmm")
        return try {
            LocalTime.parse(this, timePattern).validNel()
        } catch (e: DateTimeException) {
            "Invalid time $this: ${e.message}".invalidNel()
        }
    }

    private fun String.parseDayOfWeek(): ValidatedNel<String, DayOfWeek> =
        minLength(3).zip(
            maxLength(3),
            oneOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        ) { _, _, _ ->
            when (this) {
                "Mon" -> DayOfWeek.MONDAY
                "Tue" -> DayOfWeek.TUESDAY
                "Wed" -> DayOfWeek.WEDNESDAY
                "Thu" -> DayOfWeek.THURSDAY
                "Fri" -> DayOfWeek.FRIDAY
                "Sat" -> DayOfWeek.SATURDAY
                "Sun" -> DayOfWeek.SUNDAY
                else -> throw IllegalStateException("Unknown Day of week $this")
            }
        }

    private fun String.minLength(chars: Int): ValidatedNel<String, String> =
        if (length >= chars) validNel()
        else "'$this' needs to be shorter than $chars".invalidNel()

    private fun String.maxLength(chars: Int): ValidatedNel<String, String> =
        if (length <= chars) validNel()
        else "'$this' needs to be longer than $chars".invalidNel()

    private fun String.oneOf(vararg selection: String): ValidatedNel<String, String> =
        if (selection.contains(this)) validNel()
        else "'$this' needs to be one of $selection".invalidNel()

    override fun close() {
        reader.close()
    }
}

inline fun <T> CsvTimeEntryParser.useTimeEntries(
    crossinline block: (Sequence<ValidatedNel<String, TimeEntry>>) -> T
) = use { block(timeEntrySequence()) }
