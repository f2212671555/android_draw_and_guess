package com.ntouandroid.drawandguess

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ntouandroid.drawandguess.model.repository.MyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class JoinRoomActivity : AppCompatActivity() {


    lateinit var Bt_join: Button;
    lateinit var Bt_refresh: Button;
    lateinit var  Lv_RoomList: ListView;
    lateinit var listAdapter: ListAdapter;
    lateinit var tvTestRoomList: TextView

    var myRepository: MyRepository = MyRepository();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_room)

        Bt_join = findViewById(R.id.bt_join);
        Lv_RoomList = findViewById(R.id.Lv_Roomlist);

        loadingRoomList();

        Bt_join.setOnClickListener{nextpagecheck()};
    }



    private fun loadingRoomList() {

        GlobalScope.launch(Dispatchers.IO) {
            val roomList = myRepository.getRoomList()
            runOnUiThread {
                listAdapter = ArrayAdapter(
                    applicationContext,
                    android.R.layout.simple_list_item_1,
                    roomList
                    )
                Lv_RoomList.adapter = listAdapter
            }
        }
    }

    fun nextpagecheck(){
        var intent = Intent(this@JoinRoomActivity, PaintActivity::class.java);
        startActivity(intent);
    }



}
