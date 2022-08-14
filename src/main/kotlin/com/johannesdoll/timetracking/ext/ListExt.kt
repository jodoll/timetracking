package com.johannesdoll.timetracking.ext

fun List<String?>.getTextOrNull(index: Int) = getOrNull(index)?.takeIf { it.isNotBlank() }