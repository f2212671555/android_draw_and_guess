package com.ntouandroid.drawandguess.listener

import android.app.Activity
import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner


class ArchLifecycleApp : Application(), LifecycleObserver {

    companion object{
        const val QUIT_ROOM ="QUIT_ROOM"
        const val JOIN_ROOM ="JOIN_ROOM"
        var userStatus = "QUIT_ROOM"
    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        println("onAppBackgrounded")
//        println(userStatus)
//        if(userStatus == JOIN_ROOM){
//            println("onAppBackgrounded    JOIN_ROOM")
//        } else if (userStatus == QUIT_ROOM){
//            println("onAppBackgrounded    QUIT_ROOM")
//        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        println("onAppForegrounded")
    }

}