package com.aidanvii.codechallenge.state

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class IntentToViewState(
    private val coroutineScope: CoroutineScope,
    private val dispatchIntentAsync: DispatchIntentAsync,
    viewStateProvider: ViewStateProvider,
) : DispatchIntent,
    ViewStateProvider by viewStateProvider {

    override operator fun invoke(intent: Action.Intent) {
        coroutineScope.launch {
            dispatchIntentAsync(intent)
        }
    }
}