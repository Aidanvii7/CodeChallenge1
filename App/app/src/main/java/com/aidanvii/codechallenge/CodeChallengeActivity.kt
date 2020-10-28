package com.aidanvii.codechallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import com.aidanvii.codechallenge.effects.ChooseAvatarPhotoSideEffect
import com.aidanvii.codechallenge.state.IntentToViewState
import com.aidanvii.codechallenge.ui.core.BackPressedDispatcherProvider
import com.aidanvii.codechallenge.ui.screens.ScreenSurfaceWithViewState
import com.aidanvii.codechallenge.utils.unaryPlus
import com.aidanvii.codechallenge.utils.whenCreated
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class CodeChallengeActivity : AppCompatActivity() {

    private val intentToViewState: IntentToViewState by inject()

    private val chooseAvatarPhotoSideEffect: ChooseAvatarPhotoSideEffect by inject {
        parametersOf(this as ComponentActivity)
    }

    init {
        whenCreated {
            chooseAvatarPhotoSideEffect.start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BackPressedDispatcherProvider {
                val viewState = +intentToViewState.viewState
                ScreenSurfaceWithViewState(
                    viewState = viewState,
                    dispatchIntent = intentToViewState,
                )
            }
        }
    }
}