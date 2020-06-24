package com.ntouandroid.drawandguess.utils.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import java.util.*

class NotificationUtil {
    companion object {

        fun startClock(context: Context, alarmManager: AlarmManager, clockBean: ClockBean) {
            setNextAlarm(
                context,
                alarmManager,
                clockBean.pendingIntent,
                clockBean
            )
            println("NotificationUtil startClock")
        }

        fun stopClock(context: Context, alarmManager: AlarmManager, clockBean: ClockBean) {
            alarmManager.cancel(clockBean.pendingIntent)
            println("NotificationUtil stopClock")
        }

        fun setPendingIntent(context: Context, hour: Int, minute: Int): PendingIntent {
            val intent = Intent(context, Receiver::class.java)
            intent.putExtra(Receiver.CLOCK_HOUR, hour)
            intent.putExtra(Receiver.CLOCKS_MINUTE, minute)
            return PendingIntent.getBroadcast(
                context,
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        private fun setNextAlarm(
            context: Context,
            alarmManager: AlarmManager?,
            pendingIntent: PendingIntent?, clockBean: ClockBean
        ) {
            val c: Calendar = Calendar.getInstance()
            c.set(Calendar.HOUR_OF_DAY, clockBean.hour)
            c.set(Calendar.MINUTE, clockBean.minute)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.MILLISECOND, 0)

            if (System.currentTimeMillis() > c.timeInMillis) {
                c.add(Calendar.DATE, 1)
            }

            if (Build.VERSION.SDK_INT >= 19) {
                alarmManager?.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
            } else {
                alarmManager?.set(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
            }
        }
    }

}