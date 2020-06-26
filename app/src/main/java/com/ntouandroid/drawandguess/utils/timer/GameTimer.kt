package com.ntouandroid.drawandguess.utils.timer

import android.os.Handler

class GameTimer(private var timerBarController: TimerBarController){
    interface TimerBarController{
        fun timerOnUpdate()
        fun timesUp()
    }

    var secondsCount = 40f
    var maxTimeInSeconds = secondsCount
    var isOnStart = false
    private var stopTimer = false

    private lateinit var timerThread: Thread
    private var handlerUI = Handler()
    private lateinit var runnable: Runnable

    fun startTimer() {
        if(!isOnStart){
            isOnStart = true

            timerThread = Thread{
                while(secondsCount>=0 && !stopTimer){
                    Thread.sleep(10) // 0.01 second
                    secondsCount -= 0.01f
                }
                if(!stopTimer){
                    handlerUI.post{
                        timerBarController.timesUp()
                        stopTimer()
                    }
                }
                println("timer thread end")
            }

            runnable = Runnable {
                timerBarController.timerOnUpdate()
                handlerUI.postDelayed(runnable, 10)
            }

            stopTimer = false
            timerThread.start()
            handlerUI.postDelayed(runnable, 10)
        }
    }

    fun stopTimer() {
        stopTimer = true
        isOnStart = false
        handlerUI.removeCallbacks(runnable)
    }

    fun cancelTimer() {

    }
}