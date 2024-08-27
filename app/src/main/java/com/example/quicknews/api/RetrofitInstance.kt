package com.example.quicknews.api

import com.example.quicknews.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//Retrofit is a type-safe HTTP client library for Android and Java, developed by Square
class RetrofitInstance {

    companion object{ //What we will write it will static or fixed values.

        private val retrofit by lazy {

            val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        }

        val api by lazy {
            retrofit.create(NewsAPI::class.java)
        }
    }

}