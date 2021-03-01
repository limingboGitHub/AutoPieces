package com.example.autopieces.view.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.example.autopieces.utils.logE
import java.lang.StringBuilder

class StarTextView(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {

    override fun setText(text: CharSequence, type: BufferType) {
        val stringBuilder = StringBuilder()
        repeat(text.toString().toInt()){
            stringBuilder.append("★")
        }
        super.setText(stringBuilder.toString(), type)
    }
}