package com.aidanvii.codechallenge.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserProfile(
    val userId: String,
    val emailAddress: String,
    val avatarPhotoUrl: String,
) : Parcelable