package com.example.autopieces

import androidx.lifecycle.MutableLiveData
import kotlin.math.max

class Player{
    var level = MutableLiveData<Int>()
    var money = MutableLiveData<Int>()
    var currentExp = MutableLiveData<Int>()
    var maxExp = MutableLiveData<Int>()

    private val maxExpArray = arrayOf(
        10,10,4,10,20,40,60,80
    )

    init {
        level.value = 0
        money.value = 0
        currentExp.value = 0
        maxExp.value = 10
    }

    fun addExp(exp:Int){
        val currentExp = currentExp.value?:0
        val maxExp = maxExp.value?:0
        if (currentExp+exp >= maxExp){
            val restExp = currentExp+exp - maxExp
            val level = this.level.value?:0 +1
            this.level.value = level
            this.currentExp.value = restExp
            this.maxExp.value = maxExpArray[level-1]
        }else{
            this.currentExp.value = currentExp + exp
        }
    }
}