package com.aidanvii.codechallenge.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class UserProfileError : Parcelable {

    @Parcelize
    object Unknown : UserProfileError()

    @Parcelize
    object UpdateAvatarFailed : UserProfileError()

    sealed class Authentication : UserProfileError() {

        @Parcelize
        object NotLoggedIn : UserProfileError()
    }
}