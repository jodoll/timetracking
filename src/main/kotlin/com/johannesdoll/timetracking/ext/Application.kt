/*
 * Copyright (C) 2022. Johannes Doll
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.johannesdoll.timetracking.ext

fun <T, R> IndexedValue<T>.mapValue(mapper: (IndexedValue<T>) -> R) =
    IndexedValue(this.index, mapper(this))

fun <T, R> Sequence<IndexedValue<T>>.mapIndexedValue(mapper: (IndexedValue<T>) -> R) =
    map { it.mapValue(mapper) }