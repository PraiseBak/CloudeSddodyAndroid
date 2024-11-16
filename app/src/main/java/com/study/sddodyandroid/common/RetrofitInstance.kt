package com.study.sddodyandroid.common

import android.content.Context
import android.content.SharedPreferences
import com.study.sddodyandroid.R
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private lateinit var SERVER_URL : String

    private lateinit var sharedPreferences: SharedPreferences // lateinit으로 선언

    private lateinit var okHttpClient: OkHttpClient

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService : APIService by lazy{
        retrofit.create(APIService:: class.java)
    }


    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE)
        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor(sharedPreferences,context))
            .retryOnConnectionFailure(false)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        SERVER_URL = context.getString(R.string.SERVER_IP)

    }
}
