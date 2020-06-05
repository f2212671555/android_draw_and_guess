package com.ntouandroid.drawandguess.webSocket

import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.ntouandroid.drawandguess.bean.MessageBean
import com.ntouandroid.drawandguess.bean.UserBean
import okhttp3.HttpUrl
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class RoomWebSocketListener(val userBean: UserBean) : WebSocketListener() {
    private var handler: Handler? = null
    private var url: HttpUrl? = null
    private var isConnected: Boolean = false
    private var webSocket: WebSocket? = null

    fun getWebSocket(): WebSocket? {
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
        handler?.sendMessage(handler!!.obtainMessage(0, text))
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("onOpen", "RoomWebSocketListener onOpen")
        this.isConnected = true
        this.webSocket = webSocket

    }

    fun close() {
        webSocket?.close(1000, "RoomWebSocketListener Connection closed")
    }

    fun setUrl(url: HttpUrl) {
        this.url = url
    }

    fun setHandler(handler: Handler) {
        this.handler = handler
    }

    fun sendMessage(message: String) {
        this.webSocket?.send(message)
    }

    fun sendMessage(message: MessageBean) {
        this.webSocket?.send(Gson().toJson(message))
    }


}