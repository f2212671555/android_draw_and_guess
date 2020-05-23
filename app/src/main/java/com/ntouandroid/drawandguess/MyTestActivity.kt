package com.ntouandroid.drawandguess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.ntouandroid.drawandguess.colorPicker.PaintActivity

class MyTestActivity: AppCompatActivity() {

    lateinit var tv_test:TextView
    lateinit var btnTestPaint:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_test)

        tv_test = findViewById(R.id.tv_test)

        btnTestPaint = findViewById(R.id.btn_testPaint)

        btnTestPaint.setOnClickListener { goTestPaintBtn() }
    }

    private fun goTestPaintBtn() {
        val intent = Intent(this,PaintActivity::class.java)
        intent.putExtra("roomid","123room")
        intent.putExtra("userid","123user")
        startActivity(intent)
    }


}
