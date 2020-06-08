package com.ntouandroid.drawandguess.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.os.Message
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

class UIHandler {
    companion object{
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun setStatusBarColor(activity: Activity) {

            // Followed by google doc.
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.statusBarColor = ContextCompat.getColor(activity, android.R.color.transparent)

            // For not opaque(transparent) color.
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        }

    }
}