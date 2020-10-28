package com.aidanvii.codechallenge.state

import android.net.Uri
import com.aidanvii.codechallenge.domain.UserProfileError
import com.aidanvii.codechallenge.domain.UserCredentials
import com.aidanvii.codechallenge.domain.UserProfile

sealed class Action {

    sealed class Intent : Action() {

        sealed class Login : Intent() {
            data class WithCredentials(
                val userCredentials: UserCredentials,
            ) : Login()

            object Automatically : Login()
        }

        object Logout : Intent()

        sealed class ChooseAvatarPhoto : Intent() {
            object FromCamera : ChooseAvatarPhoto()
            object FromGallery : ChooseAvatarPhoto()
        }

        data class ConfirmNewAvatar(
            val uri: Uri,
        ) : Intent()

        object CancelNewAvatar : Intent()
    }

    sealed class Result : Action() {

        sealed class Login {
            data class Success(
                val userProfile: UserProfile,
            ) : Result()

            data class Failure(
                val userProfileError: UserProfileError,
            ) : Result()
        }

        sealed class GetAvatarPhoto : Result() {

            data class Success(
                val uri: Uri,
            ) : GetAvatarPhoto()

            object Failure : GetAvatarPhoto()
        }

        sealed class ConfirmNewAvatar {
            data class Success(
                val avatarPhotoUrl: String,
            ) : Result()

            data class Failure(
                val userProfileError: UserProfileError,
            ) : Result()
        }
    }
}