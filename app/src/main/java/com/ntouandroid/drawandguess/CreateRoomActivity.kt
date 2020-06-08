package com.ntouandroid.drawandguess

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ntouandroid.drawandguess.filter.NameInputFilter
import com.ntouandroid.drawandguess.model.bean.RoomBean
import com.ntouandroid.drawandguess.model.bean.UserBean
import com.ntouandroid.drawandguess.model.repository.MyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CreateRoomActivity : AppCompatActivity() {

    lateinit var Et_RoomName: EditText;
    lateinit var bt_CreateRoom: Button;
    lateinit var Tv_userName: TextView;


    var myRepository: MyRepository = MyRepository();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_room)

        Et_RoomName = findViewById(R.id.Et_RoomName);
        bt_CreateRoom = findViewById(R.id.bt_CreateRoom);
        Tv_userName = findViewById(R.id.Tv_userName);

        userName = intent.getStringExtra("userName");

        Tv_userName.text = userName;

        Et_RoomName.filters = arrayOf(NameInputFilter())

        bt_CreateRoom.setOnClickListener { CreatPage() };

    }


    private fun CreatPage() {
        val intent = Intent(this, PaintActivity::class.java);
        roomName = Et_RoomName.text.toString().trim()
        if (roomName.isEmpty()) {
            Toast.makeText(this, "請入房名", Toast.LENGTH_LONG).show()
            return
        }
        GlobalScope.launch(Dispatchers.IO) {
            val roomBean = RoomBean(
                "",
                roomName,
                null,
                null
            )
            val resultRoomBean = myRepository.createRoom(roomBean)
            println(resultRoomBean)
            val resultUserJoinRoomBean =
                myRepository.joinRoom(
                    UserBean(
                        resultRoomBean.roomId.toString(),
                        "",
                        StartActivity.userName
                    )
                )
            println(resultUserJoinRoomBean)
            intent.putExtra("roomid", resultRoomBean.roomId.toString())
            intent.putExtra("userid", resultUserJoinRoomBean.userId.toString())
            intent.putExtra("roomName", roomName)
            intent.putExtra("userName", userName)
            startActivity(intent)
        }
    }

    // will be deleted
    companion object {
        var roomid: String = ""
        var userid: String = ""
        var userName: String = ""
        var roomName: String = ""
    }
    // will be deleted

}

//test