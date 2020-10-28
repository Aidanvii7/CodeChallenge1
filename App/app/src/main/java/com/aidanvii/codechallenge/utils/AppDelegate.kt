package com.aidanvii.codechallenge.utils

import android.app.Application

interface AppDelegate<T : Application> {
    val app: T
    fun onCreate()
}