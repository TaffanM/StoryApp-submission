package com.taffan.storyapp.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import com.taffan.storyapp.BuildConfig
import com.taffan.storyapp.BuildConfig.BASE_URL
import com.taffan.storyapp.preferences.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory



class ApiConfigStory {
    companion object {
        fun getApiService(userPreferences: UserPreferences): ApiServiceStory {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val authInterceptor = Interceptor { chain ->
                val req = chain.request()
                val user = runBlocking {
                    userPreferences.getUser().first()
                }
                val requestHeaders = req.newBuilder()
                    .addHeader("Authorization", "Bearer ${user?.token}")
                    .build()
                chain.proceed(requestHeaders)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiServiceStory::class.java)
        }
    }
}

