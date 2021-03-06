package com.ntouandroid.drawandguess.model.bean

import com.google.gson.annotations.SerializedName

data class RoomBean(
    @SerializedName("roomId")
    var roomId: String?,
    @SerializedName("roomName")
    var roomName: String?,
    @SerializedName("users")
    var usersList: MutableList<UserBean>?,
    @SerializedName("result")
    var result: Boolean?
)