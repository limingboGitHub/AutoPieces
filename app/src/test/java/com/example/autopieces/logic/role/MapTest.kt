package com.example.autopieces.logic.role

import com.example.autopieces.Player
import com.example.autopieces.logic.map.GameMap
import com.example.autopieces.logic.map.Position
import org.junit.Test
import org.junit.Assert.*

class MapTest {

    @Test
    fun mapTest(){
        //初始化
        val player = Player()
        val gameMap = GameMap()
        gameMap.player = player

        //商店更新
        assertEquals(0,gameMap.storeZone.roleAmount())
        gameMap.updateStore(randomCreateRoles(player.level))
        assertEquals(5,gameMap.storeZone.roleAmount())

        //商店区第一个角色加入准备区
        gameMap.storeZone.getRoleByIndex(0)?.apply {
            assertEquals(Position.POSITION_STORE,this.position.where)
            var moveResult = gameMap.roleMove(this,Position(Position.POSITION_READY,2))
            assertEquals(GameMap.MoveResult.MONEY_NOT_ENOUGH,moveResult.result)
            //充点钱
            player.money = 10
            moveResult = gameMap.roleMove(this,Position(Position.POSITION_READY,2))
            assertEquals(GameMap.MoveResult.SUCCESS,moveResult.result)
            assertEquals(Position.POSITION_READY,this.position.where)
            assertEquals(0,this.position.x)
            assertEquals(4,gameMap.storeZone.roleAmount())
        }

        gameMap.storeZone.getRoleByIndex(1)?.apply {
            val moveResult = gameMap.roleMove(this, Position(Position.POSITION_READY,3))
            assertEquals(GameMap.MoveResult.SUCCESS,moveResult.result)
            assertEquals(Position.POSITION_READY,this.position.where)
            assertEquals(1,this.position.x)
            assertEquals(3,gameMap.storeZone.roleAmount())
        }

        //移动至战斗区域
        gameMap.readyZone.getRoleByIndex(0)?.apply {
            var moveResult = gameMap.roleMove(this, Position(Position.POSITION_COMBAT,0,0))
            assertEquals(GameMap.MoveResult.NO_CHANGE,moveResult.result)

            moveResult = gameMap.roleMove(this, Position(Position.POSITION_COMBAT,6,7))
            assertEquals(GameMap.MoveResult.SUCCESS,moveResult.result)
            assertEquals(Position.POSITION_COMBAT,this.position.where)
            assertEquals(6,this.position.x)
            assertEquals(7,this.position.y)
        }

        //准备区(1)的棋子和战斗区域(6,7)交换
        gameMap.readyZone.getRoleByIndex(1)?.apply {
            var moveResult = gameMap.roleMove(this, Position(Position.POSITION_COMBAT,0,0))
            assertEquals(GameMap.MoveResult.NO_CHANGE,moveResult.result)

            moveResult = gameMap.roleMove(this, Position(Position.POSITION_COMBAT,6,7))
            assertEquals(GameMap.MoveResult.SUCCESS,moveResult.result)
            assertEquals(Position.POSITION_COMBAT,this.position.where)
            assertEquals(6,this.position.x)
            assertEquals(7,this.position.y)
        }

        gameMap.readyZone.getRoleByIndex(1)?.apply {
            var moveResult = gameMap.roleMove(this, Position(Position.POSITION_STORE,0,0))
            assertEquals(GameMap.MoveResult.SUCCESS,moveResult.result)
            assert(moveResult.removeRole.isNotEmpty())

        }
    }

    @Test
    fun roleLevelUpTest(){
        //初始化
        val player = Player()
        val gameMap = GameMap()
        gameMap.player = player
        player.money = 10


        val roles = ArrayList<Role>()
        repeat(5){
            roles.add(Role(RoleName.MING_REN))
        }
        gameMap.updateStore(roles)

        gameMap.storeZone.getRoleByIndex(0)?.apply {
            var moveResult = gameMap.roleMove(this, Position(Position.POSITION_COMBAT,0,0))
        }
        gameMap.storeZone.getRoleByIndex(1)?.apply {
            var moveResult = gameMap.roleMove(this, Position(Position.POSITION_COMBAT,0,0))
        }
        gameMap.storeZone.getRoleByIndex(2)?.apply {
            var moveResult = gameMap.roleMove(this, Position(Position.POSITION_COMBAT,0,0))
        }
        gameMap.readyZone.getRoleByIndex(0)?.apply {
            assertEquals(2,this.role.level)
        }
    }
}