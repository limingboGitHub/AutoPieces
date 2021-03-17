package com.example.autopieces.logic.combat

import android.util.Log
import com.example.autopieces.logic.map.CombatZone
import com.example.autopieces.logic.map.MapRole
import com.example.autopieces.utils.logD
import com.example.autopieces.utils.logE

class Combat(
        val combatZone : CombatZone
) {
    val TAG = "Combat"

    /**
     * 战斗时间30秒
     */
    var combatTime : Long = 10 * 1000

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
        var actionTime = 0L
        while (combatTime > 0
            &&!combatZone.isCombatEnd()) {
            lastActionTime = System.currentTimeMillis()
            combatZone.action(actionTime.toInt())

            actionTime = System.currentTimeMillis() - lastActionTime
            combatTime = (combatTime - actionTime).coerceAtLeast(0)
//            logE(TAG,"战斗剩余时间:${combatTime/1000}")
        }
        logE(TAG,"战斗时间结束")
    }, "combatThread")

    /**
     * 战斗开始
     */
    fun start(){
        combatThread.start()
    }

    /**
     * 战斗是否结束
     */
    fun isEnd() = combatTime <=0 || combatZone.isCombatEnd()
}