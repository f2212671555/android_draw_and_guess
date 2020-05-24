package com.ntouandroid.drawandguess

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ntouandroid.drawandguess.bean.RoomBean
import com.ntouandroid.drawandguess.bean.UserBean
import com.ntouandroid.drawandguess.repository.MyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyTestActivity : AppCompatActivity() {

    lateinit var tv_test: TextView
    lateinit var tvTestRoomList: TextView
    lateinit var btnTestPaint: Button
    lateinit var btnJoinRoom: Button
    lateinit var etJoinRoom: EditText
    var myRepository: MyRepository = MyRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_test)

        tv_test = findViewById(R.id.tv_test)

        btnTestPaint = findViewById(R.id.btn_testPaint)
        tvTestRoomList = findViewById(R.id.tv_testRoomList)
        btnJoinRoom = findViewById(R.id.btn_joinRoom)
        etJoinRoom = findViewById(R.id.et_joinRoom)


        tvTestRoomList.movementMethod = ScrollingMovementMethod()
        btnTestPaint.setOnClickListener { goTestPaintBtn() }
        btnJoinRoom.setOnClickListener { joinRoom() }
//        loadingRoomList()

    }

    private fun joinRoom() {
        val intent = Intent(this, PaintActivity::class.java)
        var userId = ""
        val userName = "USERNAME"
        val roomId = etJoinRoom.text.toString()
        val userBean = UserBean(roomId, userId, userName)
        GlobalScope.launch(Dispatchers.IO) {
            val respUserJoinRoomBean = myRepository.joinRoom(userBean)
            println(respUserJoinRoomBean)
            userId = respUserJoinRoomBean.userId.toString()

            intent.putExtra("roomid", roomId)
            intent.putExtra("userid", userId)
            intent.putExtra("userName", userName)
            startActivity(intent)
        }
    }

    private fun loadingRoomList() {

        val userName = "BOB"
        GlobalScope.launch(Dispatchers.IO) {
            val roomList = myRepository.getRoomList()
            runOnUiThread {
                tvTestRoomList.text = ""
                roomList.forEach { room ->
                    val text = "$room\n----------------------\n"
                    tvTestRoomList.append(text)
                }
            }
        }
    }

    private fun goTestPaintBtn() {
        val intent = Intent(this, PaintActivity::class.java)
        val userName = "BOB"
        GlobalScope.launch(Dispatchers.IO) {
            val roomBean = RoomBean("", "a好好玩s")
            val resultRoomBean = myRepository.createRoom(roomBean)
            println(resultRoomBean)
            val respUserJoinRoomBean = myRepository.joinRoom(UserBean(resultRoomBean.roomId.toString(),"",userName))
            println(respUserJoinRoomBean)
            val userId = respUserJoinRoomBean.userId.toString()
            intent.putExtra("roomid", resultRoomBean.roomId.toString())
            intent.putExtra("userid", userId)
            intent.putExtra("userName", userName)
            startActivity(intent)
        }
    }


}
