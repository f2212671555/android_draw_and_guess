package com.ntouandroid.drawandguess

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ntouandroid.drawandguess.bean.RoomBean
import com.ntouandroid.drawandguess.repository.MyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyTestActivity : AppCompatActivity() {

    lateinit var tv_test: TextView
    lateinit var btnTestPaint: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_test)

        tv_test = findViewById(R.id.tv_test)

        btnTestPaint = findViewById(R.id.btn_testPaint)

        btnTestPaint.setOnClickListener { goTestPaintBtn() }
    }

    private fun goTestPaintBtn() {
        val intent = Intent(this, PaintActivity::class.java)
        val myRepository = MyRepository()
        val userName = "BOB"
        GlobalScope.launch(Dispatchers.IO) {
            val roomBean = RoomBean("", "a好好玩s")
            val resultRoomBean = myRepository.createRoom(roomBean)
            println(resultRoomBean)
            intent.putExtra("roomid", resultRoomBean.roomId.toString())
            intent.putExtra("userid", "")
            intent.putExtra("userName", userName)
            startActivity(intent)
        }
    }


}
