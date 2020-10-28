@file:OptIn(ExperimentalAnimationApi::class)

package com.aidanvii.codechallenge.ui.screens

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.aidanvii.codechallenge.Action0
import com.aidanvii.codechallenge.ComposableFunction
import com.aidanvii.codechallenge.R
import com.aidanvii.codechallenge.state.Action.Intent.*
import com.aidanvii.codechallenge.state.DispatchIntent
import com.aidanvii.codechallenge.state.ViewState
import com.aidanvii.codechallenge.state.ViewState.Profile.AvatarImage.FromCurrentAvatarUrl
import com.aidanvii.codechallenge.state.ViewState.Profile.AvatarImage.FromNewAvatarUri
import com.aidanvii.codechallenge.state.invoke
import com.aidanvii.codechallenge.ui.core.*
import com.aidanvii.codechallenge.utils.safeCast
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.imageloading.ImageLoadState
import dev.chrisbanes.accompanist.imageloading.MaterialLoadingImage

@Preview(
    name = "profile screen preview light (current avatar)",
)
@Composable
private fun ProfileScreenPreviewLightCurrentAvatar() {
    OnBackPressedDispatcherOwnerStub.BackPressedDispatcherProvider {
        ScreenSurfaceWithViewState(
            useDarkTheme = false,
            viewState = ViewState.Profile(
                previousViewState = null,
                userName = "Aidan",
                emailAddress = "aidan.vii@gmail.com",
                avatarImage = FromCurrentAvatarUrl(
                    url = "https://pbs.twimg.com/profile_images/1284435151162937344/nAwInn_m_400x400.jpg",
                ),
                retrievingNewAvatarImage = false,
            ),
            dispatchIntent = {},
        )
    }
}

