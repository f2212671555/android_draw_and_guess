package com.ntouandroid.drawandguess.model.bean

import com.google.gson.annotations.SerializedName

data class TopicDetailBean(
    @SerializedName("category")
    var category: String?,
    @SerializedName("topic")
    var topic: String?,
    @SerializedName("currentDrawUserId")
    var currentDrawUserId: String?,
    @SerializedName("nextDrawUserId")
    var nextDrawUserId: String?,
    @SerializedName("result")
    var result: Boolean?
)