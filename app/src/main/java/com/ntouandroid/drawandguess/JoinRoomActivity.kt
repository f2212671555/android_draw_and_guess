package com.ntouandroid.drawandguess

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ntouandroid.drawandguess.model.bean.RoomBean
import com.ntouandroid.drawandguess.model.bean.UserBean
import com.ntouandroid.drawandguess.model.repository.MyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class JoinRoomActivity : AppCompatActivity() {


    lateinit var joinBtn: Button;
    lateinit var refreshBtn: ImageButton
    lateinit var roomListLV: ListView
    lateinit var listAdapter: ListAdapter;
    lateinit var tvTestRoomList: TextView

    private var myRepository: MyRepository = MyRepository();
    private var roomList: List<RoomBean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_room)

        joinBtn = findViewById(R.id.bt_join);
        roomListLV = findViewById(R.id.lv_room_list);
        refreshBtn = findViewById(R.id.btn_refresh)

        loadingRoomList();
        val userName = intent.getStringExtra("userName")

        joinBtn.setOnClickListener {
            val pos = roomListLV.checkedItemPosition
            if (roomListLV.isItemChecked(pos)) {
                val roomBean = roomList?.get(pos)
                println(userName)
                println(roomBean)
                joinRoom(userName!!, roomBean?.roomId!!)
            }
        }
        refreshBtn.setOnClickListener { loadingRoomList() }
    }

    private fun loadingRoomList() {

        GlobalScope.launch(Dispatchers.IO) {
            roomList = myRepository.getRoomList()
            if (roomList != null) {
                val roomNameList: MutableList<String> = ArrayList()
                roomList!!.forEach {
                    roomNameList.add(it.roomName!!)
                }

                runOnUiThread {
                    listAdapter = ArrayAdapter(
                        applicationContext,
                        android.R.layout.simple_list_item_1,
                        roomNameList
                    )
                    roomListLV.adapter = listAdapter
                }
            }
        }
    }

    private fun joinRoom(userName: String, roomId: String) {
        val intent = Intent(this, PaintActivity::class.java)

        val userBean = UserBean(
            roomId,
            "",
            userName
        )
        GlobalScope.launch(Dispatchers.IO) {
            val resultUserJoinRoomBean = myRepository.joinRoom(userBean)
            if (resultUserJoinRoomBean.result!!) {
                intent.putExtra("roomid", roomId)
                intent.putExtra("userid", resultUserJoinRoomBean.userId.toString())
                intent.putExtra("userName", userName)
                startActivity(intent)
            }
        }
    }

}
