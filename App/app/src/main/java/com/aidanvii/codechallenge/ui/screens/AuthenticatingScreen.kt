@file:OptIn(ExperimentalAnimationApi::class)

package com.aidanvii.codechallenge.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.ui.tooling.preview.Preview
import com.aidanvii.codechallenge.state.ViewState.Authenticating

@Preview(
    name = "authenticating screen preview light",
)
@Composable
private fun AuthenticatingScreenPreviewLightLoggingIn() {
    ScreenSurfaceWithViewState(
        useDarkTheme = false,
        viewState = Authenticating(
            previousViewState = null,
        ),
        dispatchIntent = {},
    )
}

@Composable
@OptIn(ExperimentalAnimationApi::class)
fun AuthenticatingScreen(
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier,
    )
}