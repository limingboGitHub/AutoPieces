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
class MapRole(
    var role:Role,
    var position:Position = Position(),
    var equipment:ArrayList<Role> = ArrayList(),

    /**
     * 所属阵营
     */
    var belongTeam:Int = 1,

    /**
     * 行动状态
     */
    var state : Int = STATE_IDLE,

    /**
     * 行动状态持续剩余时间
     */
    var stateRestTime : Int = 0,

    /**
     * 攻击目标
     */
    var beAttackedRoles : ArrayList<MapRole> = ArrayList(),

    /**
     * 移动终点位置
     */
    var moveTarget : Pair<Int,Int>? = null,

    /**
     * 移动路径
     */
    var movePath : ArrayList<Pair<Int,Int>> = ArrayList(),

    /**
     * 存活状态
     */
    var isAlive : Boolean = true,

    /**
     * 标识
     */
    var flag : Int = FLAG_ROLE
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

        /**
         * 标识 普通角色，棋子
         */
        const val FLAG_ROLE = 0

        /**
         * 标识 角色移动占位符
         */
        const val FLAG_MOVE_PLACEHOLDER = 1
    }

    val TAG = "MapRole"


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