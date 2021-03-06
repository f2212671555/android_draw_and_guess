package com.ntouandroid.drawandguess.model.bean

import com.google.gson.annotations.SerializedName

data class UserBean(
    @SerializedName("roomId")
    var roomId: String?,
    @SerializedName("userId")
    var userId: String?,
    @SerializedName("userName")
    var userName: String?,
    @SerializedName("role")
    var role: String?
)