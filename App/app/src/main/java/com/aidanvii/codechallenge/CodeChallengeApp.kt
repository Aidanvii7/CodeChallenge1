package com.aidanvii.codechallenge

import android.app.Application
import com.aidanvii.codechallenge.utils.AppDelegate

class CodeChallengeApp : Application() {

    private val appDelegate: AppDelegate<CodeChallengeApp> by lazy(LazyThreadSafetyMode.NONE) {
        CodeChallengeAppDelegate(this)
    }

    override fun onCreate() {
        super.onCreate()
        appDelegate.onCreate()
    }
}