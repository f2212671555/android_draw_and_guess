package com.ntouandroid.drawandguess

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drawtest.ColorPaint
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.ntouandroid.drawandguess.adapter.UserListAdapter
import com.ntouandroid.drawandguess.bean.MessageBean
import com.ntouandroid.drawandguess.bean.UserBean
import com.ntouandroid.drawandguess.colorPicker.PaintBoard
import com.ntouandroid.drawandguess.repository.MyRepository
import com.ntouandroid.drawandguess.service.MyWebSocket
import com.ntouandroid.drawandguess.utils.GameTimer
import com.ntouandroid.drawandguess.webSocket.RoomWebSocketListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


class PaintActivity : AppCompatActivity() {

    lateinit var eraser: Button
    lateinit var size: Button
    var sizeNumGet: Float = 10.0f
    lateinit var clean: Button
    lateinit var btnColorSelected: Button
    lateinit var backgroundClean: Button

    lateinit var colorR: SeekBar
    lateinit var colorG: SeekBar
    lateinit var colorB: SeekBar
    lateinit var strColor: EditText

    lateinit var etMessage: EditText
    lateinit var etChat: EditText
    lateinit var tvMessage: TextView
    lateinit var btnSendMessage: Button
    lateinit var btnChat: Button
    private var myRoomWebSocketListener: RoomWebSocketListener? = null
    lateinit var paintB: PaintBoard
    var eraserMode = false
    lateinit var mTimer: GameTimer

    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paint)

        roomid = intent.getStringExtra("roomid")
        userid = intent.getStringExtra("userid")
        userName = intent.getStringExtra("userName")

        initDrawers()
        initChatRoom()


        paintB = findViewById(R.id.layout_paint_board)
        paintB.post(Runnable {
            paintB.init(paintB.width, paintB.height).initDrawRoom(roomid, userid)
        })

        lateinit var btnColorPreview: Button
        eraser = findViewById(R.id.eraser)
        size = findViewById(R.id.size)
        etMessage = findViewById(R.id.message_et)
