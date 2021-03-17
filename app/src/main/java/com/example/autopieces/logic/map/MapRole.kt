package com.example.autopieces.logic.map

import com.example.autopieces.logic.combat.Damage
import com.example.autopieces.logic.role.Role
import com.example.autopieces.utils.logE

/**
 * 地图中一个角色
 * 包含
 * @param role 角色属性
 * @param position 角色的位置信息
 */
open class MapRole(
    var role:Role,
    var position:Position = Position(),
    var equipment:ArrayList<Role> = ArrayList(),

    /**
     * 所属阵营
     */
    var belongTeam:Int = 1,

    /**
     * 状态
     * 0    静止
     * 1    前摇中
     * 2    后摇中
     */
    var state : Int = STATE_IDLE,

    /**
     * 状态持续剩余时间
     */
    var stateRestTime : Int = 0,

    /**
     * 攻击目标
     */
    var attackRoles : ArrayList<MapRole> = ArrayList(),

    /**
     * 移动目标位置
     */
    var moveTarget : Pair<Int,Int>? = null,

    /**
     * 存活状态
     */
    var isAlive : Boolean = true
){

    companion object{
        /**
         * 状态 静止
         */
        const val STATE_IDLE = 0

        /**
         * 状态 攻击前摇
         */
        const val STATE_BEFORE_ATTACK = 1

        /**
         * 状态 攻击后摇
         */
        const val STATE_AFTER_ATTACK = 2

        /**
         * 状态 移动
         */
        const val STATE_MOVING = 3
    }
    val TAG = "MapRole"

    fun hurtRole(){
        attackRoles.forEach {
            val damageValue = role.physicalAttack - it.role.physicalDefense
            it.beHurt(Damage(damageValue))
            logE(TAG,"${role.name}攻击了 ${it.role.name},造成${damageValue}点伤害")
        }
    }

    fun beHurt(damage: Damage){
        role.curHP = (role.curHP-damage.value).coerceAtLeast(0)
        if (role.curHP <= 0){
            isAlive = false
        }
    }

    fun changeState(attackState:Int){
        this.state = attackState
        when(attackState){
            STATE_BEFORE_ATTACK -> {
                this.stateRestTime = role.beforeAttackTime
            }
            STATE_AFTER_ATTACK -> {
                this.stateRestTime = role.afterAttackTime
            }
            STATE_MOVING ->{
                this.stateRestTime = role.moveSpeed
            }
        }
    }
}