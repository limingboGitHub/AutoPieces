package com.example.autopieces

import androidx.lifecycle.MutableLiveData
import kotlin.math.max

class Player{
    var level = MutableLiveData<Int>()
    var money = MutableLiveData<Int>()
    var currentExp = MutableLiveData<Int>()
    var maxExp = MutableLiveData<Int>()

    private val maxExpArray = arrayOf(
        2,4,6,10,18,30,46,70,9999
    )

    init {
        level.value = 1
        money.value = 0
        currentExp.value = 0
        maxExp.value = maxExpArray[0]
    }

    fun getLevel() = level.value?:1

    fun getMoney() = money.value?:0

    fun addExp(exp:Int){
        val level = getLevel()
        if (level == 9)
            return

        val currentExp = currentExp.value?:0
        val maxExp = maxExp.value?:0

        if (currentExp+exp >= maxExp){
            val restExp = currentExp+exp - maxExp
            this.level.value = level + 1
            this.currentExp.value = 0
            this.maxExp.value = maxExpArray[level]
            addExp(restExp)
        }else{
            this.currentExp.value = currentExp + exp
        }
    }
}