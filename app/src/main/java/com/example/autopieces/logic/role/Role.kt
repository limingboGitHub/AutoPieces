package com.example.autopieces.logic.role

data class Role(
    var name:String,
    val cost : Int = 1,
    var level : Int = 1
){

    fun levelUp(){
        level++
    }
}