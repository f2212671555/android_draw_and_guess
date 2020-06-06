package com.ntouandroid.drawandguess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class StartActivity : AppCompatActivity() {


    lateinit var Bt_Create: Button;
    lateinit var Bt_Join: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        Bt_Create = findViewById(R.id.button_create)
        Bt_Join = findViewById(R.id.button_join);

        Bt_Create.setOnClickListener{CreatPage()};
        Bt_Join.setOnClickListener{JoinPage()};
    }




    fun CreatPage(){
        var intent = Intent(this@StartActivity, CreateRoomActivity::class.java);
        startActivity(intent);
    }

    fun JoinPage(){
        var intent = Intent(this@StartActivity, JoinRoomActivity::class.java);
        startActivity(intent);
    }

}
