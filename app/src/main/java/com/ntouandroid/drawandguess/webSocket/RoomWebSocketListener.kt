package com.ntouandroid.drawandguess.webSocket

import android.os.Handler
import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class RoomWebSocketListener : WebSocketListener() {
    private lateinit var handler: Handler
    companion object{
        lateinit var WEBSOCKET: WebSocket
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("onMessage", text)
        handler.sendMessage(handler.obtainMessage(0,text))
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("onOpen", "hi")
        WEBSOCKET = webSocket

    }

    fun setHandler(handler: Handler) {
        this.handler = handler
    }
}