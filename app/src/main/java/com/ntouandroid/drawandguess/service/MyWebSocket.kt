package com.ntouandroid.drawandguess.service

import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.*
import retrofit2.Retrofit
import java.time.Duration


class MyWebSocket {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun createWebSocket(webSocketListener: WebSocketListener, url: String): WebSocket {
            val request = Request.Builder().url(url).build()
            val okHttpClient = OkHttpClient.Builder().pingInterval(Duration.ofSeconds(10)).build()
            val realWebSocket = okHttpClient.newWebSocket(request, webSocketListener)

            okHttpClient.dispatcher.executorService.shutdown()

            return realWebSocket
        }
    }
}