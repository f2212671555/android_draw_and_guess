package com.ntouandroid.drawandguess.bean

import com.google.gson.annotations.SerializedName

data class UserBean(
    @SerializedName("roomId")
    var roomId: String?,
    @SerializedName("userId")
    var userId: String?,
    @SerializedName("userName")
    var userName: String?
)