package com.aidanvii.codechallenge.state

import com.aidanvii.codechallenge.Provider

fun interface DispatchIntent {
    operator fun invoke(intent: Action.Intent)
}

interface DispatchIntentAsync {
    suspend operator fun invoke(intent: Action.Intent)
}

fun interface DispatchResult {
    operator fun invoke(result: Action.Result)
}

interface DispatchResultAsync {
    suspend operator fun invoke(result: Action.Result)
}

inline operator fun DispatchIntent.invoke(intentProvider: Provider<Action.Intent>) {
    invoke(intentProvider())
}

suspend inline operator fun DispatchIntentAsync.invoke(intentProvider: Provider<Action.Intent>) {
    invoke(intentProvider())
}

inline operator fun DispatchResult.invoke(resultProvider: Provider<Action.Result>) {
    invoke(resultProvider())
}

suspend inline operator fun DispatchResultAsync.invoke(resultProvider: Provider<Action.Result>) {
    invoke(resultProvider())
}

//fun DispatchIntent.dispatchIntent(intent: Action.Intent) = invoke(intent)
//fun DispatchIntent.dispatchIntent(intentProvider: Provider<Action.Intent>) = invoke(intentProvider)
//fun DispatchResult.dispatchResult(result: Action.Result) = invoke(result)
//fun DispatchResult.dispatchResult(resultProvider: Provider<Action.Result>) = invoke(resultProvider)
suspend fun DispatchIntentAsync.dispatchIntentAsync(intent: Action.Intent) = invoke(intent)
suspend fun DispatchResultAsync.dispatchResultAsync(resultProvider: Provider<Action.Result>) = invoke(resultProvider)
