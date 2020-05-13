package com.ntouandroid.drawandguess.service

import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class MyRequest {
    data class Result(
        var result: Boolean = false,
        var jsonString: String = ""
    )

    companion object {
        fun sendGetRequest(url: String): Result {
            val okHttpClient = OkHttpClient()
            var result = Result()
            val request = Request.Builder()
                .url(url)
                .build()

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    result = Result(false, e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    val resStr = response.body?.string()
                    if (resStr != null) {
                        result = Result(true, resStr)
                    }
                }
            })
            return result
        }

        fun sendPostRequest(url: String, reqJsonString: String): Result {
            val okHttpClient = OkHttpClient()
            var result = Result()
            val requestBody = reqJsonString.toRequestBody()

            val request = Request.Builder()
                .method("POST", requestBody)
                .url(url)
                .build()

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    result = Result(false, e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    val resStr = response.body?.string()
                    if (resStr != null) {
                        result = Result(true, resStr)
                    }
                }
            })
            return result
        }


    }

}
