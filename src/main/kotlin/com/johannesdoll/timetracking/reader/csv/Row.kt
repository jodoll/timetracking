package com.johannesdoll.timetracking.reader.csv

data class Row(val number: Int, val cells: List<String>) : List<String> by cells