//        val rightNavView: NavigationView = findViewById(R.id.nav_view_right)
//        val rightNavViewHeader = rightNavView.getHeaderView(0)
//        val displayMetrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(displayMetrics)
//        val height = displayMetrics.heightPixels
//        rightNavViewHeader.layoutParams.height = height
        etChat = findViewById(R.id.et_chat)
        btnChat = findViewById(R.id.btn_chat)

        tvMessage = findViewById(R.id.tv_recieve)
        btnSendMessage = findViewById(R.id.send_message_btn)

        clean = findViewById(R.id.backgroundClean)
        btnColorSelected = findViewById(R.id.btnColorSelected)

        initColorPickerDialog()
        eraser.setOnClickListener { eraserFun() }
        initSizeChangeDialog()
        clean.setOnClickListener { backgroundClean() }


        btnSendMessage.setOnClickListener { sendMessage("answer") }
        btnChat.setOnClickListener { sendMessage("chat") }

        val timeSec = 5f


        mTimer = GameTimer(object : GameTimer.TimerBarController {
            override fun timerOnUpdate() {
//                println("計時器進度條跳一次")
            }

            override fun timesUp() {
//                println("計時器進度條停止")
            }

        })
        mTimer.secondsCount = timeSec
        mTimer.maxTimeInSeconds = timeSec

        mTimer.startTimer()


    }

    private fun sendMessage(type: String) {
        if (myRoomWebSocketListener != null) {
            when (type) {
                "chat" -> {
                    val messageBean =
                        MessageBean(type, userid, userName, roomid, etChat.text.toString(), false)
                    myRoomWebSocketListener!!.sendMessage(messageBean)
                }
                "answer" -> {
                    val messageBean =
                        MessageBean(
                            type,
                            userid,
                            userName,
                            roomid,
                            etMessage.text.toString(),
                            false
                        )
                    myRoomWebSocketListener!!.sendMessage(messageBean)
                }
                else -> {
                    println("sendMessage missing type")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initChatRoom() {

        myRoomWebSocketListener = RoomWebSocketListener(UserBean(roomid, userid, userName))
        val outerClass = WeakReference(this)
        val myHandler = MyHandler(outerClass)

        MyWebSocket.createRoomWebSocket(myRoomWebSocketListener!!, roomid, userid)

        myRoomWebSocketListener?.setHandler(myHandler)
    }

    private fun initColorPickerDialog() {

        val colorpickerDialog = Dialog(this)
        colorpickerDialog.setContentView(R.layout.colorpicker)
//        val colorSelector: RelativeLayout = colorpickerDialog.findViewById(R.id.colorSelector)
        val colorCancelBtn: Button = colorpickerDialog.findViewById(R.id.colorCancelBtn)
        val colorOkBtn: Button = colorpickerDialog.findViewById(R.id.colorOkBtn)
        val btnColorPreview: Button = colorpickerDialog.findViewById(R.id.btnColorPreview)
        colorR = colorpickerDialog.findViewById(R.id.colorR)
        colorG = colorpickerDialog.findViewById(R.id.colorG)
        colorB = colorpickerDialog.findViewById(R.id.colorB)
        strColor = colorpickerDialog.findViewById(R.id.strColor)

        strColor.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.length == 6) {
                    colorR.progress = Integer.parseInt(s.substring(0..1), 16)
                    colorG.progress = Integer.parseInt(s.substring(2..3), 16)
                    colorB.progress = Integer.parseInt(s.substring(4..5), 16)
                } else if (s.length == 8) {
                    colorR.progress = Integer.parseInt(s.substring(2..3), 16)
                    colorG.progress = Integer.parseInt(s.substring(4..5), 16)
                    colorB.progress = Integer.parseInt(s.substring(6..7), 16)
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })

        colorR.max = 255
        colorR.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(
                seekBar: SeekBar, progress: Int,
                fromUser: Boolean
            ) {
                val colorStr = getColorString()
                strColor.setText(colorStr.replace("#", "").toUpperCase())
                btnColorPreview.setBackgroundColor(Color.parseColor(colorStr))
            }
        })

        colorG.max = 255
        colorG.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(
                seekBar: SeekBar, progress: Int,
                fromUser: Boolean
            ) {
                val colorStr = getColorString()
                strColor.setText(colorStr.replace("#", "").toUpperCase())
                btnColorPreview.setBackgroundColor(Color.parseColor(colorStr))
            }
        })

        colorB.max = 255
        colorB.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(
                seekBar: SeekBar, progress: Int,
                fromUser: Boolean
            ) {
                val colorStr = getColorString()
                strColor.setText(colorStr.replace("#", "").toUpperCase())
                btnColorPreview.setBackgroundColor(Color.parseColor(colorStr))
            }
        })

        colorCancelBtn.setOnClickListener {
            colorpickerDialog.dismiss()
        }

        colorOkBtn.setOnClickListener {
            colorpickerDialog.dismiss()
            val color: String = getColorString()
            setColor()
            btnColorSelected.setBackgroundColor(Color.parseColor(color))
        }

        btnColorSelected.setOnClickListener {
            colorpickerDialog.show()
        }
    }

    private fun initSizeChangeDialog() {
        val sizechangeDialog = Dialog(this)
        sizechangeDialog.setContentView(R.layout.sizechange)
        val sizeNumPrint: EditText = sizechangeDialog.findViewById(R.id.sizeNumPrint)
        val sizeCancelBtn: Button = sizechangeDialog.findViewById(R.id.sizeCancelBtn)
        val sizeOkBtn: Button = sizechangeDialog.findViewById(R.id.sizeOkBtn)
//        val sizeSelector: LinearLayout = sizechangeDialog.findViewById(R.id.sizeSelector)
        val sizeNum: SeekBar = sizechangeDialog.findViewById(R.id.sizeNum)

        size.setOnClickListener {
            sizechangeDialog.show()
            sizeChange()
        }

        sizeNumPrint.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                println(s.toString())
                sizeNum.progress = Integer.parseInt(s.toString(), 10)
                // --- fix----
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })

        sizeNum.max = 100
        sizeNum.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(
                seekBar: SeekBar, progress: Int,
                fromUser: Boolean
            ) {
                println(sizeNum.progress)
                val sizeGet = Integer.parseInt((sizeNum.progress).toString())
                sizeNumPrint.setText((sizeNum.progress).toString())
                sizeNumGet = sizeGet.toFloat()
            }
        })

        sizeCancelBtn.setOnClickListener {
            sizechangeDialog.dismiss()
        }

        sizeOkBtn.setOnClickListener {
            sizechangeDialog.dismiss()
            sizeChange()
        }
    }

    companion object {
        var colorpaint = ColorPaint(0, 0, 0, 30.0f)
        var roomid: String = ""
        var userid: String = ""
        var nextid: String = ""
        var userName: String = ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun eraserFun() {

        eraserMode = !eraserMode
        paintB.erase(eraserMode)
    }

    fun sizeChange() {
        colorpaint = ColorPaint(
            ((255 * colorR.progress) / colorR.max),
            ((255 * colorG.progress) / colorG.max),
            ((255 * colorB.progress) / colorB.max),
            sizeNumGet
        )
    }

    fun setColor() {
        colorpaint = ColorPaint(
            ((255 * colorR.progress) / colorR.max),
            ((255 * colorG.progress) / colorG.max),
            ((255 * colorB.progress) / colorB.max),
            sizeNumGet
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun backgroundClean() {
        paintB.cleanBackground()
    }

    fun getColorString(): String {
        var r = Integer.toHexString(((255 * colorR.progress) / colorR.max))
        if (r.length == 1) r = "0" + r
        var g = Integer.toHexString(((255 * colorG.progress) / colorG.max))
        if (g.length == 1) g = "0" + g
        var b = Integer.toHexString(((255 * colorB.progress) / colorB.max))
        if (b.length == 1) b = "0" + b
        return "#" + r + g + b
    }

    override fun onResume() {
        super.onResume()
        println("onResume")
//        ArchLifecycleApp.userStatus = ArchLifecycleApp.JOIN_ROOM
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        println("onPause")
        // 倒數 後 退出房間
        // close webSocket 觸發 退出房間
        paintB.closeWebSocket()
        myRoomWebSocketListener?.close()
    }

    fun addChatCardView(messageBean: MessageBean) {
        val llChat: LinearLayout = findViewById(R.id.ll_chat)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        var text = ""
        if (messageBean.userId == userid) { // 自己
            params.apply {
                gravity = Gravity.END
                topMargin = 10.dp
                leftMargin = 20.dp
            }
            text = "你 : ${messageBean.message}"
        } else {
            params.apply {
                gravity = Gravity.START
                topMargin = 10.dp
                rightMargin = 20.dp
            }
            text = "${messageBean.userName} : ${messageBean.message}"
        }
        val cardView = CardView(this)
        cardView.radius = 10.dp.toFloat()
        cardView.layoutParams = params
        val t = TextView(this)
        t.text = text
        cardView.addView(t)
        llChat.addView(cardView)
    }

    class MyHandler(private val outerClass: WeakReference<PaintActivity>) : Handler() {
        override fun handleMessage(msg: Message) {
            val messageBean = Gson().fromJson(msg?.obj.toString(), MessageBean::class.java)
            when (messageBean.type) {
                "chat" -> {
                    //某某人聊天
                    outerClass.get()?.addChatCardView(messageBean)
                }
                "answer" -> {
                    //某某人猜答案
                    outerClass.get()?.tvMessage?.append(messageBean.message)
                }
                "join" -> {
                    //某某人加入房間
                    outerClass.get()?.modifyUserList(messageBean)
                }
                "quit" -> {
                    //某某人離開房間
                    outerClass.get()?.modifyUserList(messageBean)
                }
                else -> {
                    println("handleMessage missing type!!")
                }
            }
        }
    }

    fun gamestart() {
        if (userid == nextid) {
            //lock chat
            etMessage.setEnabled(false)
        } else {
            etMessage.setEnabled(true)
        }

        //mTimer.startTimer()
    }

    private fun initDrawers() {

        val drawLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        drawLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerClosed(drawerView: View) {
                drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            override fun onDrawerOpened(drawerView: View) {
                drawLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }

        })

        // left drawer
        val btnLeftNav: Button = findViewById(R.id.btnLeftNav)
        val leftDrawerView: NavigationView = findViewById(R.id.nav_view_left)
        btnLeftNav.setOnClickListener {
            drawLayout.openDrawer(leftDrawerView)
        }

        // right drawer

        val btnRightNav: Button = findViewById(R.id.btnRightNav)
        val rightDrawerView: NavigationView = findViewById(R.id.nav_view_right)
        btnRightNav.setOnClickListener {
            drawLayout.openDrawer(rightDrawerView)
        }

        initUserList()
    }

    //    private var usersList: MutableList<UserBean> = ArrayList()
    private lateinit var userListAdapter: UserListAdapter
    private fun initUserList() {
        var usersList: MutableList<UserBean> = ArrayList()
        // must set up in main thread --start
        val userListView: RecyclerView = findViewById(R.id.view_user_list)
        userListAdapter = UserListAdapter(this, usersList)
        userListView.adapter = userListAdapter
        userListView.layoutManager = LinearLayoutManager(this)
        // must set up in main thread -- end

        val myRepository = MyRepository()
        GlobalScope.launch(Dispatchers.IO) {
            println(roomid)
            val roomBean = myRepository.getRoomUsers(roomid)
            usersList = roomBean.usersList!!
            runOnUiThread {
                userListAdapter.updateAll(usersList)
            }
        }
    }

    private fun modifyUserList(messageBean: MessageBean) {

        if (messageBean.type == "join") {
            if (messageBean.userId != userid) {
                userListAdapter.add(messageBean)
            }
        } else if (messageBean.type == "quit") {
            userListAdapter.remove(messageBean.userId!!)
        }
    }
}
