package com.ntouandroid.drawandguess.colorPicker

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.drawtest.ColorPaint
import com.ntouandroid.drawandguess.R
import kotlinx.android.synthetic.main.activity_paint.*
import kotlinx.android.synthetic.main.colorpicker.*
import kotlinx.android.synthetic.main.sizechange.*

class Paint : AppCompatActivity() {

    lateinit var eraser: Button
    lateinit var size: Button
    var sizeNumGet: Float = 30.0f
    lateinit var clean: Button





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paint)

        eraser = findViewById(R.id.eraser)
        size = findViewById(R.id.size)

        btnColorSelected.setOnClickListener {
            colorSelector.visibility = View.VISIBLE
        }

        eraser.setOnClickListener{ eraserFun() }
        size.setOnClickListener{ sizeChange() }
        clean.setOnClickListener{ backgroundClean() }

        sizeNumPrint.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                sizeNum.progress = Integer.parseInt(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })

        strColor.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.length == 6){
                    colorR.progress = Integer.parseInt(s.substring(0..1), 16)
                    colorG.progress = Integer.parseInt(s.substring(2..3), 16)
                    colorB.progress = Integer.parseInt(s.substring(4..5), 16)
                } else if (s.length == 8){
                    colorR.progress = Integer.parseInt(s.substring(2..3), 16)
                    colorG.progress = Integer.parseInt(s.substring(4..5), 16)
                    colorB.progress = Integer.parseInt(s.substring(6..7), 16)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })

        colorR.max = 255
        colorR.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                           fromUser: Boolean) {
                val colorStr = getColorString()
                strColor.setText(colorStr.replace("#","").toUpperCase())
                btnColorPreview.setBackgroundColor(Color.parseColor(colorStr))
            }
        })

        colorG.max = 255
        colorG.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                           fromUser: Boolean) {
                val colorStr = getColorString()
                strColor.setText(colorStr.replace("#","").toUpperCase())
                btnColorPreview.setBackgroundColor(Color.parseColor(colorStr))
            }
        })

        colorB.max = 255
        colorB.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                           fromUser: Boolean) {
                val colorStr = getColorString()
                strColor.setText(colorStr.replace("#","").toUpperCase())
                btnColorPreview.setBackgroundColor(Color.parseColor(colorStr))
            }
        })

        colorCancelBtn.setOnClickListener {
        }

        colorOkBtn.setOnClickListener {
            sizeChange()
        }

        sizeNum.max = 100
        sizeNum.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int,
                                           fromUser: Boolean) {
                val sizeGet = Integer.toHexString(sizeNum.progress)
                sizeNumPrint.setText(sizeGet.replace("#","").toUpperCase())
            }
        })

        sizeCancelBtn.setOnClickListener {
            colorSelector.visibility = View.GONE
        }

        sizeOkBtn.setOnClickListener {
            val color:String = getColorString()
            setColor()
            btnColorSelected.setBackgroundColor(Color.parseColor(color))
            colorSelector.visibility = View.GONE
        }
    }
    companion object {
        var colorpaint = ColorPaint(0,0,0, 30.0f)
        var roomid: String = ""
        var userid: String = ""
        var nextid: String = ""
    }

    fun eraserFun(){
        colorpaint = ColorPaint(0,0,0, 30.0f)
    }

    fun sizeChange(){
        colorpaint = ColorPaint(((255 * colorR.progress) / colorR.max),((255 * colorG.progress) / colorG.max),((255 * colorB.progress) / colorB.max), sizeNumGet)
    }

    fun setColor(){
        colorpaint = ColorPaint(((255 * colorR.progress) / colorR.max),((255 * colorG.progress) / colorG.max),((255 * colorB.progress) / colorB.max), sizeNumGet)
    }

    fun backgroundClean(){

    }

    fun getColorString(): String {
        var r = Integer.toHexString(((255*colorR.progress)/colorR.max))
        if(r.length==1) r = "0"+r
        var g = Integer.toHexString(((255*colorG.progress)/colorG.max))
        if(g.length==1) g = "0"+g
        var b = Integer.toHexString(((255*colorB.progress)/colorB.max))
        if(b.length==1) b = "0"+b
        return "#" + r + g + b
    }
}
