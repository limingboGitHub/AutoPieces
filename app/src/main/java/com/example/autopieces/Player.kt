package com.example.autopieces

import androidx.lifecycle.MutableLiveData
import kotlin.math.max

class Player{
    var level = 1
    var money = 0
    var currentExp = 0
    var maxExp = 2

    companion object{
        val maxExpArray = arrayOf(
            2,4,6,10,18,30,46,70,9999
        )
    }
}