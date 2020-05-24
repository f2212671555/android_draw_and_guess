package com.ntouandroid.drawandguess.colorPicker


import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.res.Resources
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
import com.google.gson.Gson
import com.ntouandroid.drawandguess.PaintActivity
import com.ntouandroid.drawandguess.bean.PaintBoardDraw
import com.ntouandroid.drawandguess.service.MyWebSocket
import com.ntouandroid.drawandguess.webSocket.DrawWebSocketListener
import java.io.OutputStream
import java.lang.ref.WeakReference


@RequiresApi(Build.VERSION_CODES.O)
class PaintBoard(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var paint: Paint
    private var bitmap: Bitmap
    private var mCanvas: Canvas

    private var startX: Float = 0f
    private var startY: Float = 0f
    private var myDrawWebSocketListener: DrawWebSocketListener? = null

    init {

        // bitmap
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Canvas                   畫布
        mCanvas = Canvas(bitmap)
        mCanvas.drawColor(Color.argb(255, 0, 0, 0))

        // Paint                     畫筆
        //paint.setAntiAlias(true);//抗锯齿功能
        //paint.setStyle(Style.FILL);//设置填充样式，Style.STOKE 为空心
        //paint.setStrokeWidth(30);//设置画笔宽度
        //paint.setShadowLayer(10, 15, 15, Color.GREEN);//设置阴影

        var r: Int = PaintActivity.colorpaint.r
        var g: Int = PaintActivity.colorpaint.g
        var b: Int = PaintActivity.colorpaint.b

        paint = Paint()
        paint
        paint.color = Color.rgb(r, g, b)
        paint.strokeWidth = 10f



    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initDrawRoom(roomId:String,userId:String) {
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

    fun changeColor() {
        var r: Int = PaintActivity.colorpaint.r
        var g: Int = PaintActivity.colorpaint.g
        var b: Int = PaintActivity.colorpaint.b

        paint.color = Color.rgb(r, g, b)
    }

    fun sizeChange() {
        var size: Float = PaintActivity.colorpaint.size

        paint.strokeWidth = size.toFloat();//设置画笔宽度
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.drawBitmap(bitmap, 0f, 0f, paint)
    }

    private fun sendDrawToServer(paintBoardDraw: PaintBoardDraw) {
        val jsonStr = Gson().toJson(paintBoardDraw)
        myDrawWebSocketListener?.getWebSocket()?.send(jsonStr)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

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



                mCanvas.drawLine(startX, startY, stopX, stopY, paint)
                startX = event.x
                startY = event.y

                val paintBoardDraw = PaintBoardDraw("",startX,startY,stopX,stopY)
                sendDrawToServer(paintBoardDraw)

                // call onDraw
                invalidate()
            }
        }

        return true
    }

    fun clean() {
        mCanvas.drawColor(Color.argb(255, 0, 0, 0))
    }

    fun saveBitmap(stream: OutputStream) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    }

    fun draw(paintBDBean: PaintBoardDraw) {
        changeColor()
        sizeChange()
        mCanvas.drawLine(paintBDBean.startX, paintBDBean.startY, paintBDBean.stopX, paintBDBean.stopY, paint)
        invalidate()
    }

    // Declare the Handler as a static class.
    class MyHandler(private val outerClass: WeakReference<PaintBoard>) : Handler() {
        override fun handleMessage(msg: Message) {
            val text = msg?.obj.toString()
            val paintBoardDrawBean = Gson().fromJson(text, PaintBoardDraw::class.java)
            outerClass.get()?.draw(paintBoardDrawBean)
        }

    }
}