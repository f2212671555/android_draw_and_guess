package com.ntouandroid.drawandguess.model.repository

import com.ntouandroid.drawandguess.model.bean.RoomBean
import com.ntouandroid.drawandguess.model.bean.UserBean
import com.ntouandroid.drawandguess.config.Config
import com.ntouandroid.drawandguess.model.service.MyServiceInterface
import com.ntouandroid.drawandguess.model.service.RetrofitClient

class MyRepository {
    private val service: MyServiceInterface =
        RetrofitClient.getService(
            MyServiceInterface::class.java, Config.HTTP_SCHEME + Config.HOST)

    suspend fun getRoomList() = service.getRoomList()
    suspend fun getRoomUsers(roomId: String) = service.getRoomUsers(roomId)
    suspend fun createRoom(roomBean: RoomBean) = service.createRoom(roomBean)
    suspend fun joinRoom(userBean: UserBean) = service.joinRoom(userBean)
    suspend fun startDraw(roomId: String) = service.startDraw(roomId)
    suspend fun getRoomTopic(roomId: String) = service.getRoomTopic(roomId)
}