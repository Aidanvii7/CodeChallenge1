package com.aidanvii.codechallenge.state

import android.net.Uri
import android.os.Parcelable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aidanvii.codechallenge.domain.UserProfileError
import com.aidanvii.codechallenge.domain.UserCredentials
import com.aidanvii.codechallenge.ui.screens.AuthenticatingScreen
import com.aidanvii.codechallenge.ui.screens.EnterCredentialsScreen
import com.aidanvii.codechallenge.ui.screens.ProfileScreen
import com.aidanvii.codechallenge.utils.Wrapper
import kotlinx.android.parcel.Parcelize

@Immutable
sealed class ViewState : Parcelable {

    abstract val previousViewState: ViewState?

    open val surfaceShape: Shape
        get() = RoundedCornerShape(20.dp)

    open val initialSurfaceElevation: Dp
        get() = 10.dp

    open val initialVerticalOffset: Dp
        get() = 0.dp

    val surfaceElevation: MutableState<Dp> by lazy {
        mutableStateOf(initialSurfaceElevation)
    }
    val verticalOffset: MutableState<Dp> by lazy {
        mutableStateOf(initialVerticalOffset)
    }

    @Composable
    abstract fun build(dispatchIntent: DispatchIntent)

    @Immutable
    @Parcelize
    data class EnterCredentials(
        override val previousViewState: ViewState?,
        val userCredentials: UserCredentials,
        val userProfileError: UserProfileError? = null,
    ) : ViewState() {

        @Composable
        override fun build(dispatchIntent: DispatchIntent) {
            EnterCredentialsScreen(
                viewState = this,
                dispatchIntent = dispatchIntent,
            )
        }

        val emailAddress: String
            get() = userCredentials.emailAddress

        val password: String
            get() = userCredentials.password
    }

    @Immutable
    @Parcelize
    data class Authenticating(
        override val previousViewState: ViewState?,
    ) : ViewState() {

        override val initialSurfaceElevation: Dp
            get() = 5.dp

        override val surfaceShape: Shape
            get() = RoundedCornerShape(percent = 50)

        @Composable
        override fun build(dispatchIntent: DispatchIntent) {
            AuthenticatingScreen()
        }
    }

    @Immutable
    @Parcelize
    data class Profile(
        override val previousViewState: ViewState?,
        val userName: String,
        val emailAddress: String,
        val avatarImage: AvatarImage,
        val retrievingNewAvatarImage: Boolean,
    ) : ViewState() {

        sealed class AvatarImage : Parcelable, Wrapper<Any> {

            abstract val uuid: Long

            @Immutable
            @Parcelize
            data class FromCurrentAvatarUrl(
                val url: String,
                override val uuid: Long = System.nanoTime(),
            ) : AvatarImage() {
                override fun unwrap() = url
            }

            @Immutable
            @Parcelize
            data class FromNewAvatarUri(
                val uri: Uri,
                val confirmed: Boolean,
                override val uuid: Long = System.nanoTime(),
            ) : AvatarImage() {
                override fun unwrap() = uri
            }
        }

        override val initialSurfaceElevation: Dp
            get() = 20.dp

        @Composable
        override fun build(dispatchIntent: DispatchIntent) {
            ProfileScreen(
                viewState = this,
                dispatchIntent = dispatchIntent,
            )
        }
    }
}