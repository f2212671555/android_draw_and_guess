package com.ntouandroid.drawandguess.colorPicker


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import com.example.drawtest.ColorPaint
import com.google.gson.Gson
import com.ntouandroid.drawandguess.PaintActivity
import com.ntouandroid.drawandguess.bean.PaintBoardDraw
import com.ntouandroid.drawandguess.service.MyWebSocket
import com.ntouandroid.drawandguess.webSocket.DrawWebSocketListener
import java.io.OutputStream
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


    fun init(width: Int, height: Int): PaintBoard {
        println("HI INIT")
        // bitmap
        mWidth = width
        mHeight = height
        bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)

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

//        paint?.isAntiAlias = true;//抗锯齿功能
//        paint?.style = Paint.Style.FILL;//设置填充样式，Style.STOKE 为空心
        //paint.setStrokeWidth(30);//设置画笔宽度
        //paint.setShadowLayer(10, 15, 15, Color.GREEN);//设置阴影
        invalidate()
        return this
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initDrawRoom(roomId: String, userId: String) {
        myDrawWebSocketListener = DrawWebSocketListener()
        val outerClass = WeakReference(this)
        val myHandler = PaintBoard.MyHandler(outerClass)
        MyWebSocket.createDrawWebSocket(
            myDrawWebSocketListener!!,
            roomId,
            userId
        )
        myDrawWebSocketListener!!.setHandler(myHandler)
    }

    fun cleanBackground() {
        val paintBoardDraw = PaintBoardDraw(
            "clean",
            "",
            0f,
            0f,
            0f,
            0f,
            0,
            0,
            0,
            0f
        )
        sendDrawToServer(paintBoardDraw)
        bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(bitmap!!)
        mCanvas?.drawColor(Color.argb(255, 0, 0, 0))
        invalidate()
    }

    fun erase(isErase: Boolean) {
        PaintActivity.colorpaint = ColorPaint(0, 0, 0, 50.0f)
//        if(isErase){
//            paint?.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
//        }else{
//            paint?.xfermode = null
//        }

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

    private fun sendDrawToServer(paintBoardDraw: PaintBoardDraw) {
        val jsonStr = Gson().toJson(paintBoardDraw)
        myDrawWebSocketListener?.getWebSocket()?.send(jsonStr)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        println("HI onTouchEvent")
//        if(PaintActivity.userid!= PaintActivity.nextid){
//            return true
//        }


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
                    // dp to px
                    val paintBoardDraw = PaintBoardDraw(
                        "draw",
                        "drawLine",
                        startX / mWidth,
                        startY / mHeight,
                        stopX / mWidth,
                        stopY / mHeight,
                        r,
                        g,
                        b,
                        size / (mHeight * mWidth)
                    )
                    startX = event.x
                    startY = event.y


                    sendDrawToServer(paintBoardDraw)

                    // call onDraw
//                draw(paintBoardDraw)
                    invalidate()
                }

            }
        }

        return true
    }

    fun saveBitmap(stream: OutputStream) {
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    }

    // for other player draw
    private fun setColorAndSize(paintBDBean: PaintBoardDraw) {
        r = paintBDBean.r
        g = paintBDBean.g
        b = paintBDBean.b

        paint?.color = Color.rgb(r, g, b)

        size = paintBDBean.size * mWidth * mHeight

        paint?.strokeWidth = size
    }

    fun paintBoardDrawBeanActionDispatcher(paintBDBean: PaintBoardDraw) {
        when (paintBDBean.action) {
            "draw" -> draw(paintBDBean)
            "clean" -> cleanBackground()
        }
    }

    // for other player draw
    fun draw(paintBDBean: PaintBoardDraw) {
        setColorAndSize(paintBDBean)
        if (mCanvas != null && paint != null) {
            mCanvas?.drawLine(
                paintBDBean.startX * mWidth,
                paintBDBean.startY * mHeight,
                paintBDBean.stopX * mWidth,
                paintBDBean.stopY * mHeight,
                paint!!
            )
            invalidate()
        }

    }

    // Declare the Handler as a static class.
    class MyHandler(private val outerClass: WeakReference<PaintBoard>) : Handler() {
        override fun handleMessage(msg: Message) {
            val text = msg?.obj.toString()
            val paintBoardDrawBean = Gson().fromJson(text, PaintBoardDraw::class.java)
            outerClass.get()?.paintBoardDrawBeanActionDispatcher(paintBoardDrawBean)
        }

    }
}