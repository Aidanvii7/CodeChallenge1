package com.aidanvii.codechallenge.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

inline fun LifecycleOwner.whenCreated(crossinline block: () -> Unit) = lifecycle.whenCreated(block)
inline fun Lifecycle.whenCreated(crossinline block: () -> Unit) {
    addObserver(
        object : SelfRemovingLifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate() = block()
        }
    )
}

abstract class SelfRemovingLifecycleObserver : LifecycleObserver {
    open fun onDestroy() = Unit

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) {
        onDestroy()
        removeFrom(owner)
    }

    private fun removeFrom(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
    }
}