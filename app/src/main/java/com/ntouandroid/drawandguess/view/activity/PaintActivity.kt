package com.ntouandroid.drawandguess.view.activity

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.drawtest.ColorPaintBean
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.ntouandroid.drawandguess.R
import com.ntouandroid.drawandguess.config.Config
import com.ntouandroid.drawandguess.model.bean.MessageBean
import com.ntouandroid.drawandguess.model.bean.UserBean
import com.ntouandroid.drawandguess.model.repository.MyRepository
import com.ntouandroid.drawandguess.model.service.MyWebSocket
import com.ntouandroid.drawandguess.model.webSocket.RoomWebSocketListener
import com.ntouandroid.drawandguess.utils.UIControl.UIHandler
import com.ntouandroid.drawandguess.utils.timer.GameTimer
import com.ntouandroid.drawandguess.view.adapter.UserListAdapter
import com.ntouandroid.drawandguess.view.colorPicker.PaintBoard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


class PaintActivity : AppCompatActivity() {

    lateinit var eraser: ImageButton
    lateinit var size: ImageButton
    var sizeNumGet: Float = 10.0f
    lateinit var clean: ImageButton
    lateinit var btnColorSelected: ImageButton
    lateinit var backgroundClean: ImageButton

    lateinit var colorR: SeekBar
    lateinit var colorG: SeekBar
    lateinit var colorB: SeekBar
    lateinit var strColor: EditText

    lateinit var etMessage: EditText
    lateinit var etChat: EditText
    lateinit var tvMessage: TextView
    lateinit var btnSendMessage: ImageButton
    lateinit var btnChat: ImageButton
    private var myRoomWebSocketListener: RoomWebSocketListener? = null
    lateinit var paintB: PaintBoard
    var eraserMode = false
    private var mTimer: GameTimer? = null

    private var roomId: String = ""
    private var roomName: String = ""
    private var userId: String = ""
    private var userName: String = ""
    private var userRole: String = ""
    private var answer: String = ""

    private lateinit var userListAdapter: UserListAdapter
    private lateinit var progressBar: ProgressBar
    private var isAnimatingUpdatingDelayed: Boolean = false

    private lateinit var llDrawTopic: LinearLayout
    private lateinit var llDrawTopicAnswer: LinearLayout
    private var userMode =
        INIT_MODE

