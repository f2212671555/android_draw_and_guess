package com.ntouandroid.drawandguess.bean

import com.google.gson.annotations.SerializedName

data class RoomBean(
    @SerializedName("roomId")
    var roomId: String?,
    @SerializedName("roomName")
    var roomName: String?
)