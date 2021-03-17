package com.example.autopieces.logic.role

data class Role(
    var name:String,
    val cost : Int = 1,
    var level : Int = 1,

    var curHP : Int = 100,
    var maxHP : Int = 100,

    var curMP : Int = 50,
    var maxMP : Int = 50,
    //攻击力
    var physicalAttack : Int = 50,
    //法强
    var magicValue : Int = 100,

    /**
     * 攻击时间间隔 = 攻击前摇 + 攻击后摇
     */
    //攻击前摇 单位:ms
    var beforeAttackTime : Int = 250,
    //攻击后摇 单位:ms
    var afterAttackTime : Int = 250,
    //暴击率
    var criticalRate : Int = 25,
    //暴击伤害
    var criticalDamage : Int = 150,
    //护甲
    var physicalDefense : Int = 20,
    //魔抗
    var magicDefense : Int = 20,

    /**
     * 攻击距离
     */
    var attackScope : Int = 1,

    /**
     * 可同时攻击目标的数量
     */
    var attackAmount : Int = 1,

    /**
     * 移动速度  ms/格
     */
    var moveSpeed : Int = 500
){

    fun levelUp(){
        level++
    }
}