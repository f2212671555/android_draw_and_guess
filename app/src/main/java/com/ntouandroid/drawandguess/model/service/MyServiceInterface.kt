package com.ntouandroid.drawandguess.model.service

import com.ntouandroid.drawandguess.model.bean.RoomBean
import com.ntouandroid.drawandguess.model.bean.TopicDetailBean
import com.ntouandroid.drawandguess.model.bean.UserActionRoomBean
import com.ntouandroid.drawandguess.model.bean.UserBean
import com.ntouandroid.drawandguess.config.Config
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MyServiceInterface {
    @GET(Config.ROOM_LIST)
    suspend fun getRoomList(): List<RoomBean>

    @GET(Config.ROOM_USERS)
    suspend fun getRoomUsers(@Query(Config.ROOM_ID_KEY) roomId: String): RoomBean

    @POST(Config.CREATE_ROOM)
    suspend fun createRoom(@Body roomBean: RoomBean): RoomBean

    @POST(Config.JOIN_ROOM)
    suspend fun joinRoom(@Body userBean: UserBean): UserActionRoomBean

    @GET(Config.START_DRAW)
    suspend fun startDraw(@Query(Config.ROOM_ID_KEY) roomId: String): TopicDetailBean

    @GET(Config.ROOM_TOPIC)
    suspend fun getRoomTopic(@Query(Config.ROOM_ID_KEY) roomId: String): TopicDetailBean
}