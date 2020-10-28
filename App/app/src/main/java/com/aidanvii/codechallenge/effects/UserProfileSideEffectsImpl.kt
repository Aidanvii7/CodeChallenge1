package com.aidanvii.codechallenge.effects

import com.aidanvii.codechallenge.domain.Failure
import com.aidanvii.codechallenge.domain.Success
import com.aidanvii.codechallenge.domain.UserCredentials
import com.aidanvii.codechallenge.domain.UserProfileRepository
import com.aidanvii.codechallenge.state.Action
import com.aidanvii.codechallenge.state.ActionsProvider
import com.aidanvii.codechallenge.state.DispatchResultAsync
import com.aidanvii.codechallenge.state.invoke
import kotlinx.coroutines.CoroutineScope

internal class UserProfileSideEffectsImpl(
    actionsProvider: ActionsProvider,
    dispatchResultAsync: DispatchResultAsync,
    coroutineScope: CoroutineScope,
    private val userProfileRepository: UserProfileRepository,
) : UserProfileSideEffects(
    actionsProvider = actionsProvider,
    dispatchResultAsync = dispatchResultAsync,
    coroutineScope = coroutineScope,
) {

    override fun transform(action: Action) = action

    override suspend fun handle(action: Action) {
        when (action) {
            is Action.Intent.Login -> handleLogin(action)
            is Action.Intent.Logout -> handleLogout()
            is Action.Intent.ConfirmNewAvatar -> handleConfirmNewAvatar(action)
        }
    }

    private suspend fun handleLogin(action: Action.Intent.Login) {
        when (action) {
            is Action.Intent.Login.WithCredentials -> handleLogin(action.userCredentials)
            Action.Intent.Login.Automatically -> handleLogin()
        }
    }

    private suspend fun handleLogout() {
        userProfileRepository.logout()
    }

    private suspend fun handleConfirmNewAvatar(action: Action.Intent.ConfirmNewAvatar) {
        when (val result = userProfileRepository.updateAvatar(action.uri)) {
            is Success -> dispatchResultAsync {
                Action.Result.ConfirmNewAvatar.Success(
                    avatarPhotoUrl = result.unwrap().avatarPhotoUrl,
                )
            }
            is Failure -> dispatchResultAsync {
                Action.Result.ConfirmNewAvatar.Failure(
                    userProfileError = result.unwrap(),
                )
            }
        }
    }

    private suspend fun handleLogin(userCredentials: UserCredentials? = null) {
        when (val result = userProfileRepository.getUserProfile(userCredentials)) {
            is Success -> dispatchResultAsync {
                Action.Result.Login.Success(
                    userProfile = result.unwrap(),
                )
            }
            is Failure -> dispatchResultAsync {
                Action.Result.Login.Failure(
                    userProfileError = result.unwrap(),
                )
            }
        }
    }
}