package com.aidanvii.codechallenge.utils

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

sealed class CoroutineDispatchers {
    abstract val main: CoroutineDispatcher
    abstract val default: CoroutineDispatcher
    abstract val io: CoroutineDispatcher
    abstract val single: CoroutineDispatcher

    object Default : CoroutineDispatchers() {
        override val main: CoroutineDispatcher = Dispatchers.Main
        override val default: CoroutineDispatcher = Dispatchers.Default
        override val io: CoroutineDispatcher = Dispatchers.IO
        override val single: CoroutineDispatcher
            get() = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    }

    @VisibleForTesting
    object Synchronous : CoroutineDispatchers() {
        override val main: CoroutineDispatcher = Dispatchers.Unconfined
        override val default: CoroutineDispatcher = Dispatchers.Unconfined
        override val io: CoroutineDispatcher = Dispatchers.Unconfined
        override val single: CoroutineDispatcher = Dispatchers.Unconfined
    }
}