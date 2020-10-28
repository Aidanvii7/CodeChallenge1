package com.aidanvii.codechallenge.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aidanvii.codechallenge.state.DispatchIntent
import com.aidanvii.codechallenge.state.ViewState

@Composable
fun ScreensFromState(
    modifier: Modifier = Modifier,
    viewState: ViewState,
    dispatchIntent: DispatchIntent,
) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .padding(all = 16.dp),
    ) {
        TransitioningScreenFromState<ViewState.EnterCredentials>(viewState, dispatchIntent)
        TransitioningScreenFromState<ViewState.Authenticating>(viewState, dispatchIntent)
        TransitioningScreenFromState<ViewState.Profile>(viewState, dispatchIntent)
    }
}

@Composable
@OptIn(ExperimentalAnimationApi::class)
private inline fun <reified T : ViewState> TransitioningScreenFromState(
    viewState: ViewState,
    dispatchIntent: DispatchIntent,
) {
    val viewStateToAnimate = viewState as? T ?: viewState.previousViewState as? T
    AnimatedVisibility(
//        initiallyVisible = true,
        modifier = Modifier.wrapContentSize(),
        visible = viewState is T,
        enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
        exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center),
    ) {
        viewStateToAnimate?.build(dispatchIntent = dispatchIntent)
    }
}