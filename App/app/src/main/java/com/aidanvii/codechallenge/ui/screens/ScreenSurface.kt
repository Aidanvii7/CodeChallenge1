package com.aidanvii.codechallenge.ui.screens

import androidx.compose.animation.animate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aidanvii.codechallenge.ComposableFunction
import com.aidanvii.codechallenge.state.DispatchIntent
import com.aidanvii.codechallenge.state.ViewState
import com.aidanvii.codechallenge.ui.ScreensFromState
import com.aidanvii.codechallenge.ui.core.ScreenChrome

@Composable
fun ScreenSurfaceWithViewState(
    modifier: Modifier = Modifier,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    viewState: ViewState,
    dispatchIntent: DispatchIntent,
) {
    ScreenSurface(
        modifier = modifier,
        useDarkTheme = useDarkTheme,
        surfaceShape = viewState.surfaceShape,
        surfaceElevation = viewState.surfaceElevation,
        verticalOffset = viewState.verticalOffset,
    ) {
        ScreensFromState(
            viewState = viewState,
            dispatchIntent = dispatchIntent,
        )
    }
}

@Composable
private fun ScreenSurface(
    modifier: Modifier = Modifier,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    surfaceShape: Shape,
    surfaceElevation: State<Dp>,
    verticalOffset: State<Dp>,
    content: ComposableFunction,
) {
    ScreenChrome(
        modifier = modifier,
        useDarkTheme = useDarkTheme,
    ) {
        Box(
            modifier = modifier
                .padding(
                    horizontal = 25.dp,
                    vertical = 25.dp,
                ).offset(
                    y = animate(target = verticalOffset.value)
                ),
            alignment = Alignment.Center,
        ) {
            Surface(
                elevation = animate(target = surfaceElevation.value),
                shape = surfaceShape,
                content = content,
            )
        }
    }
}