package com.aidanvii.codechallenge.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.liveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlin.reflect.KProperty0

@Composable
@OptIn(ExperimentalCoroutinesApi::class)
operator fun <T> StateFlow<T>.unaryPlus(): T = collectAsLiveState()

@Composable
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> StateFlow<T>.collectAsLiveState(): T =
    remember {
        liveData { collectLatest { emit(it) } }
    }.observeAsState().value ?: value


@Suppress(functionName)
@OptIn(ExperimentalCoroutinesApi::class)
inline fun <T> StateFlow(initialValue: () -> T): StateFlow<T> =
    MutableStateFlow(initialValue())

@OptIn(ExperimentalCoroutinesApi::class)
inline fun <T> KProperty0<StateFlow<T>>.mutateWith(mutate: T.() -> T) {
    val stateFlow = get() as MutableStateFlow<T>
    stateFlow.value = stateFlow.value.mutate()
}

@OptIn(ExperimentalCoroutinesApi::class)
operator fun <T> StateFlow<T>.invoke(): T = value