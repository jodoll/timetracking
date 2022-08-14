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