package com.example.autopieces

import androidx.lifecycle.MutableLiveData

data class Player(
    var level:Int = 1,
    var currentExp:Int = 0,
    var maxExp:Int = 2
) {
    var money = MutableLiveData<Int>()
}