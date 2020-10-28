package com.aidanvii.codechallenge.state

import android.os.Parcelable
import com.aidanvii.codechallenge.domain.UserProfile
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppState(
    val userProfile: UserProfile,
    val viewState: ViewState,
) : Parcelable