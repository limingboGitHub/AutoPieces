package com.example.autopieces.view.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.autopieces.R
import com.example.autopieces.logic.role.Role

class StoreAdapter : BaseQuickAdapter<Role,BaseViewHolder>(R.layout.item_store){

    override fun convert(holder: BaseViewHolder, item: Role) {
        holder.setText(R.id.name_tv,item.name)
    }
}