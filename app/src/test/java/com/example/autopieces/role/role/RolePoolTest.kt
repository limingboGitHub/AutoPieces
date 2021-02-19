package com.example.autopieces.role.role

import com.example.autopieces.Player
import com.example.autopieces.role.RolePool
import com.example.autopieces.role.randomCreateRoles
import org.junit.Test
import org.junit.Assert.*

class RolePoolTest {

    @Test
    fun rolePoolTest(){
        RolePool.init()

        val player = Player()

        val roles = randomCreateRoles(player.level)
        assertEquals(roles.size,4)
    }
}