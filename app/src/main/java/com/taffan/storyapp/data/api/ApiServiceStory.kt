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

interface ApiServiceStory {
    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): FileUploadResponse

    @GET("stories")
    suspend fun getStories(): GetStoriesResponse

    @GET("stories/{id}")
    suspend fun getDetailStories(
        @Path("id") id: String
    ): DetailStoryResponse
}