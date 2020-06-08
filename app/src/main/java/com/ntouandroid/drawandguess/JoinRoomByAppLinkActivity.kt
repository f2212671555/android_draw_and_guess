package com.ntouandroid.drawandguess

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ntouandroid.drawandguess.model.bean.UserBean
import com.ntouandroid.drawandguess.model.repository.MyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class JoinRoomByAppLinkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_room_by_app_link)

        val userNameET: EditText = findViewById(R.id.et_user_name_app_link)
        val roomNameTV: TextView = findViewById(R.id.tv_room_name_app_link)
        val joinBtn: Button = findViewById(R.id.bt_join)

        var roomId = ""
        var userName = ""

        // app link start
        val appLinkAction: String? = intent.action
        val appLinkData: Uri? = intent.data
        if (appLinkData != null) {
            //https://draw-and-guess-ntou.herokuapp.com/appLink?roomId=&roomName=
            roomId = appLinkData.getQueryParameter("roomId").toString()
            val roomName = appLinkData.getQueryParameter("roomName").toString()
            if (roomName.isNotEmpty() && roomId.isNotEmpty()) {
                roomNameTV.text = roomName
            }
        }
        // app link end
        joinBtn.setOnClickListener {
            userName = userNameET.text.toString()
            if (userName.isNotEmpty()) {
                if (roomId.isNotEmpty()) {
                    joinRoom(userName, roomId)
                }
            } else {
                Toast.makeText(this, "請輸入名稱", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun joinRoom(userName: String, roomId: String) {
        val myRepository = MyRepository()
        val dialog = ProgressDialog.show(this, "", "進入房間中...", true)
        val intent = Intent(this, PaintActivity::class.java)

        val userBean = UserBean(
            roomId,
            "",
            userName
        )
        GlobalScope.launch(Dispatchers.IO) {
            val resultUserJoinRoomBean = myRepository.joinRoom(userBean)
            dialog.dismiss()
            if (resultUserJoinRoomBean.result!!) {
                intent.putExtra("roomid", roomId)
                intent.putExtra("userid", resultUserJoinRoomBean.userId.toString())
                intent.putExtra("userName", userName)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
