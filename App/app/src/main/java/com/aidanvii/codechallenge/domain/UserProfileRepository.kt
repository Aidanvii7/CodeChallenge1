package com.aidanvii.codechallenge.domain

import android.content.Context
import android.net.Uri
import com.aidanvii.codechallenge.data.GravatarApiService
import com.aidanvii.codechallenge.data.TemporaryUserDetails
import com.aidanvii.codechallenge.data.UserApiService
import com.aidanvii.codechallenge.data.UserDetailsStore
import com.aidanvii.codechallenge.utils.CoroutineDispatchers
import com.aidanvii.codechallenge.utils.bitmapFrom
import com.aidanvii.codechallenge.utils.md5
import com.aidanvii.codechallenge.utils.toBase64String
import com.slack.eithernet.ApiResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext

/**
 * TODO: This class needs a LOT of tidying up and some responsibilities split out!!
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileRepository internal constructor(
    private val applicationContext: Context,
    private val userApiService: UserApiService,
    private val gravatarApiService: GravatarApiService,
    private val userDetailsStore: UserDetailsStore,
    private val coroutineDispatchers: CoroutineDispatchers,
) {

    suspend fun getUserProfile(
        userCredentials: UserCredentials? = null,
    ): Either<UserProfile, UserProfileError> =
        withContext(coroutineDispatchers.io) {
            if (userCredentials != null) {
                getUserProfileWithCredentials(userCredentials)
            } else {
                when (val result = tryGetUserProfileWithExistingCredentials()) {
                    is Success -> result.unwrap().first.toSuccess()
                    is Failure -> result.unwrap().toFailure()
                }
            }
        }

    private suspend fun getUserProfileWithCredentials(
        userCredentials: UserCredentials,
    ): Either<UserProfile, UserProfileError> =
        when (val getNewSessionTokenResult = getNewSessionToken(userCredentials)) {
            is Success -> when (val result = tryGetUserProfileWithExistingCredentials()) {
                is Success -> result.unwrap().first.toSuccess()
                is Failure -> result.unwrap().toFailure()
            }
            is Failure -> getNewSessionTokenResult.unwrap().toFailure()
        }

    suspend fun updateAvatar(
        avatarImageUri: Uri,
    ): Either<UserProfile, UserProfileError> {
        val avatarImageAsBase64String = withContext(coroutineDispatchers.default) {
            applicationContext.contentResolver.bitmapFrom(avatarImageUri).toBase64String()
        }
        return withContext(coroutineDispatchers.io) {
            when (val getUserProfileResult = tryGetUserProfileWithExistingCredentials()) {
                is Success -> {
                    val (_, sessionToken) = getUserProfileResult.unwrap()
                    when (val updateAvatarResult = userApiService.updateAvatar(
                        sessionToken = sessionToken,
                        avatarData = UserApiService.AvatarData(
                            base64AvatarData = avatarImageAsBase64String,
                        )
                    )) {
                        is ApiResult.Success -> {
                            val (userProfile, _) = getUserProfileResult.unwrap()
                            UserProfile(
                                userId = userProfile.userId,
                                emailAddress = userProfile.emailAddress,
                                avatarPhotoUrl = updateAvatarResult.response.avatarImageUrl,
                            ).toSuccess()
                        }
                        is ApiResult.Failure -> UserProfileError.UpdateAvatarFailed.toFailure()
                    }
                }
                is Failure -> getUserProfileResult.unwrap().toFailure()
            }
        }
    }

    private suspend fun tryGetUserProfileWithExistingCredentials(): Either<Pair<UserProfile, String>, UserProfileError> {
        val storedUserCredentials = userDetailsStore.userCredentials
        val temporaryUserDetails: TemporaryUserDetails = userDetailsStore.temporaryUserDetails ?: storedUserCredentials?.let {
            when (val getNewSessionTokenResult = getNewSessionToken(storedUserCredentials)) {
                is Success -> getNewSessionTokenResult.unwrap().run {
                    TemporaryUserDetails(
                        sessionToken = sessionToken,
                        userId = userId
                    )
                }
                is Failure -> return getNewSessionTokenResult.unwrap().toFailure()
            }
        } ?: return UserProfileError.Authentication.NotLoggedIn.toFailure()

        return if (storedUserCredentials != null) {
            when (val getUserDetailsResult = userApiService.getUserDetails(
                sessionToken = temporaryUserDetails.sessionToken,
                userId = temporaryUserDetails.userId,
            )) {
                is ApiResult.Success -> {
                    val userProfileWithToken = UserProfile(
                        userId = temporaryUserDetails.userId,
                        emailAddress = getUserDetailsResult.response.emailAddress,
                        avatarPhotoUrl = getUserDetailsResult.response.avatarImageUrl.let { avatarImageUrl ->
                            if (avatarImageUrl.isNotBlank()) avatarImageUrl else {
                                when (val getUserProfileResult = gravatarApiService.getUserProfile(getUserDetailsResult.response.emailAddress.md5)) {
                                    is ApiResult.Success -> getUserProfileResult.response.entries.firstOrNull()?.imageUrl ?: avatarImageUrl
                                    is ApiResult.Failure -> avatarImageUrl
                                }
                            }
                        },
                    ) to temporaryUserDetails.sessionToken
                    userProfileWithToken.toSuccess()
                }
                is ApiResult.Failure -> when (getUserDetailsResult) {
                    is ApiResult.Failure.HttpFailure -> when (getUserDetailsResult.code) {
                        401 -> when (val getNewSessionTokenResult = getNewSessionToken(storedUserCredentials)) {
                            is Success -> tryGetUserProfileWithExistingCredentials()
                            is Failure -> getNewSessionTokenResult.unwrap().toFailure()
                        }
                        else -> TODO("everything else")
                    }
                    else -> TODO("everything else")
                }
            }
        } else UserProfileError.Authentication.NotLoggedIn.toFailure()
    }

    private suspend fun getNewSessionToken(
        userCredentials: UserCredentials,
    ): Either<UserApiService.UserIdWithSessionToken, UserProfileError> =
        userCredentials.run {
            when (val result = userApiService.getSessionToken(
                userCredentials = UserApiService.UserCredentials(
                    emailAddress = emailAddress,
                    password = password,
                )
            )) {
                is ApiResult.Success -> {
                    val (emailAddress, password) = userCredentials
                    val (userId, sessionToken) = result.response
                    userDetailsStore.apply {
                        updateUserCredentials(
                            userCredentials = UserCredentials(
                                emailAddress = emailAddress,
                                password = password,
                            )
                        )
                        updateTemporaryUserDetails(
                            temporaryUserDetails = TemporaryUserDetails(
                                sessionToken = sessionToken,
                                userId = userId,
                            )
                        )
                    }
                    result.response.toSuccess()
                }
                is ApiResult.Failure -> when (result) {
                    is ApiResult.Failure.ApiFailure,
                    is ApiResult.Failure.NetworkFailure,
                    is ApiResult.Failure.UnknownFailure,
                    is ApiResult.Failure.HttpFailure,
                    -> UserProfileError.Unknown.toFailure()
                }
            }
        }

    suspend fun logout() {
        withContext(coroutineDispatchers.io) {
            userDetailsStore.clear()
        }
    }
}