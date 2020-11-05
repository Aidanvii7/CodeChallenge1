package com.aidanvii.codechallenge.effects

import com.aidanvii.codechallenge.domain.UserProfile
import com.aidanvii.codechallenge.domain.UserProfileError
import com.aidanvii.codechallenge.state.Action
import com.aidanvii.codechallenge.state.ActionsProvider
import com.aidanvii.codechallenge.state.DispatchResultAsync
import com.aidanvii.codechallenge.state.invoke
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

class StubbedUserSideEffects(
    actionsProvider: ActionsProvider,
    dispatchResultAsync: DispatchResultAsync,
    coroutineScope: CoroutineScope,
) : UserProfileSideEffects(
    actionsProvider = actionsProvider,
    dispatchResultAsync = dispatchResultAsync,
    coroutineScope = coroutineScope,
) {

    companion object {
        val dummyUserProfile = UserProfile(
            userId = "Aidan",
            emailAddress = "aidan.vii@gmail.com",
            avatarPhotoUrl = "https://pbs.twimg.com/profile_images/1284435151162937344/nAwInn_m_400x400.jpg",
        )
    }
    override suspend fun handle(action: Action) {
        when (action) {
            is Action.Intent -> handle(action)
        }
    }

    override fun transform(action: Action) = action

    @Volatile
    private var loggedInUserProfile: UserProfile? = null

    private suspend fun handle(
        action: Action.Intent,
    ) {
        when (action) {
            is Action.Intent.Login.WithCredentials -> {
                delay(1000)
                dispatchResultAsync {
                    loggedInUserProfile = dummyUserProfile
                    Action.Result.Login.Success(
                        userProfile = dummyUserProfile,
                    )
                }
            }
            is Action.Intent.Login.Automatically -> {
                delay(1000)
                dispatchResultAsync {
                    val loggedInUserProfile = loggedInUserProfile
                    if(loggedInUserProfile != null) {
                        Action.Result.Login.Success(
                            userProfile = loggedInUserProfile,
                        )
                    } else {
                        Action.Result.Login.Failure(
                            userProfileError = UserProfileError.Authentication.NotLoggedIn,
                        )
                    }
                }
            }
            is Action.Intent.ConfirmNewAvatar -> {
                delay(1000)
                dispatchResultAsync {
                    Action.Result.ConfirmNewAvatar.Success(
                        avatarPhotoUrl = dummyUserProfile.avatarPhotoUrl,
                    )
                }
            }
        }
    }
}