    companion object {
        const val INIT_MODE = "INIT_MODE"
        const val DRAW_MODE = "DRAW_MODE"
        const val ANSWER_MODE = "ANSWER_MODE"
        var colorpaint = ColorPaintBean(0, 0, 0, 30.0f)
    }

    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paint)
        UIHandler.setStatusBarColor(this)

        roomId = intent.getStringExtra(MainActivity.ROOM_ID)
        roomName = intent.getStringExtra(MainActivity.ROOM_NAME)
        userRole = intent.getStringExtra(MainActivity.ROOM_ROLE)
        userId = intent.getStringExtra(MainActivity.USER_ID)
        userName = intent.getStringExtra(MainActivity.USER_NAME)

        paintB = findViewById(R.id.layout_paint_board)
        progressBar = findViewById(R.id.pb_timer)
        userMode =
            INIT_MODE
        etMessage = findViewById(R.id.message_et)

        initUIControl()
        initTopicSection()
        initDrawers()
        initChatRoom()

        paintB.post(Runnable {
            try {
                paintB.init(paintB.width, paintB.height).initDrawRoom(roomId, userId)

            } catch (e: IllegalArgumentException) {
                Log.d("paintB init failed!!", "IllegalArgumentException")
                Toast.makeText(this, "paintB init failed!!", Toast.LENGTH_SHORT).show()
                finish()
            }
        })


        lateinit var btnColorPreview: Button
        eraser = findViewById(R.id.eraser)
        size = findViewById(R.id.size)

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

    }

    private fun initTopicSection() {
        llDrawTopic = findViewById(R.id.ll_draw_topic)
        llDrawTopicAnswer = findViewById(R.id.ll_draw_topic_answer)
        val btnDrawTopic: Button = findViewById(R.id.btn_draw_topic)
        btnDrawTopic.setOnClickListener {
            userMode =
                DRAW_MODE
            llDrawTopic.visibility = View.GONE
            myRoomWebSocketListener!!.sendMessage(
                MessageBean(
                    "startDraw",
                    userId,
                    userName,
                    roomId,
                    "",
                    false
                )
            )
        }
        if (userRole == MainActivity.ROOM_ROLE_MANAGER) {
            val llStartGame: LinearLayout = findViewById(R.id.ll_game_start)
            llStartGame.visibility = View.VISIBLE
            val btnStartGame: Button = findViewById(R.id.btn_game_start)
            btnStartGame.setOnClickListener {
                if (userListAdapter.itemCount > 1) {
                    llStartGame.visibility = View.GONE
                    getDrawTopic()
                } else {
                    Toast.makeText(this, "人數還不夠喔!!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
        }
    }

    private fun getDrawTopic() {
        val myRepository =
            MyRepository()
        GlobalScope.launch(Dispatchers.IO) {
            val topicDetailBean = myRepository.startDraw(roomId)
            if (topicDetailBean.result!!) {
                runOnUiThread {
                    val tvDrawTopic: TextView = findViewById(R.id.tv_draw_topic)
                    tvDrawTopic.text = topicDetailBean.topic
                    llDrawTopic.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun sendMessage(type: String) {
        if (myRoomWebSocketListener != null) {
            when (type) {
                "chat" -> {
                    val text = etChat.text.toString()
                    if (text.isEmpty()) {
                        return
                    }
                    val messageBean =
                        MessageBean(
                            type,
                            userId,
                            userName,
                            roomId,
                            text,
                            false
                        )
                    etChat.setText("")
                    myRoomWebSocketListener!!.sendMessage(messageBean)
                }
                "answer" -> {
                    val text = etMessage.text.toString()
                    if (text.isEmpty()) {
                        return
                    }
                    val messageBean =
                        MessageBean(
                            type,
                            userId,
                            userName,
                            roomId,
                            text,
                            false
                        )
                    etMessage.setText("")
                    myRoomWebSocketListener!!.sendMessage(messageBean)
                }
                "startGame" -> {

                }
                else -> {
                    println("sendMessage missing type")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initChatRoom() {

        myRoomWebSocketListener =
            RoomWebSocketListener(
                UserBean(
                    roomId,
                    userId,
                    userName,
                    userRole
                )
            )
        val outerClass = WeakReference(this)
        val myHandler =
            MyHandler(
                outerClass
            )

        MyWebSocket.createRoomWebSocket(myRoomWebSocketListener!!, roomId, userId)

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
                sizeNum.progress = Integer.parseInt(s.toString(), 10)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun eraserFun() {
        eraserMode = !eraserMode
        paintB.erase(eraserMode)
    }

    fun sizeChange() {
        colorpaint = ColorPaintBean(
            ((255 * colorR.progress) / colorR.max),
            ((255 * colorG.progress) / colorG.max),
            ((255 * colorB.progress) / colorB.max),
            sizeNumGet
        )
    }

    fun setColor() {
        colorpaint = ColorPaintBean(
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

    override fun onBackPressed() {
        println("onBackPressed")
        showDialog("確定要離開房間嗎?", "")
    }

    private fun showDialog(title: String, message: String) {

        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("確認") { dialog, whichButton ->
            Toast.makeText(this, "離開房間~", Toast.LENGTH_SHORT).show()
            finish()
        }

        builder.setNegativeButton("取消") { dialog, whichButton ->
            dialog.dismiss()
        }

        // create dialog and show it
        val dialog = builder.create()
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        println("onResume")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        println("onPause")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy")
//        Toast.makeText(this, "離開房間~", Toast.LENGTH_SHORT).show()
        // 倒數 後 退出房間
        // close webSocket 觸發 退出房間
        paintB.closeWebSocket()
        myRoomWebSocketListener?.close()
        // 避免webSocket connect 在 onDestroy() 之後，導致沒關閉webSocket
        myRoomWebSocketListener?.setFlag(RoomWebSocketListener.QUIT_FLAG)

    }

    fun addChatCardView(messageBean: MessageBean, text: String) {
        println(messageBean)
        val llChat: LinearLayout = findViewById(R.id.ll_chat)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val tmpGravity: Int = if (messageBean.userId == userId) { // 自己
            Gravity.END
        } else {
            Gravity.START
        }
        params.apply {
            gravity = tmpGravity
            topMargin = 10.dp
            rightMargin = 20.dp
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
            when (msg.what) {
                0 -> {
                    val messageBean = Gson().fromJson(msg.obj.toString(), MessageBean::class.java)
                    when (messageBean.type) {
                        "startDraw" -> {
                            // 拿答案..
                            outerClass.get()?.getDrawTopicDetail()
                            // 這回合換其他人畫
                            outerClass.get()?.tvMessage?.append("這回合換${messageBean.userName}畫畫喔~")
                        }
                        "nextDraw" -> {
                            // 這回合換你畫畫
                            // 當大家都倒數完之後，會發請求(ready)
                            // 當大家伺服器的狀態都是ready
                            // 發請求開始下一題
                            outerClass.get()?.tvMessage?.append("這回合換你畫畫喔~")
                            outerClass.get()?.getDrawTopic()
                        }
                        "chat" -> {
                            //某某人聊天
                            outerClass.get()?.addChatCardView(
                                messageBean,
                                "${messageBean.userName} : ${messageBean.message}"
                            )
                        }
                        "answer" -> {
                            //某某人猜答案
                            outerClass.get()?.checkAndSetAnswer(messageBean)
                        }
                        "join" -> {
                            //某某人加入房間
                            outerClass.get()
                                ?.addChatCardView(messageBean, "${messageBean.userName} 加入了喔!!")
                            outerClass.get()?.modifyUserList(messageBean)
                        }
                        "quit" -> {
                            //某某人離開房間
                            outerClass.get()
                                ?.addChatCardView(messageBean, "${messageBean.userName} 離開了喔!!")
                            outerClass.get()?.modifyUserList(messageBean)
                        }
                        "roomQuit" -> {
                            //房間關閉
                            Toast.makeText(
                                outerClass.get()?.getMyContext(),
                                "房間關閉了喔~",
                                Toast.LENGTH_SHORT
                            ).show()
                            outerClass.get()?.finish()
                        }
                        "gameStop" -> {
                            //畫圖的人離開房間
                            outerClass.get()?.mTimer?.cancelTimer()
                            // 準備下一題
                            // 跟server說你ready了
                            outerClass.get()?.myRoomWebSocketListener?.sendMessage(
                                MessageBean(
                                    "ready",
                                    outerClass.get()?.userId,
                                    outerClass.get()?.userName,
                                    outerClass.get()?.roomId,
                                    "",
                                    false
                                )
                            )
                        }
                        else -> {
                            println("handleMessage missing type!!")
                        }
                    }
                }
                1 -> {
                    val a = outerClass.get()?.getMyContext()
                    Toast.makeText(a, "與伺服器失去連線!!", Toast.LENGTH_SHORT).show()
                    outerClass.get()?.finish()
                }
                else -> {
                    println("handleMessage what not control!!")
                }
            }

        }
    }

    private fun getMyContext(): Context {
        return this
    }

    private fun checkAndSetAnswer(messageBean: MessageBean) {
        var text = ""
        if (messageBean.result!!) {
            // 答案正確
            if (messageBean.userId == userId) {
                // 自己的答案正確
                text = "恭喜你答對了喔！！\n"
                answerCurrentUIControl()
            } else {
                text = "恭喜${messageBean.userName}答對了喔！！\n"
            }
        } else {
            // 答案不正確
            text = messageBean.userName + " : " + messageBean.message + "\n"
        }
        tvMessage.append(text)
    }

    /*
    在畫畫開始前，就控制UI
    鎖大家的畫布、和答題EditView
     */
    private fun initUIControl() {
        userMode =
            INIT_MODE
        paintB.setUserMode(userMode)
        etMessage.isEnabled = false
    }

    /*
    在畫畫開始時，控制UI
    開啟要畫畫的人的畫布，鎖不用畫畫的人的畫布
    開啟不用畫畫的人的答題EditView，鎖要畫畫的人的答題EditView
     */
    private fun drawStartUIControl() {
        if (userMode == ANSWER_MODE) {
            paintB.setUserMode(ANSWER_MODE)
            etMessage.isEnabled = true
        } else if (userMode == DRAW_MODE) {
            paintB.setUserMode(DRAW_MODE)
            etMessage.isEnabled = false
        }
    }

    /*
    答對題目時，控制UI
    關閉答題EditView
     */
    private fun answerCurrentUIControl() {
        etMessage.isEnabled = false
    }

    private fun startDraw() {
        // 控制/鎖住 UI
        drawStartUIControl()
        // 開始倒數計時
        startTimer(30.toFloat())

    }

    private fun getDrawTopicDetail() {
        /* 跟 myRepository.startDraw(roomId)的差別 在於 不會指派 畫畫
         一樣會回傳題目 目前畫畫 下個畫畫的人
         */
        val myRepository =
            MyRepository()
        GlobalScope.launch(Dispatchers.IO) {
            val topicDetailBean = myRepository.getRoomTopic(roomId)

            userMode = if (userId == topicDetailBean.currentDrawUserId) {
                // 開啟畫畫模式
                DRAW_MODE
            } else {
                // 開啟答題模式
                ANSWER_MODE
            }
            runOnUiThread {
                startDraw()
            }
            answer = topicDetailBean.topic.toString()
        }
    }

    private fun initDrawers() {
        val btnInvite: Button = findViewById(R.id.btn_invite)
        btnInvite.setOnClickListener {
            val appLinkUrl =
                Config.HTTP_SCHEME + Config.HOST + Config.APP_LINK + "?" + Config.ROOM_ID_KEY + "=" + roomId + "&" + Config.ROOM_NAME_KEY + "=" + roomName
            val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("appLinkUrl", appLinkUrl)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "邀請網址已經複製了喔！！", Toast.LENGTH_SHORT).show()
        }

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
        val btnLeftNav: ImageButton = findViewById(R.id.btnLeftNav)
        val leftDrawerView: NavigationView = findViewById(R.id.nav_view_left)
        btnLeftNav.setOnClickListener {
            drawLayout.openDrawer(leftDrawerView)
        }

        // right drawer

        val btnRightNav: ImageButton = findViewById(R.id.btnRightNav)
        val rightDrawerView: NavigationView = findViewById(R.id.nav_view_right)
        btnRightNav.setOnClickListener {
            drawLayout.openDrawer(rightDrawerView)
        }

        initUserList()
    }

    private fun initUserList() {
        var usersList: MutableList<UserBean> = ArrayList()
        // must set up in main thread --start
        val userListView: RecyclerView = findViewById(R.id.view_user_list)
        userListAdapter = UserListAdapter(this, usersList)
        userListView.adapter = userListAdapter
        userListView.layoutManager = LinearLayoutManager(this)
        // must set up in main thread -- end

        val myRepository =
            MyRepository()
        GlobalScope.launch(Dispatchers.IO) {
            println(roomId)
            val roomBean = myRepository.getRoomUsers(roomId)
            usersList = roomBean.usersList!!
            runOnUiThread {
                userListAdapter.updateAll(usersList)
            }
        }
    }

    private fun modifyUserList(messageBean: MessageBean) {

        if (messageBean.type == "join") {
            if (messageBean.userId != userId) {
                userListAdapter.add(messageBean)
            }
        } else if (messageBean.type == "quit") {
            userListAdapter.remove(messageBean.userId!!)
        }
    }

    private fun startTimer(timeSec: Float) {
        mTimer = GameTimer(object :
            GameTimer.TimerBarController {
            override fun timerOnUpdate() {
//                println(mTimer.secondsCount * 100)
//                println("計時器進度條跳一次")
                update(-1, (mTimer!!.secondsCount * 100).toInt())
            }

            override fun timesUp() {
//                println("計時器進度條停止")
                // 清空畫布
                backgroundClean()
                // 控制/鎖住 UI
                initUIControl()
                // show answer
                showAnswer(6.toFloat())
            }
        })
        mTimer!!.secondsCount = timeSec
        mTimer!!.maxTimeInSeconds = timeSec
        progressBar.max = (timeSec * 100).toInt()
        progressBar.progress = (timeSec * 100).toInt()
        mTimer!!.startTimer()
    }

    private fun update(progressIncrement: Int, trueProgress: Int) {
        if (isAnimatingUpdatingDelayed) progressBar.incrementProgressBy(progressIncrement)
        else progressBar.progress = trueProgress
    }

    private fun showAnswer(timeSec: Float) {
        llDrawTopicAnswer.visibility = View.VISIBLE
        val tvDrawTopicAnswer: TextView = findViewById(R.id.tv_draw_topic_answer)
        tvDrawTopicAnswer.text = answer
        mTimer = GameTimer(object :
            GameTimer.TimerBarController {
            override fun timerOnUpdate() {
            }

            override fun timesUp() {
                llDrawTopicAnswer.visibility = View.GONE
                // 準備下一題
                // 跟server說你ready了
                myRoomWebSocketListener?.sendMessage(
                    MessageBean(
                        "ready",
                        userId,
                        userName,
                        roomId,
                        "",
                        false
                    )
                )
            }
        })
        mTimer!!.secondsCount = timeSec
        mTimer!!.maxTimeInSeconds = timeSec
        mTimer!!.startTimer()
    }
}
