package com.ntouandroid.drawandguess.model.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.ntouandroid.drawandguess.config.Config
import com.ntouandroid.drawandguess.model.webSocket.DrawWebSocketListener
import com.ntouandroid.drawandguess.model.webSocket.RoomWebSocketListener
import okhttp3.*
import java.time.Duration


class MyWebSocket {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun createRoomWebSocket(
            webSocketListener: RoomWebSocketListener,
            roomId: String,
            userId: String
        ): WebSocket {
            val url = HttpUrl.Builder().scheme("https").host(Config.HOST)
                .addPathSegment(Config.WS_ROOM)
                .addPathSegment(roomId)
                .addQueryParameter(Config.USER_ID_KEY, userId).build()
            webSocketListener.setUrl(url)
            val request = Request.Builder().url(url).build()
            val okHttpClient = OkHttpClient.Builder().pingInterval(Duration.ofSeconds(10)).build()
            val realWebSocket = okHttpClient.newWebSocket(request, webSocketListener)

            okHttpClient.dispatcher.executorService.shutdown()

            return realWebSocket
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun createDrawWebSocket(
            webSocketListener: DrawWebSocketListener,
            roomId: String,
            userId: String
        ): WebSocket {
            val url = HttpUrl.Builder().scheme("https").host(Config.HOST)
                .addPathSegment(Config.WS_DRAW)
                .addPathSegment(roomId)
                .addQueryParameter(Config.USER_ID_KEY, userId).build()
            webSocketListener.setUrl(url)
            val request = Request.Builder().url(url).build()
            val okHttpClient = OkHttpClient.Builder().pingInterval(Duration.ofSeconds(10)).build()
            val realWebSocket = okHttpClient.newWebSocket(request, webSocketListener)

            okHttpClient.dispatcher.executorService.shutdown()

            return realWebSocket
        }
    }
}