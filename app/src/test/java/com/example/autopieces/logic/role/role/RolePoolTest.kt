package com.example.autopieces.logic.role.role

import com.example.autopieces.logic.Player
import com.example.autopieces.logic.role.RolePool
import com.example.autopieces.logic.role.createRandomRoles
import org.junit.Test
import org.junit.Assert.*

class RolePoolTest {

    @Test
    fun rolePoolTest(){
        RolePool.init()

        val player = Player()

        val roles = createRandomRoles(player.level)
        assertEquals(roles.size,5)
        //检查卡池的数量是否正确减少
        roles.forEach {
            var roleAmount = 0
            for (role in roles) {
                if (role.name == it.name)
                    roleAmount++
            }
            assertEquals(29-roleAmount,RolePool.getRoleAmount(it.name))
        }
    }
}