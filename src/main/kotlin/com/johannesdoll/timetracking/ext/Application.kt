package com.johannesdoll.timetracking.ext

fun <T, R> IndexedValue<T>.mapValue(mapper: (IndexedValue<T>) -> R) =
    IndexedValue(this.index, mapper(this))

fun <T, R> Sequence<IndexedValue<T>>.mapIndexedValue(mapper: (IndexedValue<T>) -> R) =
    map { it.mapValue(mapper) }