package com.aidanvii.codechallenge.ui.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.ui.tooling.preview.Preview
import com.aidanvii.codechallenge.ComposableFunction

@Preview(
    name = "Light theme",
    showBackground = true
)
@Composable
private fun ScreenChromePreviewLight() {
    ScreenChromePreviewBase(
        useDarkTheme = false,
    )
}

@Preview(
    name = "Dark theme",
    showBackground = true
)
@Composable
private fun ScreenChromePreviewDark() {
    ScreenChromePreviewBase(
        useDarkTheme = true,
    )
}

@Composable
private fun ScreenChromePreviewBase(
    useDarkTheme: Boolean,
) {
    ScreenChrome(
        useDarkTheme = useDarkTheme,
    ) {
    }
}

@Composable
fun ScreenChrome(
    modifier: Modifier = Modifier,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: ComposableFunction
) {
    Chrome(
        useDarkTheme = useDarkTheme,
        modifier = modifier.fillMaxSize(),
        content = content
    )
}

@Composable
fun Chrome(
    modifier: Modifier = Modifier,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: ComposableFunction
) {
    AppTheme(useDarkTheme) {
        Surface(
            color = MaterialTheme.colors.background,
            modifier = modifier,
        ) {
            content()
        }
    }
}