package com.example.chs

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class AuthClient {
    private val REQUEST_TIMEOUT = 60

    public val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://munteanu.codes:5010/")
//                    .baseUrl("http://192.168.0.3:5000/")
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit
            .create(AuthService::class.java)
    }

    public val okHttpClient: OkHttpClient by lazy {
        val httpClient = OkHttpClient()
            .newBuilder()
            .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .callTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)

        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")

            if (PreferencesController.token.isNotEmpty()) {
                requestBuilder
                    .addHeader("Authorization","Bearer " + PreferencesController.token)
            }

            val request = requestBuilder.build()
            chain.proceed(request)
        }

        val interceptor = HttpLoggingInterceptor()
        interceptor.level =HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(interceptor)
        httpClient.build()
    }
}