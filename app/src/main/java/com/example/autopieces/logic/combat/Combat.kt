package com.example.autopieces.logic.combat

import com.example.autopieces.logic.map.CombatZone
import com.example.autopieces.logic.map.MapRole

class Combat(
        val combatZone : CombatZone
) {

    /**
     * 战斗时间30秒
     */
    var combatTime : Long = 30 * 1000

    /**
     * 加时时长
     */
    var extraTime : Long = 10 * 1000

    /**
     * 战斗线程
     */
    private val combatThread = Thread({
        val startTime = System.currentTimeMillis()
        //每10ms更新一次战斗
        while (combatTime>0){
            combatZone.action()

            combatTime-=System.currentTimeMillis()-startTime
        }
    },"combatThread")

    /**
     * 战斗开始
     */
    fun start(){
        combatThread.start()
    }
}