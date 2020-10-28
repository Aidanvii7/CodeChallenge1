package com.aidanvii.codechallenge.data

import com.slack.eithernet.ApiResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.*

internal interface UserApiService {

    @POST("sessions/new")
    suspend fun getSessionToken(
        @Body userCredentials: UserCredentials,
    ): ApiResult<UserIdWithSessionToken, Unit> // TODO: failure types!

    @GET("users/{userId}")
    suspend fun getUserDetails(
        @Header("Authorization") sessionToken: String,
        @Path("userId") userId: String,
    ): ApiResult<UserDetails, Unit>

    // TODO figure this out
    @POST("users/{userId}/avatar")
    suspend fun updateAvatar(
        @Header("Authorization") sessionToken: String,
        @Body avatarData: AvatarData,
    ): ApiResult<AvatarUrl, Unit>

    @JsonClass(generateAdapter = true)
    class UserCredentials(
        @Json(name = "email")
        val emailAddress: String,
        @Json(name = "password")
        val password: String,
    )

    @JsonClass(generateAdapter = true)
    data class UserIdWithSessionToken(
        @Json(name = "userid")
        val userId: String,
        @Json(name = "token")
        val sessionToken: String,
    )

    @JsonClass(generateAdapter = true)
    class UserDetails(
        @Json(name = "email")
        val emailAddress: String,
        @Json(name = "avatar_url")
        val avatarImageUrl: String,
    )

    @JsonClass(generateAdapter = true)
    class AvatarData(
        @Json(name = "avatar")
        val base64AvatarData: String, // TODO figure this out
    )

    @JsonClass(generateAdapter = true)
    class AvatarUrl(
        @Json(name = "avatar_url")
        val avatarImageUrl: String,
    )
}
