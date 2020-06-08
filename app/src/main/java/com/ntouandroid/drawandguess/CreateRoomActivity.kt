package com.ntouandroid.drawandguess

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ntouandroid.drawandguess.filter.NameInputFilter
import com.ntouandroid.drawandguess.model.bean.RoomBean
import com.ntouandroid.drawandguess.model.bean.UserBean
import com.ntouandroid.drawandguess.model.repository.MyRepository
import com.ntouandroid.drawandguess.utils.UIHandler
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
        UIHandler.setStatusBarColor(this)

        Et_RoomName = findViewById(R.id.Et_RoomName);
        bt_CreateRoom = findViewById(R.id.bt_CreateRoom);
        Tv_userName = findViewById(R.id.Tv_userName);

        userName = intent.getStringExtra("userName");

        Tv_userName.text = userName;

        Et_RoomName.filters = arrayOf(NameInputFilter())

        bt_CreateRoom.setOnClickListener { CreatPage() };


    }

    override fun onUserInteraction() {
        if (currentFocus != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    private fun CreatPage() {
        val intent = Intent(this, PaintActivity::class.java);
        roomName = Et_RoomName.text.toString().trim()
        if (roomName.isEmpty()) {
            Toast.makeText(this, "請入房名", Toast.LENGTH_LONG).show()
            return
        }
        val dialog = ProgressDialog.show(this@CreateRoomActivity, "", "創建房間中...", true)
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
            dialog.dismiss()
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