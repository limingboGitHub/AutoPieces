package com.example.autopieces.logic.combat

import android.util.Log
import com.example.autopieces.logic.map.CombatZone
import com.example.autopieces.logic.map.MapRole
import com.example.autopieces.utils.logD

class Combat(
        val combatZone : CombatZone
) {
    val TAG = "Combat"

    /**
     * 战斗时间30秒
     */
    var combatTime : Long = 30 * 1000

    /**
     * 加时时长
     */
    var extraTime : Long = 10 * 1000

    /**
     * 行动时间
     */
    var lastActionTime = 0L

    /**
     * 战斗线程
     */
    private val combatThread = Thread({
        val startTime = System.currentTimeMillis()
        //每10ms更新一次战斗
        while (combatTime > 0) {
            lastActionTime = System.currentTimeMillis()
            combatZone.action()

            val costTime = System.currentTimeMillis() - lastActionTime
            combatTime -= costTime
            logD(TAG,"战斗剩余时间:${combatTime/1000}")
        }
    }, "combatThread")

    /**
     * 战斗开始
     */
    fun start(){
        combatThread.start()
    }
}