package com.example.autopieces.logic.map

import java.io.Closeable

//角色的位置
data class Position(
        var where : String = POSITION_STORE,
        var x:Int = 0,
        var y:Int = 0
) {

    companion object{
        /**
         * 角色的位置
         */
        val POSITION_STORE = "store"
        val POSITION_EQUIPMENT = "equipment"
        val POSITION_READY = "ready"
        val POSITION_COMBAT = "combat"
        val POSITION_STORE_DOWN = "storeDown"
        val POSITION_OTHER = "other"
    }
}