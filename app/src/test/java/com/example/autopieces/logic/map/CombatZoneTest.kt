package com.example.autopieces.logic.map

import org.junit.Test
import org.junit.Assert.*

class CombatZoneTest {

    @Test
    fun calculateAttackScope(){

        val result1 = calculateAttackScopeAll(1)
        assertEquals(4,result1.size)

        val result2 = calculateAttackScopeAll(2)
        assertEquals(12,result2.size)

        val result3 = calculateAttackScopeAll(3)
        assertEquals(24,result3.size)

        val result4 = calculateAttackScopeAll(4)
        assertEquals(40,result4.size)
    }
}