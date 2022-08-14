package com.johannesdoll.timetracking.report

import java.math.BigDecimal
import java.time.Duration

internal fun Duration.toDecimalHours(): BigDecimal {
    val fullHours = toHours() * 100
    val fractalHour = (toMinutesPart() * 100) / 60
    return BigDecimal.valueOf(fullHours + fractalHour, 2)
}