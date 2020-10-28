package com.aidanvii.codechallenge.state

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class)
interface ViewStateProvider {
    val viewState: StateFlow<ViewState>
}