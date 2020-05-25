package com.ntouandroid.drawandguess

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.drawtest.ColorPaint
import com.ntouandroid.drawandguess.colorPicker.PaintBoard
import com.ntouandroid.drawandguess.listener.ArchLifecycleApp
import com.ntouandroid.drawandguess.repository.MyRepository
import com.ntouandroid.drawandguess.service.MyWebSocket
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

    lateinit var colorR: SeekBar
    lateinit var colorG: SeekBar
    lateinit var colorB: SeekBar
    lateinit var strColor: EditText

    lateinit var etMessage: EditText
    lateinit var tvMessage: TextView
    lateinit var btnSendMessage: Button
    private var myRoomWebSocketListener: RoomWebSocketListener? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paint)

        lateinit var btnColorPreview: Button
        eraser = findViewById(R.id.eraser)
        size = findViewById(R.id.size)
        etMessage = findViewById(R.id.message_et)
        tvMessage = findViewById(R.id.tv_recieve)
        btnSendMessage = findViewById(R.id.send_message_btn)

        clean = findViewById(R.id.backgroundClean)
        btnColorSelected = findViewById(R.id.btnColorSelected)

        initColorPickerDialog()
        eraser.setOnClickListener { eraserFun() }
        initSizeChangeDialog()
        clean.setOnClickListener { backgroundClean() }

        roomid = intent.getStringExtra("roomid")
        userid = intent.getStringExtra("userid")
        userName = intent.getStringExtra("userName")

        val paintB: PaintBoard = findViewById(R.id.layout_paint_board)
        paintB.post(Runnable {
            paintB.init(paintB.width, paintB.height).initDrawRoom(roomid, userid)
        })
        initChatRoom()

        btnSendMessage.setOnClickListener { sendMessage() }


    }

    private fun sendMessage() {
        if (myRoomWebSocketListener != null) {
            myRoomWebSocketListener!!.getWebSocket()?.send(etMessage.text.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initChatRoom() {

        myRoomWebSocketListener = RoomWebSocketListener()
        val outerClass = WeakReference(this)
        val myHandler = MyHandler(outerClass)
        GlobalScope.launch(Dispatchers.IO) {

            MyWebSocket.createRoomWebSocket(myRoomWebSocketListener!!, roomid, userid)

            myRoomWebSocketListener!!.setHandler(myHandler)
        }

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

    fun eraserFun() {
        colorpaint = ColorPaint(0, 0, 0, 30.0f)
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

    fun backgroundClean() {

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

    override fun onPause() {
        super.onPause()
        println("onPause")
        val myRepository: MyRepository = MyRepository()
        GlobalScope.launch(Dispatchers.IO) {
            val respUserActionRoomBean = myRepository.quitRoom(userid, roomid)
            println(respUserActionRoomBean)
            if(respUserActionRoomBean.result!!){ // quit game success
            } else{// quit game failure

            }
        }
//        ArchLifecycleApp.userStatus = ArchLifecycleApp.QUIT_ROOM
    }

    class MyHandler(private val outerClass: WeakReference<PaintActivity>) : Handler() {
        override fun handleMessage(msg: Message) {
            outerClass.get()?.tvMessage?.append(msg?.obj.toString())
        }

    }
}
