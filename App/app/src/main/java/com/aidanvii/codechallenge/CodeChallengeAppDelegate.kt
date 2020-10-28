package com.aidanvii.codechallenge


import androidx.annotation.VisibleForTesting
import com.aidanvii.codechallenge.effects.UserProfileSideEffects
import com.aidanvii.codechallenge.state.Action
import com.aidanvii.codechallenge.state.DispatchIntent
import com.aidanvii.codechallenge.state.invoke
import com.aidanvii.codechallenge.utils.AppDelegate
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.core.logger.Logger as KoinLogger
import org.koin.core.module.Module as KoinModule

class CodeChallengeAppDelegate(
    override val app: CodeChallengeApp,
    @param:VisibleForTesting
    private val koinLogger: KoinLogger = AndroidLogger(Level.ERROR),
    @param:VisibleForTesting
    private val onKoinApplication: KoinApplication.() -> Unit = {},
    @param:VisibleForTesting
    private val koinOverrides: KoinModule.() -> Unit = {},
) : AppDelegate<CodeChallengeApp> {
    override fun onCreate() {
        initKoin()
        initSideEffects()
    }

    private fun initKoin() {
        startKoin {
            logger(koinLogger)
            androidContext(app.applicationContext)
            modules(modules = modules + overrideModule)
        }.onKoinApplication()
    }

    private val overrideModule: KoinModule
        get() = module(override = true) { koinOverrides() }

    private fun initSideEffects() {
        app.get<UserProfileSideEffects>().start()
        app.get<DispatchIntent>().invoke {
            Action.Intent.Login.Automatically
        }
    }

    @VisibleForTesting
    private val modules: List<KoinModule> = listOf(
        codeChallengeAppModule,
        sideEffectsModule,
    )
}