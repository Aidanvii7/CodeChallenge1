@file:OptIn(ExperimentalAnimationApi::class)

package com.aidanvii.codechallenge.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.aidanvii.codechallenge.ComposableFunction
import com.aidanvii.codechallenge.Consumer
import com.aidanvii.codechallenge.R
import com.aidanvii.codechallenge.domain.UserCredentials
import com.aidanvii.codechallenge.state.Action.Intent.Login
import com.aidanvii.codechallenge.state.DispatchIntent
import com.aidanvii.codechallenge.state.ViewState.EnterCredentials
import com.aidanvii.codechallenge.state.invoke

@Preview(
    name = "enter credentials screen preview light (credentials populated)",
)
@Composable
private fun EnterCredentialsScreenPreviewLightNotLoggedIn() {
    ScreenSurfaceWithViewState(
        useDarkTheme = false,
        viewState = EnterCredentials(
            previousViewState = null,
            userCredentials = UserCredentials(
                emailAddress = "aidan.vii@gmail.com",
                password = "password",
            )
        ),
        dispatchIntent = {},
    )
}

@Composable
@OptIn(ExperimentalAnimationApi::class)
fun EnterCredentialsScreen(
    modifier: Modifier = Modifier,
    viewState: EnterCredentials,
    dispatchIntent: DispatchIntent,
) {

    // TODO: not secure, needs rethinking
    var emailAddress by savedInstanceState { viewState.emailAddress }
    var password by remember { mutableStateOf(viewState.password) }
    val loginEnabled by remember(emailAddress, password) {
        val emailAddressAndPasswordNotBlank = emailAddress.isNotBlank() && password.isNotBlank()
        viewState.apply {
            if (emailAddressAndPasswordNotBlank) {
                verticalOffset.value = (-30).dp
                surfaceElevation.value = initialSurfaceElevation * 5
            } else {
                verticalOffset.value = 0.dp
                surfaceElevation.value = initialSurfaceElevation
            }
        }
        mutableStateOf(emailAddressAndPasswordNotBlank)
    }
    var keyboardController by remember { mutableStateOf<SoftwareKeyboardController?>(null) }

    Column(
        modifier = modifier,
    ) {
        EmailAddressField(
            modifier = Modifier.fillMaxWidth(),
            userNameValue = emailAddress,
            onTextInputStarted = { keyboardController = it },
            onUserNameChanged = { value ->
                if (!value.contains(" ")) {
                    emailAddress = value
                }
            },
        )
        Spacer(modifier = Modifier.height(16.dp))
        PasswordField(
            modifier = Modifier.fillMaxWidth(),
            passwordValue = password,
            onTextInputStarted = { keyboardController = it },
            onPasswordChanged = { value ->
                if (!value.contains(" ")) {
                    password = value
                }
            },
        )
        Spacer(modifier = Modifier.height(16.dp))
        // FIXME: this produces strange animation after having transitioned between screens
        /*AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = loginEnabled,
            enter = fadeIn() + expandIn(expandFrom = Alignment.TopCenter),
            exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.TopCenter),
        ) {*/
            Button(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally),
                enabled = loginEnabled,
                onClick = {
                    keyboardController?.hideSoftwareKeyboard()
                    dispatchIntent {
                        Login.WithCredentials(
                            userCredentials = UserCredentials(
                                emailAddress = emailAddress,
                                password = password,
                            )
                        )
                    }
                },
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.login),
                )
            }
        //}
    }
}

@Composable
private fun EmailAddressField(
    modifier: Modifier = Modifier,
    userNameValue: String,
    onTextInputStarted: Consumer<SoftwareKeyboardController>,
    onUserNameChanged: Consumer<String>,
) {
    LoginTextField(
        modifier = modifier,
        keyboardType = KeyboardType.Email,
        value = userNameValue,
        label = { EmailAddressText() },
        onTextInputStarted = onTextInputStarted,
        onValueChanged = onUserNameChanged,
    )
}

@Composable
private fun PasswordField(
    modifier: Modifier = Modifier,
    passwordValue: String,
    onTextInputStarted: Consumer<SoftwareKeyboardController>,
    onPasswordChanged: Consumer<String>,
) {
    LoginTextField(
        modifier = modifier,
        visualTransformation = PasswordVisualTransformation(),
        keyboardType = KeyboardType.Password,
        leadingIcon = { Icon(Icons.Filled.Lock) },
        value = passwordValue,
        label = { PasswordText() },
        onTextInputStarted = onTextInputStarted,
        onValueChanged = onPasswordChanged,
    )
}

@Composable
private fun LoginTextField(
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: ComposableFunction? = null,
    value: String,
    label: ComposableFunction,
    onTextInputStarted: Consumer<SoftwareKeyboardController>,
    onValueChanged: Consumer<String>,
) {
    OutlinedTextField(
        modifier = modifier,
        visualTransformation = visualTransformation,
        keyboardType = keyboardType,
        leadingIcon = leadingIcon,
        value = value,
        label = label,
        onTextInputStarted = onTextInputStarted,
        onValueChange = onValueChanged,
    )
}

@Composable
private fun EmailAddressText(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = stringResource(id = R.string.email),
    )
}

@Composable
private fun PasswordText(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = stringResource(id = R.string.password),
    )
}