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
    //攻击速度 次/ms
    var attackSpeed : Int = 500,
    //暴击率
    var criticalRate : Int = 25,
    //暴击伤害
    var criticalDamage : Int = 150,
    //护甲
    var physicalDefense : Int = 20,
    //魔抗
    var magicDefense : Int = 20
){

    fun levelUp(){
        level++
    }
}