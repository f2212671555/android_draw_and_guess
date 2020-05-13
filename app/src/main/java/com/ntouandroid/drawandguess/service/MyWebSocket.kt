package com.ntouandroid.drawandguess.service

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocketListener

class MyWebSocket {
    companion object {
        fun createWebSocket(webSocketListener: WebSocketListener, url: String) {
            val request = Request.Builder().url(url).build()
            val okHttpClient = OkHttpClient()
            okHttpClient.newWebSocket(request, webSocketListener)

            okHttpClient.dispatcher.executorService.shutdown()
        }
    }
}