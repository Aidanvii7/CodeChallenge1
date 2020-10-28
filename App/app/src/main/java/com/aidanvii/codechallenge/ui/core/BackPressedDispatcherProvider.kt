/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aidanvii.codechallenge.ui.core

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import com.aidanvii.codechallenge.ComposableFunction

/**
 * Taken from https://github.com/android/compose-samples/blob/master/Jetchat/app/src/main/java/com/example/compose/jetchat/conversation/BackPressedDispatcherProvider.kt
 */
@Composable
fun OnBackPressedDispatcherOwner.BackPressedDispatcherProvider(
    children: ComposableFunction,
) {
    Providers(
        values = arrayOf(BackPressedDispatcherAmbient provides this),
        children = children,
    )
}

/**
 * This [Composable] can be used with a [BackPressedDispatcherAmbient] to intercept a back press (if [enabled]).
 */
@Composable
fun onBackPressed(
    enabled: Boolean = true,
    action: () -> Unit,
) {
    val dispatcher = BackPressedDispatcherAmbient.current.onBackPressedDispatcher

    // This callback is going to be remembered only if onBackPressed is referentially equal.
    val backCallback = remember(action) {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                action()
            }
        }
    }

    // Using onCommit guarantees that failed transactions don't incorrectly toggle the
    // remembered callback.
    onCommit(enabled) {
        backCallback.isEnabled = enabled
    }

    onCommit(dispatcher, action) {
        // Whenever there's a new dispatcher set up the callback
        dispatcher.addCallback(backCallback)
        onDispose {
            backCallback.remove()
        }
    }
}

/**
 * This [Ambient] is used to provide an [OnBackPressedDispatcherOwner]:
 *
 * ```
 * Providers(BackPressedDispatcherAmbient provides requireActivity()) { }
 * ```
 *
 * and setting up the callbacks with [onBackPressed].
 */
val BackPressedDispatcherAmbient = staticAmbientOf<OnBackPressedDispatcherOwner> { error("Ambient used without Provider") }

/**
 * For [androidx.ui.tooling.preview.Preview] composables only.
 */
object OnBackPressedDispatcherOwnerStub : OnBackPressedDispatcherOwner {
    override fun getLifecycle(): Lifecycle = TODO("Not yet implemented")
    override fun getOnBackPressedDispatcher() = OnBackPressedDispatcher()
}