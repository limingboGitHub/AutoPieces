package com.example.autopieces.logic.map

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
    var attackState : Int = 0,

    /**
     * 攻击状态剩余时间
     */
    var attackStateRestTime : Int = 0
){

}