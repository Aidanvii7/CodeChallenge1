@file:OptIn(ExperimentalCoroutinesApi::class)

package com.aidanvii.codechallenge.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress(functionName)
fun <T, R> CoroutineScope.DerivedStateFlow(
    stateFlow: StateFlow<T>,
    lazyThreadSafetyMode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    deriveValue: T.() -> R,
): Lazy<StateFlow<R>> = lazy(lazyThreadSafetyMode) {
    val derivedStateFlow = MutableStateFlow(stateFlow.value.deriveValue())
    launch {
        stateFlow.collectLatest { input ->
            derivedStateFlow.value = input.deriveValue()
        }
    }
    derivedStateFlow
}

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress(functionName)
fun <T, R> DerivedStateFlow(
    stateFlow: StateFlow<T>,
    lazyThreadSafetyMode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    deriveValue: T.() -> R,
): Lazy<StateFlow<R>> = GlobalScope.DerivedStateFlow(
    stateFlow = stateFlow,
    lazyThreadSafetyMode = lazyThreadSafetyMode,
    deriveValue = deriveValue,
)

// TODO: Remove this?
@OptIn(ExperimentalCoroutinesApi::class)
@Suppress(functionName)
fun <T, R> ViewModel.DerivedStateFlow(
    stateFlow: StateFlow<T>,
    lazyThreadSafetyMode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    deriveValue: T.() -> R,
): Lazy<StateFlow<R>> = viewModelScope.DerivedStateFlow(
    stateFlow = stateFlow,
    lazyThreadSafetyMode = lazyThreadSafetyMode,
    deriveValue = deriveValue,
)