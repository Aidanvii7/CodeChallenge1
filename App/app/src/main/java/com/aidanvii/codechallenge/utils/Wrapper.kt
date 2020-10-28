package com.aidanvii.codechallenge.utils

import java.io.Closeable

interface Wrapper<T> {
    fun unwrap(): T
}

inline fun <T, W, R> T.useUnwrapped(block: (W) -> R): R
        where T : Closeable,
              T : Wrapper<W> = use { block(unwrap()) }