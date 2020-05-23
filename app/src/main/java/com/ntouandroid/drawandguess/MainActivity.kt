package com.ntouandroid.drawandguess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {


    lateinit var Bt_Start: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Bt_Start = findViewById(R.id.btn_Start);

        Bt_Start.setOnClickListener{nextpagecheck()};
    }




    fun nextpagecheck(){
        var intent = Intent(this@MainActivity, StartActivity::class.java);
        startActivity(intent);
    }

}
