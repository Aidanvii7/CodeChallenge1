package com.aidanvii.codechallenge.state

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalCoroutinesApi::class)
interface ActionsProvider {
    val actions: Flow<Action>
}