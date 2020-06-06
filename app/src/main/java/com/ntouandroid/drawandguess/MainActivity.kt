package com.ntouandroid.drawandguess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    lateinit var bt_Test:Button
    lateinit var Bt_Start: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Bt_Start = findViewById(R.id.button_start);

        Bt_Start.setOnClickListener{nextpagecheck()};

    }


    fun nextpagecheck(){
        var intent = Intent(this@MainActivity, StartActivity::class.java);
        startActivity(intent);
    }

    fun btn_test_click(){
        var intent = Intent(this@MainActivity,MyTestActivity::class.java)
        startActivity(intent)
    }

}
