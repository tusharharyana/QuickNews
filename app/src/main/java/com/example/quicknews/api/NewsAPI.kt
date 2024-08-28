package com.example.quicknews.api

import com.example.quicknews.models.NewsResponse
import com.example.quicknews.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("v2/everything")

    suspend fun getHeadlines(

        @Query("q")
        searchQuery: String,

        @Query("page")
        pageNumber: Int = 1,

        @Query("apikey")
        apiKey: String = API_KEY

    ):Response<NewsResponse> //NewsResponse to hondle HTTPS responses

    @GET("v2/everything") //Everything search for search function
    suspend fun searchForNews(

        @Query("q")
        searchQuery: String,

        @Query("page")
        pageNumber: Int = 1,

        @Query("apiKey")
        apiKey: String = API_KEY

    ):Response<NewsResponse>

}