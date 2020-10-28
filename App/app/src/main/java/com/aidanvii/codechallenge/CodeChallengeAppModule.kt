@file:Suppress(removeExplicitTypeArguments)

package com.aidanvii.codechallenge

import androidx.activity.ComponentActivity
import com.aidanvii.codechallenge.data.GravatarApiService
import com.aidanvii.codechallenge.data.UserApiService
import com.aidanvii.codechallenge.domain.UserProfileRepository
import com.aidanvii.codechallenge.domain.UserCredentials
import com.aidanvii.codechallenge.data.UserDetailsStore
import com.aidanvii.codechallenge.domain.UserProfile
import com.aidanvii.codechallenge.effects.ChooseAvatarPhotoSideEffect
import com.aidanvii.codechallenge.state.*
import com.aidanvii.codechallenge.utils.CoroutineDispatchers
import com.aidanvii.codechallenge.utils.removeExplicitTypeArguments
import com.slack.eithernet.ApiResultCallAdapterFactory
import com.slack.eithernet.ApiResultConverterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.GlobalScope
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val codeChallengeAppModule = module {

    single<CoroutineDispatchers> { CoroutineDispatchers.Default }

    single<AppStateProcessor> {
        AppStateProcessor(
            // TODO: Restore this somehow!!
            initialAppState = AppState(
                userProfile = UserProfile(
                    userId = "",
                    emailAddress = "",
                    avatarPhotoUrl = "",
                ),
                viewState = ViewState.EnterCredentials(
                    previousViewState = null,
                    userCredentials = UserCredentials(
                        emailAddress = "",
                        password = "",
                    ),
                ),
            ),
            coroutineDispatchers = get(),
        )
    }

    single<IntentToViewState> {
        val appStateProcessor = get<AppStateProcessor>()
        IntentToViewState(
            coroutineScope = GlobalScope, // TODO: use something else?
            viewStateProvider = appStateProcessor,
            dispatchIntentAsync = appStateProcessor,
        )
    }

    single<DispatchIntent> { get<IntentToViewState>() }

    factory { (activity: ComponentActivity) ->
        val appStateProcessor = get<AppStateProcessor>()
        ChooseAvatarPhotoSideEffect(
            activity = activity,
            coroutineDispatchers = get(),
            actionsProvider = appStateProcessor,
            dispatchResultAsync = appStateProcessor,
        )
    }

    single<Moshi> {
        Moshi.Builder().build()
    }

    single<MoshiConverterFactory> {
        MoshiConverterFactory.create(get())
    }

    single<OkHttpClient> {
        OkHttpClient
            .Builder()
            .build()
    }

    single<UserApiService> {
        Retrofit
            .Builder()
            .client(get())
            .baseUrl(BuildConfig.USER_PROFILE_BASE_URL)
            .addConverterFactory(ApiResultConverterFactory)
            .addCallAdapterFactory(ApiResultCallAdapterFactory)
            .addConverterFactory(get<MoshiConverterFactory>())
            .build()
            .create(UserApiService::class.java)
    }

    single<GravatarApiService> {
        Retrofit
            .Builder()
            .client(get())
            .baseUrl(BuildConfig.GRAVATAR_BASE_URL)
            .addConverterFactory(ApiResultConverterFactory)
            .addCallAdapterFactory(ApiResultCallAdapterFactory)
            .addConverterFactory(get<MoshiConverterFactory>())
            .build()
            .create(GravatarApiService::class.java)
    }

    single<UserDetailsStore> {
        UserDetailsStore(
            applicationContext = get(),
        )
    }

    single<UserProfileRepository> {
        UserProfileRepository(
            applicationContext = get(),
            userApiService = get(),
            gravatarApiService = get(),
            userDetailsStore = get(),
            coroutineDispatchers = get(),
        )
    }
}