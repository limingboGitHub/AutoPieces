package com.example.autopieces.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.autopieces.R
import com.lmb.lmbkit.utils.getScreenWidth
import kotlinx.android.synthetic.main.item_store.view.*

class StoreView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs){

    val cardNum = 5
    val cardSpace = resources.getDimensionPixelOffset(R.dimen.card_space)
    var cardWidth = (getScreenWidth() - (cardNum+1)*cardSpace) / cardNum

    init {
        orientation = HORIZONTAL

        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        layoutParams.weight = cardWidth.toFloat()

        repeat(5){
            val card = LayoutInflater.from(context).inflate(R.layout.item_store,this,false)
            card.layoutParams = layoutParams
            addView(card)
        }
    }
}