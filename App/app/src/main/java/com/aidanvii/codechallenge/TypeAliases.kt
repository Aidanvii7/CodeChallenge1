package com.aidanvii.codechallenge

import androidx.compose.runtime.Composable

typealias ComposableFunction = @Composable () -> Unit
typealias Action0 = () -> Unit // TODO: find a better name (clashes with Action from state package)
typealias Provider<T> = () -> T
typealias Consumer<T> = (T) -> Unit