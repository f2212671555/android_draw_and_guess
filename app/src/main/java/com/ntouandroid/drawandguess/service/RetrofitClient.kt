package com.ntouandroid.drawandguess.service

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class RetrofitClient {
    companion object {
        fun <T> getService(serviceClass: Class<T>, baseUrl: String): T {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build().create(serviceClass)
        }
    }
}