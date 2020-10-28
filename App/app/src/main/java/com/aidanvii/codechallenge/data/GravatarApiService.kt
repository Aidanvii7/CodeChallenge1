package com.aidanvii.codechallenge.data

import com.slack.eithernet.ApiResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.*

internal interface GravatarApiService {

    @GET("{userEmailHash}.json")
    suspend fun getUserProfile(
        @Path("userEmailHash") userEmailHash: String,
    ): ApiResult<GetUserProfileResponse, Unit>

    @JsonClass(generateAdapter = true)
    class GetUserProfileResponse(
        @Json(name = "entry")
        val entries: List<Entry>
    ) {
        @JsonClass(generateAdapter = true)
        class Entry(
            @Json(name = "thumbnailUrl")
            val imageUrl: String,
        )
    }
}