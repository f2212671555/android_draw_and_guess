package com.ntouandroid.drawandguess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.ntouandroid.drawandguess.colorPicker.PaintActivity


class JoinRoomActivity : AppCompatActivity() {


    lateinit var Bt_SET: Button;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_room)

        Bt_SET = findViewById(R.id.btn_SET);

        Bt_SET.setOnClickListener{nextpagecheck()};
    }




    fun nextpagecheck(){
        var intent = Intent(this@JoinRoomActivity, PaintActivity::class.java);
        startActivity(intent);
    }

}
