package com.example.hikigai.data.remote

import com.example.hikigai.data.model.Photo
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/*
File : 
Description : 

Author : Neev Gaur 

Todo >
*/
interface PhotoApiService {
    @GET("photos")
    suspend fun getPhotos(
        @Query("_page") page: Int,
        @Query("_limit") limit: Int
    ): Response<List<Photo>>

    companion object {
        private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

        fun create(): PhotoApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PhotoApiService::class.java)
        }
    }
}