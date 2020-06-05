package com.ntouandroid.drawandguess.config

class Config {
    companion object {
        const val WS_SCHEME = "wss://"
        const val HTTP_SCHEME = "https://"
        const val HOST = "draw-and-guess-ntou.herokuapp.com"
//        const val HOST ="79ae1976.ngrok.io"
        const val ROOM_LIST = "/room/list"
        const val ROOM_USERS= "/room/users"
        const val CREATE_ROOM = "/room/create"
        const val JOIN_ROOM = "/room/join"
        const val QUIT_ROOM = "/room/quit"
        const val START_DRAW = "/room/startDraw"
        const val ROOM_TOPIC = "/room/topic"
        const val WS_ROOM = "/ws/room/"
        const val WS_DRAW = "/ws/draw/"
        const val USER_ID_KEY = "userId"
        const val ROOM_ID_KEY = "roomId"
        const val USER_NAME_KEY = "userName"
    }

}