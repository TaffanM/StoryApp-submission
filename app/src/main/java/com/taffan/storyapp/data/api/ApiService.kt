package com.taffan.storyapp.data.api

import com.taffan.storyapp.data.response.DetailStoryResponse
import com.taffan.storyapp.data.response.GetStoriesResponse
import com.taffan.storyapp.data.response.LoginResponse
import com.taffan.storyapp.data.response.RegisterResponse
import com.taffan.storyapp.data.response.FileUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse


}