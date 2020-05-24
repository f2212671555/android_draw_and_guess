package com.ntouandroid.drawandguess.webSocket

import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import com.ntouandroid.drawandguess.config.Config
import com.ntouandroid.drawandguess.service.MyWebSocket
import okhttp3.*
import java.time.Duration

class DrawWebSocketListener : WebSocketListener() {
    private lateinit var handler: Handler
    private var url: HttpUrl? = null
    private var isConnected: Boolean = false
    private var webSocket: WebSocket? = null

    fun getWebSocket():WebSocket? {
        return webSocket
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("onClosed", "RoomWebSocketListener closed!!")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("onClosing", "RoomWebSocketListener closing!!")
        webSocket.close(1000, null)
        Log.d("onClosing", "Code: $code, Reason: $reason")
//        this.isConnected = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d("onFailure", "RoomWebSocketListener failure!!")
        webSocket.close(1000, null)
        close()
        // reconnect...
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("onMessage", text)

    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("onOpen", "hi")
        this.isConnected = true
        this.webSocket = webSocket

    }

    private fun close() {
        webSocket?.close(1000, "Connection closed")
    }

    fun setUrl(url: HttpUrl) {
        this.url = url
    }
    
    fun setHandler(handler: Handler) {
        this.handler = handler
    }
}