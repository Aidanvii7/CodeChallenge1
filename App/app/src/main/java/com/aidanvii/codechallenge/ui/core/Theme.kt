package com.aidanvii.codechallenge.ui.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.aidanvii.codechallenge.ComposableFunction
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = greenPrimary,
    primaryVariant = greenPrimaryVariant,
    secondary = redSecondary,
    surface = Color.Black,
    onSurface = Color.White,
)

private val LightColorPalette = lightColors(
    primary = greenPrimaryVariant,
    primaryVariant = greenPrimary,
    secondary = redSecondary,
    surface = Color.White,
    onSurface = Color.Black,
)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: ComposableFunction,
) {
    MaterialTheme(
        colors = if (useDarkTheme) DarkColorPalette else LightColorPalette,
        typography = typography,
        shapes = shapes,
        content = content,
    )
}