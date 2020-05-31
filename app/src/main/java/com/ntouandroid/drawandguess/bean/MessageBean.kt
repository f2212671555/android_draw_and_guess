package com.ntouandroid.drawandguess.bean

import com.google.gson.annotations.SerializedName

data class MessageBean(
    @SerializedName("type")
    var type: String?,
    @SerializedName("userId")
    var userId: String?,
    @SerializedName("roomId")
    var roomId: String?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("result")
    var result: Boolean?
)