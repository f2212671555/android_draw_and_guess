package com.ntouandroid.drawandguess

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ntouandroid.drawandguess.model.bean.RoomBean
import com.ntouandroid.drawandguess.model.bean.UserBean
import com.ntouandroid.drawandguess.model.repository.MyRepository
import com.ntouandroid.drawandguess.utils.UIHandler
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
        UIHandler.setStatusBarColor(this)

        joinBtn = findViewById(R.id.bt_join);
        roomListLV = findViewById(R.id.lv_room_list);
        refreshBtn = findViewById(R.id.btn_refresh)

        loadingRoomList();
        val userName = intent.getStringExtra(MainActivity.USER_NAME)

        joinBtn.setOnClickListener {
            val pos = roomListLV.checkedItemPosition
            if (roomListLV.isItemChecked(pos)) {
                val roomBean = roomList?.get(pos)
                joinRoom(userName!!, roomBean?.roomId!!)
            } else {
                Toast.makeText(this, "請選擇房間", Toast.LENGTH_LONG).show()
            }
        }
        refreshBtn.setOnClickListener { loadingRoomList() }
    }

    private fun loadingRoomList() {
        val dialog = ProgressDialog.show(this, "", "載入房間列表中...", true)
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
            dialog.dismiss()
        }

    }

    private fun joinRoom(userName: String, roomId: String) {
        val dialog = ProgressDialog.show(this, "", "進入房間中...", true)
        val intent = Intent(this, PaintActivity::class.java)

        val userBean = UserBean(
            roomId,
            "",
            userName,
            MainActivity.ROOM_ROLE_GENERAL_MEMBER
        )
        GlobalScope.launch(Dispatchers.IO) {
            val resultUserJoinRoomBean = myRepository.joinRoom(userBean)
            dialog.dismiss()
            if (resultUserJoinRoomBean.result!!) {
                intent.putExtra(MainActivity.ROOM_ID, roomId)
                intent.putExtra(MainActivity.USER_ID, resultUserJoinRoomBean.userId.toString())
                intent.putExtra(MainActivity.USER_NAME, userName)
                startActivity(intent)
            }
        }
    }

}
