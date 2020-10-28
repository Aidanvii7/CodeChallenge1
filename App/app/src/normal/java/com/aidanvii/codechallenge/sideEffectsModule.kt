package com.aidanvii.codechallenge

import com.aidanvii.codechallenge.effects.UserProfileSideEffects
import com.aidanvii.codechallenge.effects.UserProfileSideEffectsImpl
import com.aidanvii.codechallenge.state.AppStateProcessor
import kotlinx.coroutines.GlobalScope
import org.koin.dsl.module

val sideEffectsModule = module {
    single<UserProfileSideEffects> {
        val appStateProcessor = get<AppStateProcessor>()
        UserProfileSideEffectsImpl(
            actionsProvider = appStateProcessor,
            dispatchResultAsync = appStateProcessor,
            coroutineScope = GlobalScope, // TODO: use something else?
            userProfileRepository = get(),
        )
    }
}