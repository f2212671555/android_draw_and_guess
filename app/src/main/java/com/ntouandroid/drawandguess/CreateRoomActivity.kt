package com.ntouandroid.drawandguess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.ntouandroid.drawandguess.model.bean.RoomBean
import com.ntouandroid.drawandguess.model.bean.UserBean
import com.ntouandroid.drawandguess.model.repository.MyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CreateRoomActivity : AppCompatActivity() {

    lateinit var Et_RoomName : EditText;
    lateinit var bt_CreateRoom: Button;
    lateinit var Tv_userName : TextView;



    var myRepository: MyRepository = MyRepository();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_room)

        Et_RoomName = findViewById(R.id.Et_RoomName);
        bt_CreateRoom = findViewById(R.id.bt_CreateRoom);
        Tv_userName = findViewById(R.id.Tv_userName);

        roomName = Et_RoomName.text.toString();
        userName =  intent.getStringExtra("userName");

        Tv_userName.text =  userName;



        bt_CreateRoom.setOnClickListener{CreatPage()};

    }

    fun CreatPage(){
        var intent = Intent(this@CreateRoomActivity, PaintActivity::class.java);
        goTestPaintBtn();
    }


    private fun goTestPaintBtn() {
        val intent = Intent(this, PaintActivity::class.java)
        GlobalScope.launch(Dispatchers.IO) {
            val roomBean = RoomBean(
                "",
                "a好好玩s",
                null,
                null
            )
            val resultRoomBean = myRepository.createRoom(roomBean)
            println(resultRoomBean)
            val respUserJoinRoomBean =
                myRepository.joinRoom(
                    UserBean(
                        resultRoomBean.roomId.toString(),
                        "",
                        StartActivity.userName
                    )
                )
            println(respUserJoinRoomBean)
            val userId = respUserJoinRoomBean.userId.toString()
            intent.putExtra("roomid", resultRoomBean.roomId.toString())
            intent.putExtra("userid", userId)
            intent.putExtra("roomName", roomName)
            intent.putExtra("userName", userName)
            startActivity(intent)
        }
    }



    companion object {
        var roomid: String = ""
        var userid: String = ""
        var userName: String = ""
        var roomName: String = ""
    }

}

//test