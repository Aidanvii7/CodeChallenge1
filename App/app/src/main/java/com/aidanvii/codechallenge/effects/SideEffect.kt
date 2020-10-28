package com.aidanvii.codechallenge.effects

import com.aidanvii.codechallenge.state.Action
import com.aidanvii.codechallenge.state.ActionsProvider
import com.aidanvii.codechallenge.state.DispatchResultAsync
import com.aidanvii.codechallenge.utils.cancelOnReassign
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
abstract class SideEffect<CoroutineScopeType : CoroutineScope, ActionType : Action>(
    private val actionsProvider: ActionsProvider,
    protected val dispatchResultAsync: DispatchResultAsync,
    protected val coroutineScope: CoroutineScopeType,
) {

    private var observeActionsJob by cancelOnReassign()

    fun start() {
        observeActionsJob = coroutineScope.launch {
            actionsProvider.actions
                .map { transform(it) }
                .filterNotNull()
                .collect { handle(it) }
        }
    }

    fun stop() {
        observeActionsJob = null
    }

    abstract fun transform(action: Action): ActionType?

    abstract suspend fun handle(action: ActionType)
}