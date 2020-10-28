package com.aidanvii.codechallenge.state

import com.aidanvii.codechallenge.domain.UserCredentials

fun AppState.reduceWith(
    action: Action,
): AppState = when (action) {
    is Action.Intent -> reduceWithIntent(action)
    is Action.Result -> reduceWithResult(action)
}

private fun AppState.reduceWithIntent(
    intent: Action.Intent,
): AppState = when (intent) {
    is Action.Intent.Login -> copy(
        viewState = ViewState.Authenticating(
            previousViewState = viewState,
        )
    )
    Action.Intent.Logout -> copy(
        viewState = ViewState.EnterCredentials(
            previousViewState = viewState,
            userCredentials = UserCredentials(
                emailAddress = userProfile.emailAddress,
                password = "",
            ),
        )
    )
    Action.Intent.ChooseAvatarPhoto.FromCamera,
    Action.Intent.ChooseAvatarPhoto.FromGallery,
    -> copy(
        viewState = ViewState.Profile(
            previousViewState = viewState,
            userName = userProfile.userId,
            emailAddress = userProfile.emailAddress,
            avatarImage = ViewState.Profile.AvatarImage.FromCurrentAvatarUrl(
                url = userProfile.avatarPhotoUrl,
            ),
            retrievingNewAvatarImage = true,
        ),
    )
    is Action.Intent.ConfirmNewAvatar -> copy(
        viewState = ViewState.Profile(
            previousViewState = viewState,
            userName = userProfile.userId,
            emailAddress = userProfile.emailAddress,
            avatarImage = ViewState.Profile.AvatarImage.FromNewAvatarUri(
                uri = intent.uri,
                confirmed = true,
            ),
            retrievingNewAvatarImage = false,
        ),
    )
    Action.Intent.CancelNewAvatar -> copy(
        viewState = ViewState.Profile(
            previousViewState = viewState,
            userName = userProfile.userId,
            emailAddress = userProfile.emailAddress,
            avatarImage = ViewState.Profile.AvatarImage.FromCurrentAvatarUrl(
                url = userProfile.avatarPhotoUrl,
            ),
            retrievingNewAvatarImage = false,
        ),
    )
}

private fun AppState.reduceWithResult(
    result: Action.Result,
): AppState = when (result) {
    is Action.Result.Login.Success -> copy(
        userProfile = result.userProfile,
        viewState = ViewState.Profile(
            previousViewState = viewState,
            userName = result.userProfile.userId,
            emailAddress = result.userProfile.emailAddress,
            avatarImage = ViewState.Profile.AvatarImage.FromCurrentAvatarUrl(result.userProfile.avatarPhotoUrl),
            retrievingNewAvatarImage = false,
        )
    )
    is Action.Result.Login.Failure -> copy(
        userProfile = userProfile.copy(
            emailAddress = "",
            avatarPhotoUrl = "",
        ),
        viewState = ViewState.EnterCredentials(
            previousViewState = viewState,
            userCredentials = UserCredentials(
                emailAddress = userProfile.emailAddress,
                password = "",
            ),
            userProfileError = result.userProfileError,
        )
    )
    is Action.Result.GetAvatarPhoto.Success -> copy(
        viewState = ViewState.Profile(
            previousViewState = viewState,
            userName = userProfile.userId,
            emailAddress = userProfile.emailAddress,
            avatarImage = ViewState.Profile.AvatarImage.FromNewAvatarUri(
                uri = result.uri,
                confirmed = false,
            ),
            retrievingNewAvatarImage = false,
        )
    )
    is Action.Result.GetAvatarPhoto.Failure -> copy(
        viewState = ViewState.Profile(
            previousViewState = viewState,
            userName = userProfile.userId,
            emailAddress = userProfile.emailAddress,
            avatarImage = ViewState.Profile.AvatarImage.FromCurrentAvatarUrl(
                url = userProfile.avatarPhotoUrl,
            ),
            retrievingNewAvatarImage = false,
        )
    )
    is Action.Result.ConfirmNewAvatar.Success -> copy(
        userProfile = userProfile.copy(
            avatarPhotoUrl = result.avatarPhotoUrl,
        ),
        viewState = ViewState.Profile(
            previousViewState = viewState,
            userName = userProfile.userId,
            emailAddress = userProfile.emailAddress,
            avatarImage = ViewState.Profile.AvatarImage.FromCurrentAvatarUrl(
                url = result.avatarPhotoUrl,
            ),
            retrievingNewAvatarImage = false,
        )
    )
    is Action.Result.ConfirmNewAvatar.Failure -> TODO("Show avatar update failure?")
}