@Preview(
    name = "profile screen preview light (new avatar, not confirmed)",
)
@Composable
private fun ProfileScreenPreviewLightNewAvatarNotConfirmed() {
    OnBackPressedDispatcherOwnerStub.BackPressedDispatcherProvider {
        ScreenSurfaceWithViewState(
            useDarkTheme = false,
            viewState = ViewState.Profile(
                previousViewState = null,
                userName = "Aidan",
                emailAddress = "aidan.vii@gmail.com",
                avatarImage = FromNewAvatarUri(
                    uri = Uri.EMPTY,
                    confirmed = false,
                ),
                retrievingNewAvatarImage = false,
            ),
            dispatchIntent = {},
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewState: ViewState.Profile,
    dispatchIntent: DispatchIntent,
) {
    Box(
        modifier = modifier
            .padding(all = 16.dp)
            .fillMaxWidth(fraction = 0.9f)
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Box(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally),
            ) {
                CircularAvatarWithOptions(
                    viewState = viewState,
                    onTakePhoto = {
                        dispatchIntent { ChooseAvatarPhoto.FromCamera }
                    },
                    onFromGallery = {
                        dispatchIntent { ChooseAvatarPhoto.FromGallery }
                    },
                )
            }

            var showChangeAvatarOptions by remember(viewState.avatarImage) {
                mutableStateOf(viewState.avatarImage.safeCast<FromNewAvatarUri>()?.confirmed?.not() ?: false)
            }
            AnimatedVisibility(
                visible = showChangeAvatarOptions,
            ) {
                if (viewState.avatarImage is FromNewAvatarUri) {
                    NewAvatarConfirmation(
                        enabled = showChangeAvatarOptions,
                        onConfirmNewAvatar = {
                            showChangeAvatarOptions = false
                            dispatchIntent { ConfirmNewAvatar(viewState.avatarImage.unwrap()) }
                        },
                        onCancelNewAvatar = {
                            showChangeAvatarOptions = false
                            dispatchIntent { CancelNewAvatar }
                        },
                    )
                } else {
                    NewAvatarConfirmation(
                        enabled = true,
                        onConfirmNewAvatar = {},
                        onCancelNewAvatar = {},
                    )
                }
            }

            AnimatedVisibility(
                visible = viewState.avatarImage is FromCurrentAvatarUrl,
            ) {
                ProfileDetails(
                    userName = viewState.userName,
                    emailAddress = viewState.emailAddress,
                    onLogoutClicked = { dispatchIntent { Logout } },
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CircularAvatarWithOptions(
    modifier: Modifier = Modifier,
    viewState: ViewState.Profile,
    onTakePhoto: Action0,
    onFromGallery: Action0,
) {
    CircularBox(modifier = modifier) {
        var showAvatarOptions by remember(viewState) { mutableStateOf(false) }
        val imageModifier = if (viewState.avatarImage is FromCurrentAvatarUrl && !viewState.retrievingNewAvatarImage) {
            Modifier.clickable {
                if (!showAvatarOptions) {
                    showAvatarOptions = true
                }
            }
        } else Modifier
        CoilImage(
            modifier = imageModifier.matchParentSize(),
            data = viewState.avatarImage.unwrap(),
        ) { imageState ->
            when (imageState) {
                is ImageLoadState.Success -> MaterialLoadingImage(
                    result = imageState,
                    fadeInEnabled = true,
                    fadeInDurationMs = 600,
                )
                ImageLoadState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is ImageLoadState.Error -> AvatarImageUnavailable()
                ImageLoadState.Empty -> AvatarImageUnavailable()
            }
        }
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = showAvatarOptions,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            ChangeAvatarOptions(
                onTakePhoto = onTakePhoto,
                onFromGallery = onFromGallery,
                onCancel = {
                    if (showAvatarOptions) {
                        showAvatarOptions = false
                    }
                },
            )
        }
    }
}

@Composable
private fun AvatarImageUnavailable() {
    Image(
        asset = vectorResource(id = R.drawable.ic_crying),
        modifier = Modifier.size(100.dp),
    )
}

@Composable
private fun CircularBox(
    modifier: Modifier = Modifier,
    children: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 1.0f)
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clip(shape = RoundedCornerShape(percent = 50)),
    ) {
        children()
    }
}

@Preview(
    name = "ChangeAvatarOptions preview light",
)
@Composable
private fun ChangeAvatarOptionsPreview() {
    Chrome(
        useDarkTheme = false,
    ) {
        OnBackPressedDispatcherOwnerStub.BackPressedDispatcherProvider {
            CircularBox(
                modifier = Modifier.size(200.dp)
            ) {
                ChangeAvatarOptions(
                    modifier = Modifier,
                    onTakePhoto = {},
                    onFromGallery = {},
                    onCancel = {},
                )
            }
        }
    }
}

@Composable
private fun ChangeAvatarOptions(
    modifier: Modifier = Modifier,
    onTakePhoto: Action0,
    onFromGallery: Action0,
    onCancel: Action0,
) {
    onBackPressed(
        enabled = true,
        action = onCancel,
    )
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ChangeAvatarOptionsButton(
            onClick = {
                onTakePhoto()
                onCancel()
            },
            modifier = Modifier.weight(weight = 1.0f),
            textRes = R.string.take_photo,
        )
        ChangeAvatarOptionsButton(
            onClick = {
                onFromGallery()
                onCancel()
            },
            modifier = Modifier.weight(weight = 1f),
            textRes = R.string.from_gallery,
        )
        ChangeAvatarOptionsButton(
            onClick = onCancel,
            modifier = Modifier.weight(weight = 1f),
            textRes = R.string.cancel,
        )
    }
}

@Composable
private fun ChangeAvatarOptionsButton(
    onClick: Action0,
    modifier: Modifier = Modifier,
    @StringRes textRes: Int,
) {
    val defaultButtonBackgroundColor = ButtonConstants.defaultButtonBackgroundColor(
        enabled = true
    )
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        elevation = 0.dp,
        shape = CutCornerShape(percent = 0),
        backgroundColor = defaultButtonBackgroundColor.copy(alpha = 0.8f),
        contentColor = ButtonConstants.defaultButtonContentColor(
            enabled = true,
            defaultColor = contentColorFor(defaultButtonBackgroundColor)
        ),
    ) {
        Text(text = stringResource(id = textRes))
    }
}

