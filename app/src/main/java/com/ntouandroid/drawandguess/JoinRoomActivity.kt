package com.ntouandroid.drawandguess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class JoinRoomActivity : AppCompatActivity() {


    lateinit var Bt_join: Button;
    lateinit var Bt_refresh: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_room)

        Bt_join = findViewById(R.id.bt_join);

        Bt_join.setOnClickListener{nextpagecheck()};
    }




    fun nextpagecheck(){
        var intent = Intent(this@JoinRoomActivity, PaintActivity::class.java);
        startActivity(intent);
    }

}
