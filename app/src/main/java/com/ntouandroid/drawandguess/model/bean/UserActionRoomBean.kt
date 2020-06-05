package com.ntouandroid.drawandguess.model.bean

import com.google.gson.annotations.SerializedName

data class UserActionRoomBean(
    @SerializedName("roomId")
    var roomId: String?,
    @SerializedName("userId")
    var userId: String?,
    @SerializedName("userName")
    var userName: String?,
    @SerializedName("result")
    var result: Boolean?
)