@Preview(
    name = "ProfileDetails preview light",
)
@Composable
private fun ProfileDetailsPreviewLight() {
    Chrome(
        useDarkTheme = false,
    ) {
        ProfileDetails(
            userName = "Aidan",
            emailAddress = "aidan.vii@gmail.com",
            onLogoutClicked = {},
        )
    }
}

@Composable
private fun ProfileDetails(
    modifier: Modifier = Modifier,
    userName: String,
    emailAddress: String,
    onLogoutClicked: Action0,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            UserNameText(userName = userName)
            Spacer(modifier = Modifier.height(16.dp))
            EmailAddressText(emailAddress = emailAddress)
            Spacer(modifier = Modifier.height(16.dp))
        }
        CircleButtonWithIcon(
            modifier = Modifier
                .align(alignment = Alignment.Bottom),
            onClick = onLogoutClicked,
            color = redSecondaryVariant,
            iconAsset = Icons.Outlined.ExitToApp,
            enabled = true,
        )
    }
}

@Composable
private fun UserNameText(
    modifier: Modifier = Modifier,
    userName: String,
) {
    TextLabelRow(
        modifier = modifier,
        labelRes = R.string.username,
        text = userName,
    )
}

@Composable
private fun EmailAddressText(
    modifier: Modifier = Modifier,
    emailAddress: String,
) {
    TextLabelRow(
        modifier = modifier,
        labelRes = R.string.email,
        text = emailAddress,
    )
}

@Composable
private fun TextLabelRow(
    modifier: Modifier,
    @StringRes
    labelRes: Int,
    text: String,
) {
    Column {
        Text(
            modifier = modifier,
            text = stringResource(id = labelRes),
            fontWeight = FontWeight.Bold,
        )
        ProvideEmphasis(
            emphasis = EmphasisAmbient.current.medium,
        ) {
            Text(
                modifier = modifier,
                text = text,
                style = MaterialTheme.typography.body2,
            )
        }
    }
}

@Preview(
    name = "NewAvatarConfirmation preview",
)
@Composable
private fun NewAvatarConfirmationPreview() {

    Chrome {
        NewAvatarConfirmation(
            modifier = Modifier.width(150.dp),
            onConfirmNewAvatar = {},
            onCancelNewAvatar = {},
            enabled = true,
        )
    }
}

@Composable
private fun NewAvatarConfirmation(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onConfirmNewAvatar: Action0,
    onCancelNewAvatar: Action0,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        CircleButtonWithIcon(
            onClick = onCancelNewAvatar,
            color = redSecondaryVariant,
            iconAsset = Icons.Outlined.Close,
            enabled = enabled,
        )
        CircleButtonWithIcon(
            onClick = onConfirmNewAvatar,
            color = greenPrimaryVariant,
            iconAsset = Icons.Outlined.Check,
            enabled = enabled,
        )
    }
}

@Composable
private fun CircleButtonWithIcon(
    onClick: Action0,
    color: Color,
    iconAsset: VectorAsset,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    CircleButton(
        onClick = onClick,
        modifier = modifier,
        color = color,
        enabled = enabled,
    ) {
        Icon(
            asset = iconAsset,
            tint = Color.White,
        )
    }
}

@Composable
private fun CircleButton(
    onClick: Action0,
    color: Color,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    content: ComposableFunction,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .padding(4.dp)
            .background(
                color = color,
                shape = RoundedCornerShape(percent = 50),
            ),
        enabled = enabled,
        icon = content,
    )
}