package com.example.quicknews.api

import android.annotation.SuppressLint
import com.example.quicknews.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// RetrofitInstance provides a singleton instance of Retrofit for network operations.
class RetrofitInstance {

    @SuppressLint("StaticFieldLeak") // SuppressLint annotation for static fields.
    companion object {
        // Lazy initialization for Retrofit instance to ensure it's created only when needed.
        private val retrofit by lazy {
            // Configure HTTP logging for debugging purposes.
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Log full body of requests and responses.
            }

            // Create OkHttpClient with logging interceptor.
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            // Build Retrofit instance with the base URL and Gson converter.
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        // Provide singleton instance of NewsAPI for making network requests.
        val api: NewsAPI by lazy {
            retrofit.create(NewsAPI::class.java)
        }
    }
}
