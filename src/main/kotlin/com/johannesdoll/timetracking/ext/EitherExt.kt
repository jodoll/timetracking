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

import arrow.core.Either
import arrow.core.left

fun <A, B> Either<A, B>.ensure(onError: (B) -> A, test: (B) -> Boolean): Either<A, B> = when (this) {
    is Either.Left -> this
    is Either.Right -> if (test(value)) this else onError(value).left()
}

fun <A, B> Either<A, B>.orThrow(onError: (A) -> Throwable): B = when (this) {
    is Either.Left -> throw onError(value)
    is Either.Right -> value
}