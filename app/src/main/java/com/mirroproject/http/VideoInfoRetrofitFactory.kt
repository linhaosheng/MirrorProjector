package com.mirroproject.http

import android.util.Log
import com.mirroproject.config.AppInfo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by reeman on 2017/10/31.
 */
class VideoInfoRetrofitFactory {
    private var httpRequestData: HttpRequestData? = null

    constructor() {
        init()
    }

    fun init() {
        val retrofit = Retrofit.Builder()
                .baseUrl(AppInfo.BASE_URL)
                .client(providerOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        this.httpRequestData = retrofit.create(HttpRequestData::class.java)
    }

    fun providerOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message -> Log.i("VideoRetrofitFactory==", "okHttpClient===message===" + message) }).setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
    }

    fun providerHttpRequestData(): HttpRequestData? {
        return httpRequestData
    }
}