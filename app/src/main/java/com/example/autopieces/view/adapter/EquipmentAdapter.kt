package com.example.autopieces.view.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.autopieces.R
import com.example.autopieces.logic.Equipment

class EquipmentAdapter : BaseQuickAdapter<Equipment,BaseViewHolder>(R.layout.item_equipment){
    override fun convert(holder: BaseViewHolder, item: Equipment) {
        holder.setText(R.id.name_tv,item.name)
    }
}