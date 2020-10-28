package com.aidanvii.codechallenge.domain

import com.aidanvii.codechallenge.utils.Wrapper

sealed class Either<SuccessType : Any, FailureType : Any>

data class Success<SuccessType : Any, FailureType : Any>(
    private val result: SuccessType,
) : Either<SuccessType, FailureType>(),
    Wrapper<SuccessType> {
    override fun unwrap() = result
}

data class Failure<SuccessType : Any, FailureType : Any>(
    private val result: FailureType,
) : Either<SuccessType, FailureType>(),
    Wrapper<FailureType> {
    override fun unwrap() = result
}

fun <SuccessType : Any, FailureType : Any> SuccessType.toSuccess() = Success<SuccessType, FailureType>(this)
fun <SuccessType : Any, FailureType : Any> FailureType.toFailure() = Failure<SuccessType, FailureType>(this)