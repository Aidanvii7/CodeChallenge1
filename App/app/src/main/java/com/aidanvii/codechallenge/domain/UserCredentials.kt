package com.aidanvii.codechallenge.domain

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.android.parcel.Parcelize

@Immutable
@Parcelize
data class UserCredentials(
    val emailAddress: String,
    val password: String,
) : Parcelable