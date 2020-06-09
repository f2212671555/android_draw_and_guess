package com.ntouandroid.drawandguess

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
import com.ntouandroid.drawandguess.utils.UIHandler


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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        UIHandler.setStatusBarColor(this)
        Bt_Start = findViewById(R.id.button_start)
        Et_Name = findViewById(R.id.Et_Name)

        Et_Name.filters = arrayOf(NameInputFilter())
        Bt_Start.setOnClickListener { nextpagecheck() }

        app_context = applicationContext

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
        showDialog("確定要離開APP嗎？", "")
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

    private fun showDialog(title: String, message: String) {

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

}
