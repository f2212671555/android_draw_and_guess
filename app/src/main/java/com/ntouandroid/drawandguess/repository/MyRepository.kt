package com.ntouandroid.drawandguess.repository

import com.ntouandroid.drawandguess.bean.RoomBean
import com.ntouandroid.drawandguess.bean.UserBean
import com.ntouandroid.drawandguess.config.Config
import com.ntouandroid.drawandguess.service.MyServiceInterface
import com.ntouandroid.drawandguess.service.RetrofitClient

class MyRepository {
    private val service: MyServiceInterface =
        RetrofitClient.getService(MyServiceInterface::class.java, Config.HTTP_SCHEME + Config.HOST)

    suspend fun getRoomList() = service.getRoomList()
    suspend fun getRoomUsers(roomId: String) = service.getRoomUsers(roomId)
    suspend fun createRoom(roomBean: RoomBean) = service.createRoom(roomBean)
    suspend fun joinRoom(userBean: UserBean) = service.joinRoom(userBean)
//    suspend fun quitRoom(userId: String, roomId: String) = service.quitRoom(userId, roomId)
    suspend fun startDraw(roomId: String) = service.startDraw(roomId)
}