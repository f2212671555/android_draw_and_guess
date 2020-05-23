package com.ntouandroid.drawandguess.colorPicker

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


import java.io.OutputStream


class PaintBoard(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var paint: Paint
    private var bitmap: Bitmap
    private var mCanvas: Canvas

    private var startX: Float = 0f
    private var startY: Float = 0f






    init {
        // bitmap
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Canvas                   畫布
        mCanvas = Canvas(bitmap)
        mCanvas.drawColor(Color.argb(255,0,0,0))

        // Paint                     畫筆
        //paint.setAntiAlias(true);//抗锯齿功能
        //paint.setStyle(Style.FILL);//设置填充样式，Style.STOKE 为空心
        //paint.setStrokeWidth(30);//设置画笔宽度
        //paint.setShadowLayer(10, 15, 15, Color.GREEN);//设置阴影

        var r: Int = com.ntouandroid.drawandguess.colorPicker.PaintActivity.colorpaint.r
        var g: Int = com.ntouandroid.drawandguess.colorPicker.PaintActivity.colorpaint.g
        var b: Int = com.ntouandroid.drawandguess.colorPicker.PaintActivity.colorpaint.b

        paint = Paint()
        paint
        paint.color = Color.rgb(r,g,b)
        paint.strokeWidth = 10f



    }

    fun changeColor(){
        var r: Int = com.ntouandroid.drawandguess.colorPicker.PaintActivity.colorpaint.r
        var g: Int = com.ntouandroid.drawandguess.colorPicker.PaintActivity.colorpaint.g
        var b: Int = com.ntouandroid.drawandguess.colorPicker.PaintActivity.colorpaint.b

        paint.color = Color.rgb(r, g, b)
    }

    fun sizeChange(){
        var size: Float = com.ntouandroid.drawandguess.colorPicker.PaintActivity.colorpaint.size

        paint.strokeWidth = size.toFloat();//设置画笔宽度
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)


        canvas!!.drawBitmap(bitmap, 0f, 0f, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if(PaintActivity.userid!=PaintActivity.nextid){
            return true
        }


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

                // call onDraw
                invalidate()
            }
        }

        return true
    }

    fun clean(){
        mCanvas.drawColor(Color.argb(255,0,0,0))
    }

    fun saveBitmap(stream: OutputStream) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    }



    // Declare the Handler as a static class.
}