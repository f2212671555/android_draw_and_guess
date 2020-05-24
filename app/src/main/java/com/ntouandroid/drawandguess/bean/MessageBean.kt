package com.ntouandroid.drawandguess.bean

import com.google.gson.annotations.SerializedName

data class MessageBean(
    @SerializedName("user")
    var userBean: UserBean?,
    @SerializedName("message")
    var message: String?
)