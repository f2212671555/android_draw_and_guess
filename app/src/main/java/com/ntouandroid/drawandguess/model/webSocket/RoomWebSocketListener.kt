package com.ntouandroid.drawandguess.model.webSocket

import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.ntouandroid.drawandguess.model.bean.MessageBean
import com.ntouandroid.drawandguess.model.bean.UserBean
import okhttp3.HttpUrl
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class RoomWebSocketListener(val userBean: UserBean) : WebSocketListener() {
    private var handler: Handler? = null
    private var url: HttpUrl? = null
    private var webSocket: WebSocket? = null
    private var flag: String = ""

    companion object {
        const val QUIT_FLAG = "QUIT_FLAG"
    }

    fun getWebSocket(): WebSocket? {
        return webSocket
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("onClosed", "RoomWebSocketListener closed!!")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("onClosing", "RoomWebSocketListener closing!!")
//        handler?.sendMessage(handler!!.obtainMessage(2, "onClosing"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d("onFailure", "RoomWebSocketListener failure!!")
        handler?.sendMessage(handler!!.obtainMessage(1, "onFailure"))
        this.close()
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("onMessage", text)
        handler?.sendMessage(handler!!.obtainMessage(0, text))
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("onOpen", "RoomWebSocketListener onOpen")
        this.webSocket = webSocket
        if (flag == QUIT_FLAG) {
            this.close()
        }

    }

    fun close() {
        Log.d("close", "RoomWebSocketListener call close()!!")
        webSocket?.close(1000, "RoomWebSocketListener Connection closed")
    }

    fun setUrl(url: HttpUrl) {
        this.url = url
    }

    fun setFlag(flag: String) {
        this.flag = flag
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