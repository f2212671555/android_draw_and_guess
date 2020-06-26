package com.ntouandroid.drawandguess.view.colorPicker


import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Base64
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.drawtest.ColorPaintBean
import com.ntouandroid.drawandguess.model.service.MyWebSocket
import com.ntouandroid.drawandguess.model.webSocket.DrawWebSocketListener
import com.ntouandroid.drawandguess.view.activity.PaintActivity
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference


@RequiresApi(Build.VERSION_CODES.O)
class PaintBoard(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var paint: Paint? = null
    private var bitmap: Bitmap? = null
    private var mCanvas: Canvas? = null

    private var startX: Float = 0f
    private var startY: Float = 0f
    private var r: Int = 0
    private var g: Int = 0
    private var b: Int = 0
    private var size: Float = 0f
    private var myDrawWebSocketListener: DrawWebSocketListener? = null
    private var mWidth = 0
    private var mHeight = 0
    private var userMode = ""

    fun init(width: Int, height: Int): PaintBoard {
        println("HI INIT")

        userMode = PaintActivity.INIT_MODE
        // bitmap
        mWidth = width
        mHeight = height
        bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565)

        // Canvas                   畫布
        mCanvas = Canvas(bitmap!!)
        mCanvas?.drawColor(Color.argb(255, 0, 0, 0))

        // Paint                     畫筆

        r = PaintActivity.colorpaint.r
        g = PaintActivity.colorpaint.g
        b = PaintActivity.colorpaint.b
        paint = Paint()
        paint?.color = Color.rgb(r, g, b)
        paint?.strokeWidth = 10f

        invalidate()
        return this
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initDrawRoom(roomId: String, userId: String) {
        myDrawWebSocketListener =
            DrawWebSocketListener()
        val outerClass = WeakReference(this)
        val myHandler = PaintBoard.MyHandler(outerClass)
        MyWebSocket.createDrawWebSocket(
            myDrawWebSocketListener!!,
            roomId,
            userId
        )
        myDrawWebSocketListener!!.setHandler(myHandler)
    }

    fun closeWebSocket() {
        myDrawWebSocketListener?.close()
    }

    fun cleanBackground() {
        println("cleanBackground")
        bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565)
        mCanvas = Canvas(bitmap!!)
        mCanvas?.drawColor(Color.argb(255, 0, 0, 0))
        sendDrawToServer()
        invalidate()
    }

    fun erase(isErase: Boolean) {
        PaintActivity.colorpaint = ColorPaintBean(0, 0, 0, 50.0f)

    }

    fun changeColor() {
        r = PaintActivity.colorpaint.r
        g = PaintActivity.colorpaint.g
        b = PaintActivity.colorpaint.b

        paint?.color = Color.rgb(r, g, b)
    }

    fun sizeChange() {
        size = PaintActivity.colorpaint.size

        paint?.strokeWidth = size
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (bitmap != null && paint != null) {
            canvas?.drawBitmap(bitmap!!, 0f, 0f, paint)
        }

    }

    private var count = 0
    private fun sendDrawToServer() {
        Thread{
            val str = convertBitmapToString(bitmap)
            myDrawWebSocketListener?.getWebSocket()?.send(str)
            Thread.sleep(10)
        }.start()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        println("HI onTouchEvent")
        if (userMode != PaintActivity.DRAW_MODE) {
            return false
        }

        println("HI draw")
        changeColor()
        sizeChange()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y

            }
            MotionEvent.ACTION_MOVE -> {
                val stopX = event.x
                val stopY = event.y

                if (paint != null && mCanvas != null) {
                    mCanvas?.drawLine(startX, startY, stopX, stopY, paint!!)

                    startX = event.x
                    startY = event.y

                    invalidate()
                    sendDrawToServer()
                }
            }
        }
        return true
    }

    private fun convertBitmapToString(bitmap: Bitmap?): String {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
    }

    private fun convertStringToBitmap(str: String?): Bitmap {
        val byteArray = Base64.decode(str, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return Bitmap.createScaledBitmap(bitmap!!, mWidth, mHeight, true)
    }

    // for other player draw
    fun draw(bitmap: Bitmap?) {
        if (mCanvas != null && paint != null && bitmap != null) {
            mCanvas?.drawBitmap(bitmap, 0f, 0f, paint)
            invalidate()
            bitmap.recycle()
        }
    }

    fun setUserMode(userMode: String) {
        this.userMode = userMode
    }

    // Declare the Handler as a static class.
    class MyHandler(private val outerClass: WeakReference<PaintBoard>) : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> {
                    val text = msg.obj.toString()
                    outerClass.get()?.draw(outerClass.get()?.convertStringToBitmap(text))
//                    val paintBoardDrawBean = Gson().fromJson(text, PaintBoardDrawBean::class.java)
//                    outerClass.get()?.paintBoardDrawBeanActionDispatcher(paintBoardDrawBean)
                }
                1 -> {
                    val a = outerClass.get()?.context as Activity
                    Toast.makeText(a, "與伺服器失去連線!!", Toast.LENGTH_SHORT).show()
                    a.finish()
                }
                else -> {
                    println("handleMessage what not control!!")
                }
            }
        }
    }
}