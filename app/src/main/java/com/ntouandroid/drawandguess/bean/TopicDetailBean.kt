package com.ntouandroid.drawandguess.bean

import com.google.gson.annotations.SerializedName

data class TopicDetailBean(
    @SerializedName("category")
    var category: String?,
    @SerializedName("topic")
    var topic: String?,
    @SerializedName("userId")
    var userId: String?
)