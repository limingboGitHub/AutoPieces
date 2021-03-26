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
    var combatTime : Long = 30 * 1000

    /**
     * 加时时长
     */
    var extraTime : Long = 10 * 1000

    /**
     * 上次行动的时间
     */
    var lastActionTime = 0L

    /**
     * 战斗记录
     * 每个事件都会被记录下来，供UI层使用
     * 如果我们完全不考虑UI层的话，这个变量是不需要的
     * 但是这个战斗记录机制会很方便UI层展示
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
        while (combatTime > 0) {
            lastActionTime = System.currentTimeMillis()

            action(actionTime.toInt())

            actionTime = System.currentTimeMillis() - lastActionTime
            combatTime = (combatTime - actionTime).coerceAtLeast(0)

            if (combatZone.isCombatEnd())
                break
        }
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
            //棋子当前状态的剩余时间消耗
            it.stateRestTime = (it.stateRestTime-time).coerceAtLeast(0)
            //棋子当前状态
            when(it.state){
                MapRole.STATE_IDLE -> {
                    //寻找攻击目标
                    when(combatZone.findRoleToAttack(it)){
                        FindRoleTool.RESULT_SUCCESS -> {
                            //进入前摇状态
                            it.changeState(MapRole.STATE_BEFORE_ATTACK)
                        }
                        FindRoleTool.RESULT_FAILED -> {
                            val nextMovePoint = combatZone.findClosetTarget(it)?.apply {
                                combatZone.addRole(createMovePlaceholder(it), first, second)
                                it.moveTarget = this
                                it.changeState(MapRole.STATE_MOVING)
                            }
                        }
                        FindRoleTool.RESULT_WAIT -> {/*等待，什么也不做*/}
                    }
                }
                MapRole.STATE_BEFORE_ATTACK->{
                    //如果攻击前摇结束，则造成伤害
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
                    //攻击后摇结束
                    if (it.stateRestTime<=0)
                        it.changeState(MapRole.STATE_IDLE)
                }
                MapRole.STATE_MOVING ->{
                    //移动时间结束，移动到目标位置
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