package com.ntouandroid.drawandguess.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.ntouandroid.drawandguess.R
import com.ntouandroid.drawandguess.model.repository.MyRepository
import com.ntouandroid.drawandguess.utils.UIControl.UIHandler

class StartActivity : AppCompatActivity() {


    lateinit var Bt_Create: Button;
    lateinit var Bt_Join: Button;
    var myRepository: MyRepository = MyRepository();
    private var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        UIHandler.setStatusBarColor(this)

        userName = intent.getStringExtra(MainActivity.USER_NAME)

        Bt_Create = findViewById(R.id.button_create)
        Bt_Join = findViewById(R.id.button_join);

        Bt_Create.setOnClickListener { CreatPage() };
        Bt_Join.setOnClickListener { JoinPage() };
    }


    fun CreatPage() {
        val intent = Intent(this@StartActivity, CreateRoomActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.putExtra(MainActivity.USER_NAME, userName)
        startActivity(intent)
    }

    fun JoinPage() {
        val intent = Intent(this@StartActivity, JoinRoomActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        intent.putExtra(MainActivity.USER_NAME, userName)
        startActivity(intent)
    }


}
