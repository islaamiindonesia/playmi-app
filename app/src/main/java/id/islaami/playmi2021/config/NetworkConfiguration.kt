package id.islaami.playmi2021.config

import id.islaami.playmi2021.BuildConfig
import id.islaami.playmi2021.data.cache.UserCache
import id.islaami.playmi2021.util.HEADER_TOKEN_KEY
import id.islaami.playmi2021.util.RELEASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

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
