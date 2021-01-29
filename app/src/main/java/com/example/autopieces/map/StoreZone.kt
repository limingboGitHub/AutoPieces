package com.example.autopieces.map

/**
 * 商店区域
 */
class StoreZone : Zone(STORE_ITEM_NUM){

    companion object{
        const val STORE_ITEM_NUM = 5
    }

    override fun zoneBelongWhere(): String {
        return Position.POSITION_STORE
    }
}