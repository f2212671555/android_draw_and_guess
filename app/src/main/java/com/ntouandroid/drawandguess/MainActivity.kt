package com.ntouandroid.drawandguess

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {

    lateinit var bt_Test:Button
    lateinit var Bt_Start: Button;
    lateinit var Et_Name: EditText;
    lateinit var app_context: Context;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Bt_Start = findViewById(R.id.button_start);
        Et_Name = findViewById(R.id.Et_Name)
        Bt_Start.setOnClickListener{nextpagecheck()};
        app_context = applicationContext;
    }


    fun nextpagecheck(){
        var intent = Intent(this@MainActivity, StartActivity::class.java);
        intent.putExtra("userName", Et_Name.text.toString())
        startActivity(intent);
    }

    fun btn_test_click(){
        var intent = Intent(this@MainActivity,MyTestActivity::class.java)
        startActivity(intent)
    }

}
