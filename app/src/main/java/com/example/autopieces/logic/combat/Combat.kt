package com.example.autopieces.logic.combat

import com.example.autopieces.logic.combat.record.*
import com.example.autopieces.logic.map.CombatZone
import com.example.autopieces.logic.map.MapRole
import com.example.autopieces.logic.map.Position
import com.example.autopieces.logic.role.createMovePlaceholder
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
     * 战斗记录
     * 每个事件都会被记录下来，供UI层使用
     */
    var combatRecordList = ArrayList<CombatRecord>()

    /**
     * 战斗记录更新回调
     * 一旦产生一条新的战斗记录就回调该函数
     */
    private var recordUpdateCallback : (combatRecord:CombatRecord)->Unit = {}

    /**
     * 战斗线程
     */
    private val combatThread = Thread({
        var actionTime = 0L
        while (combatTime > 0
            &&!combatZone.isCombatEnd()) {
            lastActionTime = System.currentTimeMillis()

            action(actionTime.toInt())

            actionTime = System.currentTimeMillis() - lastActionTime
            combatTime = (combatTime - actionTime).coerceAtLeast(0)
//            logE(TAG,"战斗剩余时间:${combatTime/1000}")
        }
        logE(TAG,"战斗时间结束")
    }, "combatThread")

    /**
     * 战斗开始
     */
    fun start(recordUpdateCallback:(combatRecord:CombatRecord)->Unit){
        this.recordUpdateCallback = recordUpdateCallback
        combatThread.start()
    }

    /**
     * 战斗是否结束
     */
    fun isEnd() = combatTime <=0 || combatZone.isCombatEnd()

    /**
     * 开始行动
     */
    fun action(time:Int){
        combatZone.getAllRole().forEach {
            if (!it.isAlive)
                return@forEach

            it.stateRestTime = (it.stateRestTime-time).coerceAtLeast(0)
            //当前攻击状态
            when(it.state){
                MapRole.STATE_IDLE -> {
                    val isFindRole = combatZone.findRoleToAttack(it)
                    if (isFindRole) {
                        //进入前摇状态
                        it.changeState(MapRole.STATE_BEFORE_ATTACK)
                    } else {
                        //进入移动状态
                        combatZone.findRoleToMove(it)?.apply {
                            if (combatZone.getRoleByIndex(first,second) == null) {
                                combatZone.addRole(createMovePlaceholder(), first, second)
                                it.moveTarget = this
                                it.changeState(MapRole.STATE_MOVING)
                            }
                        }
                    }
                }
                MapRole.STATE_BEFORE_ATTACK->{
                    if (it.stateRestTime<=0){
                        //生成一条攻击记录
                        addCombatRecord(AttackRecord(it,it.beAttackedRoles))
                        //处理攻击逻辑
                        attackRole(it)
                        //进入后摇
                        it.changeState(MapRole.STATE_AFTER_ATTACK)
                    }
                }
                MapRole.STATE_AFTER_ATTACK->{
                    if (it.stateRestTime<=0)
                        it.changeState(MapRole.STATE_IDLE)
                }
                MapRole.STATE_MOVING ->{
                    if (it.stateRestTime<=0){
                        it.moveTarget?.apply {
                            //增加一条移动记录
                            addCombatRecord(MoveRecord(it,it.position.copy(),Position(Position.POSITION_COMBAT,this.first,this.second)))
                            //从区域中旧位置删除，添加到新位置
                            combatZone.removeRole(it)
                            combatZone.addRole(it,this.first,this.second)
                        }
                        it.moveTarget = null
                        it.changeState(MapRole.STATE_IDLE)
                        logE(TAG,"${it.role.name}移动到了(${it.position.x},${it.position.y})")
                    }
                }
            }
        }
    }

    private fun attackRole(mapRole: MapRole) {
        mapRole.beAttackedRoles.forEach {
            val damageValue = mapRole.role.physicalAttack - it.role.physicalDefense
            val damage = Damage(damageValue)
            //增加一条伤害记录
            addCombatRecord(HurtRecord(it,damage))

            beHurt(it,damage)
            logE(TAG,"${mapRole.role.name}攻击了 ${it.role.name},造成${damageValue}点伤害")
        }
    }

    private fun beHurt(mapRole: MapRole,damage: Damage){
        mapRole.role.curHP = (mapRole.role.curHP-damage.value).coerceAtLeast(0)
        if (mapRole.role.curHP <= 0){
            mapRole.isAlive = false
            combatZone.removeRole(mapRole)
            //增加一条死亡记录
            addCombatRecord(DeadRecord(mapRole))
        }
    }

    private fun addCombatRecord(combatRecord: CombatRecord){
        combatRecordList.add(combatRecord)
        recordUpdateCallback.invoke(combatRecord)
    }
}