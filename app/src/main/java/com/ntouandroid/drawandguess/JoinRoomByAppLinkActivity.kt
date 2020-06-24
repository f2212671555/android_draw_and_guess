package com.ntouandroid.drawandguess

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ntouandroid.drawandguess.config.Config
import com.ntouandroid.drawandguess.model.bean.UserBean
import com.ntouandroid.drawandguess.model.repository.MyRepository
import com.ntouandroid.drawandguess.utils.internet.InternetJudge
import com.ntouandroid.drawandguess.utils.UIControl.UIHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class JoinRoomByAppLinkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_room_by_app_link)
        UIHandler.setStatusBarColor(this)

        val userNameET: EditText = findViewById(R.id.et_user_name_app_link)
        val roomNameTV: TextView = findViewById(R.id.tv_room_name_app_link)
        val joinBtn: Button = findViewById(R.id.btn_join_app_link)

        var roomId = ""
        var userName = ""

        // app link start
//        val appLinkAction: String? = intent.action
        val appLinkData: Uri? = intent.data
        if (appLinkData != null) {
            roomId = appLinkData.getQueryParameter(Config.ROOM_ID_KEY).toString()
            val roomName = appLinkData.getQueryParameter(Config.ROOM_NAME_KEY).toString()
            if (roomName.isNotEmpty() && roomId.isNotEmpty()) {
                roomNameTV.text = roomName
            }
        }
        // app link end
        joinBtn.setOnClickListener {
            userName = userNameET.text.toString()
            if (userName.isNotEmpty()) {
                if (roomId.isNotEmpty()) {
                    if (InternetJudge.isInternetAvailable(this)) {
                        joinRoom(userName, roomId)
                    } else {
                        showDialog("網路連接異常", "請檢查是否有連接網路！！")
                    }
                }
            } else {
                Toast.makeText(this, "請輸入名稱", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (currentFocus != null && currentFocus!!.windowToken != null) {
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
        }
        return super.onTouchEvent(event)
    }

    private fun joinRoom(userName: String, roomId: String) {
        val myRepository = MyRepository()
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
                intent.putExtra(MainActivity.ROOM_NAME, resultUserJoinRoomBean.roomName.toString())
                intent.putExtra(MainActivity.ROOM_ROLE, MainActivity.ROOM_ROLE_GENERAL_MEMBER)
                startActivity(intent)
            } else {
                runOnUiThread {
                    Toast.makeText(this@JoinRoomByAppLinkActivity, "不存在此房間!!", Toast.LENGTH_SHORT)
                        .show()
                    onBackPressed()
                }
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showDialog(title: String, message: String) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("確認") { dialog, _ ->
            dialog.dismiss()
        }

        // create dialog and show it
        val dialog = builder.create()
        dialog.show()
    }
}
