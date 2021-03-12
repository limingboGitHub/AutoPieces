package com.example.autopieces.logic.combat

/**
 * 伤害类
 */
class Damage(
    val value:Int,
    val damageType:Int = DAMAGE_TYPE_PHY,
    val isCritical:Boolean = false
) {

    companion object{
        const val DAMAGE_TYPE_PHY = 0
        const val DAMAGE_TYPE_MAGIC = 1
        const val DAMAGE_TYPE_REAL = 2
    }
}