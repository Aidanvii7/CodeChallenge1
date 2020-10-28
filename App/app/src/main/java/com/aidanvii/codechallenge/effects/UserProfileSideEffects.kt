package com.aidanvii.codechallenge.effects

import com.aidanvii.codechallenge.state.Action
import com.aidanvii.codechallenge.state.ActionsProvider
import com.aidanvii.codechallenge.state.DispatchResultAsync
import kotlinx.coroutines.CoroutineScope

abstract class UserProfileSideEffects(
    actionsProvider: ActionsProvider,
    dispatchResultAsync: DispatchResultAsync,
    coroutineScope: CoroutineScope,
) : SideEffect<CoroutineScope, Action>(
    actionsProvider = actionsProvider,
    dispatchResultAsync = dispatchResultAsync,
    coroutineScope = coroutineScope,
)

