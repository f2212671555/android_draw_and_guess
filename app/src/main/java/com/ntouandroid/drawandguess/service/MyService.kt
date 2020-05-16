package com.ntouandroid.drawandguess.service

import com.google.gson.Gson
import com.ntouandroid.drawandguess.bean.RoomBean
import com.ntouandroid.drawandguess.bean.UserBean
import com.ntouandroid.drawandguess.config.Config
import okhttp3.HttpUrl
import okhttp3.WebSocketListener

class MyService {
    companion object {
        fun connectWebSocket(webSocketListener: WebSocketListener, url: String) {
            MyWebSocket.createWebSocket(webSocketListener, url)
        }

        fun getRoomList(): MyRequest.Result {
            return MyRequest.sendGetRequest(
                HttpUrl.Builder()
                    .scheme("https")
                    .host(Config.HOST)
                    .addPathSegment(Config.ROOM_LIST)
                    .toString()
            )
        }

        fun createRoom(roomBean: RoomBean): MyRequest.Result {
            return MyRequest.sendPostRequest(
                HttpUrl.Builder()
                    .scheme("https")
                    .host(Config.HOST)
                    .addPathSegment(Config.CREATE_ROOM)
                    .toString(), Gson().toJson(roomBean)
            )
        }

        fun joinRoom(userBean: UserBean): MyRequest.Result {
            return MyRequest.sendPostRequest(
                HttpUrl.Builder()
                    .scheme("https")
                    .host(Config.HOST)
                    .addPathSegment(Config.JOIN_ROOM)
                    .toString(),Gson().toJson(userBean)
            )
        }

        fun quitRoom(userId: String, roomId: String): MyRequest.Result {
            return MyRequest.sendGetRequest(
                HttpUrl.Builder()
                    .scheme("https")
                    .host(Config.HOST)
                    .addPathSegment(Config.QUIT_ROOM)
                    .addQueryParameter(Config.USER_ID_KEY, userId)
                    .addQueryParameter(Config.ROOM_ID_KEY, roomId)
                    .toString()
            )
        }

        fun startDraw(roomId: String): MyRequest.Result {
            return MyRequest.sendGetRequest(
                HttpUrl.Builder()
                    .scheme("https")
                    .host(Config.HOST)
                    .addPathSegment(Config.START_DRAW)
                    .addQueryParameter(Config.ROOM_ID_KEY, roomId)
                    .toString()
            )
        }

    }
}