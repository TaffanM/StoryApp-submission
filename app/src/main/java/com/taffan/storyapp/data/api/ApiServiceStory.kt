package com.taffan.storyapp.data.api

import com.taffan.storyapp.data.response.DetailStoryResponse
import com.taffan.storyapp.data.response.FileUploadResponse
import com.taffan.storyapp.data.response.GetStoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceStory {
    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): FileUploadResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): GetStoriesResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location : Int = 1
    ): GetStoriesResponse

    @GET("stories/{id}")
    suspend fun getDetailStories(
        @Path("id") id: String
    ): DetailStoryResponse

}