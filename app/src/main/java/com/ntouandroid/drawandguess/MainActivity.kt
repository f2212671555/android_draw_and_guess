package com.ntouandroid.drawandguess

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ntouandroid.drawandguess.filter.NameInputFilter
import com.ntouandroid.drawandguess.utils.InternetJudge
import com.ntouandroid.drawandguess.utils.UIHandler
import com.ntouandroid.drawandguess.utils.notification.ClockBean
import com.ntouandroid.drawandguess.utils.notification.NotificationUtil


class MainActivity : AppCompatActivity() {

    lateinit var bt_Test: Button
    lateinit var Bt_Start: Button
    lateinit var Et_Name: EditText
    lateinit var app_context: Context

    companion object {
        const val USER_ID = "USER_ID"
        const val USER_NAME = "USER_NAME"
        const val ROOM_ID = "ROOM_ID"
        const val ROOM_NAME = "ROOM_NAME"
        const val ROOM_ROLE = "ROOM_ROLE"
        const val ROOM_ROLE_MANAGER = "ROOM_ROLE_MANAGER"
        const val ROOM_ROLE_GENERAL_MEMBER = "ROOM_ROLE_GENERAL_MEMBER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        UIHandler.setStatusBarColor(this)

        Bt_Start = findViewById(R.id.button_start)
        Et_Name = findViewById(R.id.Et_Name)

        Et_Name.filters = arrayOf(NameInputFilter())
        Bt_Start.setOnClickListener {
            if (InternetJudge.isInternetAvailable(this)) {
                nextpagecheck()
            } else {
                showDialog("網路連接異常", "請檢查是否有連接網路！！")
            }
        }
        app_context = applicationContext
        initNotification()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (currentFocus != null && currentFocus!!.windowToken != null) {
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onRestart() {
        super.onRestart()
        println("MainActivity onRestart")
        Et_Name.setText("")
    }

    override fun onBackPressed() {
        showQuitAppDialog("確定要離開APP嗎？", "")
    }

    private fun quitApp() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

    private fun showQuitAppDialog(title: String, message: String) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("確認") { _, _ ->
            quitApp()
        }

        builder.setNegativeButton("取消") { dialog, _ ->
            dialog.dismiss()
        }

        // create dialog and show it
        val dialog = builder.create()
        dialog.show()
    }

    private fun showDialog(title: String, message: String) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("確認") { dialog, _ ->
            dialog.dismiss()
        }

        builder.setNegativeButton("取消") { dialog, _ ->
            dialog.dismiss()
        }

        // create dialog and show it
        val dialog = builder.create()
        dialog.show()
    }

    fun nextpagecheck() {
        val name = Et_Name.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "請輸入名字", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this@MainActivity, StartActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.putExtra(USER_NAME, name)
        startActivity(intent)
    }

    fun btn_test_click() {
        var intent = Intent(this@MainActivity, MyTestActivity::class.java)
        startActivity(intent)
    }
    private fun initNotification(){
        val pendingIntent1 = NotificationUtil.setPendingIntent(this@MainActivity, 12, 0)
        val pendingIntent2 = NotificationUtil.setPendingIntent(this@MainActivity, 18, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        NotificationUtil.startClock(this,alarmManager, ClockBean(12,0,pendingIntent1))
        NotificationUtil.startClock(this,alarmManager, ClockBean(12,0,pendingIntent2))
    }
}
