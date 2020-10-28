package com.aidanvii.codechallenge.state

import com.aidanvii.codechallenge.utils.CoroutineDispatchers
import com.aidanvii.codechallenge.utils.DerivedStateFlow
import com.aidanvii.codechallenge.utils.StateFlow
import com.aidanvii.codechallenge.utils.mutateWith
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class AppStateProcessor(
    initialAppState: AppState,
    coroutineDispatchers: CoroutineDispatchers,
) : DispatchIntentAsync,
    DispatchResultAsync,
    ViewStateProvider,
    ActionsProvider {

    private val dispatcher = coroutineDispatchers.single
    private val appState = StateFlow { initialAppState }
    override val viewState by DerivedStateFlow(appState) { viewState }

    private val _actions = MutableSharedFlow<Action>(
        extraBufferCapacity = 5,
    )
    override val actions: Flow<Action>
        get() = _actions

    override suspend operator fun invoke(intent: Action.Intent) {
        updateWith(intent as Action)
    }

    override suspend operator fun invoke(result: Action.Result) {
        updateWith(result as Action)
    }

    private suspend fun updateWith(action: Action) {
        withContext(dispatcher) {
            ::appState.mutateWith { reduceWith(action) }
        }
        _actions.emit(action)
    }
}