package com.ntouandroid.drawandguess.filter

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Pattern

class NameInputFilter : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val speChat =  "[`~!@#$%^&*()+=|{}':;’,\\[\\].<>/?～！＠＃￥％…＆＊（）—＿＋｜『』【】‘；：”“。，、？\n]"
        val pattern = Pattern.compile(speChat);
        val matcher = pattern.matcher(source.toString())
        return if (matcher.find()) "";
        else null;
    }
}