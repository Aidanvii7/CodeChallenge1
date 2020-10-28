package com.aidanvii.codechallenge.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
fun CharSequence?.isNotNullAndNotEmpty(): Boolean {
    contract { returns(true) implies (this@isNotNullAndNotEmpty != null) }
    return this != null && this.isNotEmpty()
}