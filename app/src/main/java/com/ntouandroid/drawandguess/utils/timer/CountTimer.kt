package com.ntouandroid.drawandguess.utils.timer

import android.os.CountDownTimer

class CountTimer(second: Long, countDown: Long) : CountDownTimer(second * 1000, countDown * 1000) {
    override fun onTick(millisUntilFinished: Long) {
        //test_tv.text = "seconds remaining: " + millisUntilFinished / 1000
    }

    override fun onFinish() {
        //test_tv.text = "done!"
    }


    fun startCount() {
        this.start()
    }

    fun cancelCount() {
        this.cancel()
    }
}