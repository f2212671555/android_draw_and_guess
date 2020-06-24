package com.ntouandroid.drawandguess.utils.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.ntouandroid.drawandguess.view.MainActivity
import com.ntouandroid.drawandguess.R

class Receiver : BroadcastReceiver() {

    companion object {
        const val CURRENT_CHANNEL_ID = 1
        const val DEFAULT_INT_VALUE = -1
        const val CLOCK_HOUR = "CLOCK_HOUR"
        const val CLOCKS_MINUTE = "CLOCKS_MINUTE"
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onReceive(context: Context?, intent: Intent?) {

        println("Receiver onReceive")
        if (context != null) {
            val hour = intent?.getIntExtra(
                CLOCK_HOUR,
                DEFAULT_INT_VALUE
            )
            val minute =
                intent?.getIntExtra(
                    CLOCKS_MINUTE,
                    DEFAULT_INT_VALUE
                )
            val title =
                 "現在時間 " + hour.toString() + ":" + minute.toString()
            val message = "來玩你畫我猜吧!!"
            sendNotification(
                context,
                title, message
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun sendNotification(context: Context, title: String, message: String) {
        val intent = Intent()
        intent.setClass(context, MainActivity::class.java)

        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        var notification: Notification? = null
        try {
            notification = getNotification(context, pendingIntent, title, message)
        } catch (e: Exception) {
        }

        if (notification != null) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(CURRENT_CHANNEL_ID, notification)
        }
    }

    private fun getNotification(
        context: Context,
        pendingIntent: PendingIntent?,
        title: String,
        message: String
    ): Notification? {
        var notification: Notification? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CURRENT_CHANNEL_ID.toString(),
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW
                )
                channel.setShowBadge(false)

                val notificationManager = context.getSystemService(NotificationManager::class.java)
                notificationManager!!.createNotificationChannel(channel)
                notification = Notification.Builder(context, CURRENT_CHANNEL_ID.toString())
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker(message)
                    .setWhen(System.currentTimeMillis())
                    .build()
            } else if (Build.VERSION.SDK_INT >= 16) {
                notification = Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker(message)
                    .setWhen(System.currentTimeMillis())
                    .build()
            }
        } catch (e: Exception) {
            return null
        }
        return notification
    }
}