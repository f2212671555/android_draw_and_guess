package com.ntouandroid.drawandguess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.ntouandroid.drawandguess.model.bean.RoomBean
import com.ntouandroid.drawandguess.model.bean.UserBean
import com.ntouandroid.drawandguess.model.repository.MyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class StartActivity : AppCompatActivity() {


    lateinit var Bt_Create: Button;
    lateinit var Bt_Join: Button;
    var myRepository: MyRepository = MyRepository();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        userName = intent.getStringExtra("userName")

        Bt_Create = findViewById(R.id.button_create)
        Bt_Join = findViewById(R.id.button_join);

        Bt_Create.setOnClickListener{CreatPage()};
        Bt_Join.setOnClickListener{JoinPage()};
    }




    fun CreatPage(){
        val intent = Intent(this@StartActivity, CreateRoomActivity::class.java);
        intent.putExtra("userName",userName)
        startActivity(intent);
    }

    fun JoinPage(){
        val intent = Intent(this@StartActivity, JoinRoomActivity::class.java);
        startActivity(intent);
    }

    companion object {
        var roomid: String = ""
        var userid: String = ""
        var userName: String = ""
    }

}
