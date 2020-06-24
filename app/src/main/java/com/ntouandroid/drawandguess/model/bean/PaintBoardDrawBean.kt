package com.ntouandroid.drawandguess.model.bean

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

data class PaintBoardDrawBean(
    @SerializedName("action")
    var action: String?,
    @SerializedName("bitmap")
    var bitmap: Bitmap?
)