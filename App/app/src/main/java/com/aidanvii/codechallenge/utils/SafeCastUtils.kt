package com.aidanvii.codechallenge.utils

inline fun <reified T> Any?.safeCast(): T? = this as? T