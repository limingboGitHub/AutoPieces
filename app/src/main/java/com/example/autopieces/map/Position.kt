package com.example.autopieces.map

//角色的位置
class Position(
        var where : String = POSITION_STORE,
        var x:Int = 0,
        var y:Int = 0
) {

    companion object{
        /**
         * 角色的位置
         */
        val POSITION_STORE = "store"
        val POSITION_READY = "ready"
        val POSITION_COMBAT = "combat"
        val POSITION_OTHER = "other"
    }
}