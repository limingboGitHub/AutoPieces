package com.example.autopieces.logic.map

import com.example.autopieces.logic.combat.Damage
import com.example.autopieces.logic.role.Role

/**
 * 地图中一个角色
 * 包含
 * @param role 角色属性
 * @param position 角色的位置信息
 */
class MapRole(
    var role:Role,
    var position:Position = Position(),
    var equipment:ArrayList<Role> = ArrayList(),

    /**
     * 所属阵营
     */
    var belongTeam:Int = 0,

    /**
     * 攻击状态
     * 0    静止
     * 1    前摇中
     * 2    后摇中
     */
    var attackState : Int = ATTACK_STATE_IDLE,

    /**
     * 攻击状态剩余时间
     */
    var attackStateRestTime : Int = 0,

    /**
     * 攻击目标
     */
    var attackRoles : ArrayList<MapRole> = ArrayList(),

    /**
     * 存活状态
     */
    var isAlive : Boolean = true
){

    companion object{
        /**
         * 攻击状态 静止
         */
        const val ATTACK_STATE_IDLE = 0

        /**
         * 攻击状态 前摇
         */
        const val ATTACK_STATE_BEFORE = 1

        /**
         * 攻击状态 后摇
         */
        const val ATTACK_STATE_AFTER = 2
    }

    fun hurtRole(){
        attackRoles.forEach {
            val damageValue = role.physicalAttack - it.role.physicalDefense
            it.beHurt(Damage(damageValue))
        }
    }

    fun beHurt(damage: Damage){
        role.curHP = (role.curHP-damage.value).coerceAtLeast(0)
        if (role.curHP <= 0){
            isAlive = false
        }
    }

    fun changeAttackState(attackState:Int){
        this.attackState = attackState
        when(attackState){
            ATTACK_STATE_BEFORE -> {
                this.attackStateRestTime = role.beforeAttackTime
            }
            ATTACK_STATE_AFTER -> {
                this.attackStateRestTime = role.afterAttackTime
            }
        }
    }
}