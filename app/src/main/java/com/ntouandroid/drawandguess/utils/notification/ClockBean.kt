package com.ntouandroid.drawandguess.utils.notification

import android.app.PendingIntent

data class ClockBean(
    var hour: Int,
    var minute: Int,
    var pendingIntent: PendingIntent
)