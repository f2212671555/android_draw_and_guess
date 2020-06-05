package com.ntouandroid.drawandguess

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ntouandroid.drawandguess.model.bean.RoomBean
import com.ntouandroid.drawandguess.model.bean.UserBean
import com.ntouandroid.drawandguess.listener.ArchLifecycleApp
import com.ntouandroid.drawandguess.model.repository.MyRepository
import com.ntouandroid.drawandguess.utils.GameTimer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MyTestActivity : AppCompatActivity() {

    lateinit var test_git:Button
    lateinit var tv_test: TextView
    lateinit var tvTestRoomList: TextView
    lateinit var btnTestPaint: Button
    lateinit var btnJoinRoom: Button
    lateinit var etJoinRoom: EditText

    lateinit var progressBar: ProgressBar
    var isAnimatingUpdatingDelayed: Boolean = false
    lateinit var mTimer: GameTimer
    var myRepository: MyRepository =
        MyRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_test)

        tv_test = findViewById(R.id.tv_test)

        btnTestPaint = findViewById(R.id.btn_testPaint)
        tvTestRoomList = findViewById(R.id.tv_testRoomList)
        btnJoinRoom = findViewById(R.id.btn_joinRoom)
        etJoinRoom = findViewById(R.id.et_joinRoom)
        progressBar = findViewById(R.id.pb_timer)

        tvTestRoomList.movementMethod = ScrollingMovementMethod()
        btnTestPaint.setOnClickListener { goTestPaintBtn() }

        btnJoinRoom.setOnClickListener { joinRoom() }
        //loadingRoomList()


        val timeSec = 5f //time設定幾秒

        mTimer = GameTimer(object : GameTimer.TimerBarController {
            override fun timerOnUpdate() {
//                println(mTimer.secondsCount * 100)
//                println("計時器進度條跳一次")
                update(-1, (mTimer.secondsCount * 100).toInt())
            }

            override fun timesUp() {
//                println("計時器進度條停止")
            }

        })
        mTimer.secondsCount = timeSec
        mTimer.maxTimeInSeconds = timeSec
        progressBar.max = (timeSec * 100).toInt()
        progressBar.progress = (timeSec * 100).toInt()
        mTimer.startTimer()
    }

    fun update(progressIncrement: Int, trueProgress: Int) {
        if (isAnimatingUpdatingDelayed) progressBar.incrementProgressBy(progressIncrement)
        else progressBar.progress = trueProgress
    }


    private fun joinRoom() {
        val intent = Intent(this, PaintActivity::class.java)
        var userId = ""
        val userName = "USERNAME"
        val roomId = etJoinRoom.text.toString()
        val userBean = UserBean(
            roomId,
            userId,
            userName
        )
        GlobalScope.launch(Dispatchers.IO) {
            val respUserJoinRoomBean = myRepository.joinRoom(userBean)
            println(respUserJoinRoomBean)
            userId = respUserJoinRoomBean.userId.toString()
            if (respUserJoinRoomBean.result!!) {
                ArchLifecycleApp.userStatus = ArchLifecycleApp.JOIN_ROOM
                intent.putExtra("roomid", roomId)
                intent.putExtra("userid", userId)
                intent.putExtra("userName", userName)
                startActivity(intent)
            }
        }
    }

    private fun loadingRoomList() {

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
                        userName
                    )
                )
            println(respUserJoinRoomBean)
            val userId = respUserJoinRoomBean.userId.toString()
            intent.putExtra("roomid", resultRoomBean.roomId.toString())
            intent.putExtra("userid", userId)
            intent.putExtra("userName", userName)
            startActivity(intent)
        }
    }


}