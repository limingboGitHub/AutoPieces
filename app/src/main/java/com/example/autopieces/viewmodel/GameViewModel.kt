package com.example.autopieces.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.autopieces.logic.Player
import com.example.autopieces.logic.Player.Companion.maxExpArray
import com.example.autopieces.logic.map.GameMap

class GameViewModel : ViewModel(){

    /**
     * 玩家
     */
    val player = MutableLiveData<Player>()

    val gameMap = GameMap()

    fun getPlayer(): Player {
        return player.value?: Player()
    }

    fun addMoney(toAddMoney:Int){
        val player = getPlayer()
        player.money += toAddMoney
        this.player.value = player
    }

    fun useMoney(toUseMoney:Int):Boolean{
        val player = getPlayer()
        return if (player.money>=toUseMoney){
            player.money -= toUseMoney
            this.player.value = player
            true
        }else
            false
    }

    fun addExp(exp:Int){
        val player = getPlayer()

        if (player.level == 9)
            return

        if (player.currentExp+exp >= player.maxExp){
            val restExp = player.currentExp+exp - player.maxExp
            player.level = player.level + 1
            player.currentExp = 0
            player.maxExp = maxExpArray[player.level]
            addExp(restExp)
        }else{
            player.currentExp = player.currentExp + exp
        }
        this.player.value = player
    }
}