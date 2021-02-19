package com.example.autopieces.role

import com.example.autopieces.map.MapView

data class Role(
    var name:String,
    val cost : Int = 1,
    var level : Int = 1
){

    fun levelUp(){
        level++
    }
}