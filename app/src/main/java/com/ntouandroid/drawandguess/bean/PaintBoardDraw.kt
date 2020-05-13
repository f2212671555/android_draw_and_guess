package com.ntouandroid.drawandguess.bean

import com.google.gson.annotations.SerializedName

data class PaintBoardDraw(
    @SerializedName("drawShape")
    var drawShape: String?,
    @SerializedName("startX")
    var startX: Float = 0f,
    @SerializedName("startY")
    var startY: Float = 0f,
    @SerializedName("stopX")
    var stopX: Float = 0f,
    @SerializedName("stopY")
    var stopY: Float = 0f
)