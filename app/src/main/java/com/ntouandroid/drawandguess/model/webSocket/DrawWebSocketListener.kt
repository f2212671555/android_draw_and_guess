package com.ntouandroid.drawandguess.model.webSocket

import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import okhttp3.HttpUrl
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class DrawWebSocketListener : WebSocketListener() {
    private var handler: Handler? = null
    private var url: HttpUrl? = null
    private var webSocket: WebSocket? = null

    fun getWebSocket(): WebSocket? {
        return webSocket
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("onClosed", "DrawWebSocketListener closed!!")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("onClosing", "DrawWebSocketListener closing!!")
//        handler?.sendMessage(handler!!.obtainMessage(2, "onClosing"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d("onFailure", "DrawWebSocketListener failure!!")
        handler?.sendMessage(handler!!.obtainMessage(1, "onFailure"))
        close()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        handler?.sendMessage(handler!!.obtainMessage(0, text))

    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("onOpen", "DrawWebSocketListener onOpen")
        this.webSocket = webSocket

    }

    fun close() {
        webSocket?.close(1000, "DrawWebSocketListener Connection closed")
    }

    fun setUrl(url: HttpUrl) {
        this.url = url
    }

    fun setHandler(handler: Handler) {
        this.handler = handler
    }
}