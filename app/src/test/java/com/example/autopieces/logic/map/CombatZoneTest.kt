package com.example.autopieces.logic.map

import org.junit.Test
import org.junit.Assert.*

class CombatZoneTest {

    @Test
    fun calculateAttackScope(){

        val result1 = calculateAttackScope(1)
        assertEquals(4,result1.size)

        val result2 = calculateAttackScope(2)
        assertEquals(12,result2.size)

        val result3 = calculateAttackScope(3)
        assertEquals(24,result3.size)

        val result4 = calculateAttackScope(4)
        assertEquals(40,result4.size)
    }
}