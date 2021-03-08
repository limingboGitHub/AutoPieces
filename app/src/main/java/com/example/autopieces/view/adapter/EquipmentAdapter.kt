package com.example.autopieces.view.adapter

import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.autopieces.R
import com.example.autopieces.logic.Equipment
import kotlin.math.abs

class EquipmentAdapter : BaseQuickAdapter<Equipment,BaseViewHolder>(R.layout.item_equipment){

    var scrollUpListener : ScrollUpListener? = null

    var pressX = 0f
    var pressY = 0f

    init {
        addChildClickViewIds(R.id.name_tv)
    }

    override fun convert(holder: BaseViewHolder, item: Equipment) {
        holder.setText(R.id.name_tv,item.name)

//        val nameTv = holder.getView<View>(R.id.name_tv).setOnTouchListener { v, event ->
//            when(event.action){
//                MotionEvent.ACTION_DOWN -> {
//                    pressX = event.rawX
//                    pressY = event.rawY
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    if (abs(pressX-event.rawX)<30 &&
//                            pressY-event.rawY > 30){
//                        scrollUpListener?.scrollUp(event.rawX,event.rawY)
//                    }
//                }
//                MotionEvent.ACTION_UP ->{
//                    if (abs(pressX-event.rawX)<20 &&
//                            abs(pressY-event.rawY)<20)
//                                v.performClick()
//                }
//            }
//            false
//        }
    }


    interface ScrollUpListener{
        fun scrollUp(x:Float,y:Float)
    }


}