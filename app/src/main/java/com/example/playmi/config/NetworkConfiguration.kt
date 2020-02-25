package com.example.playmi.config

import android.util.Log
import com.example.playmi.BuildConfig
import com.example.playmi.data.cache.UserCache
import com.example.playmi.util.HEADER_TOKEN_KEY
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private const val RELEASE_URL = "http://156.67.220.68/index.php/api/"
//private const val DEV_URL = "https://cryptic-sierra-40696.herokuapp.com/"

private fun httpClient(userCache: UserCache): OkHttpClient {
    val clientBuilder = OkHttpClient.Builder()
        .readTimeout(1, TimeUnit.MINUTES)
        .connectTimeout(1, TimeUnit.MINUTES)

    clientBuilder.addInterceptor { chain ->
        // Add Token to every http request
        val request =
            chain.request().newBuilder().addHeader(HEADER_TOKEN_KEY, userCache.headerToken).build()

        val response = chain.proceed(request)

        // Save new Token from response
        response.header(HEADER_TOKEN_KEY)?.let { token -> userCache.headerToken = token }

        response
    }

    val httpLoggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)

    if (BuildConfig.DEBUG) {
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(httpLoggingInterceptor)
    }

    return clientBuilder.build()
}

fun createRetrofitClient(userCache: UserCache): Retrofit =
    Retrofit.Builder()
        .baseUrl(RELEASE_URL)
        .client(httpClient(userCache))
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
