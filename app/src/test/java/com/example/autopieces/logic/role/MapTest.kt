package com.example.autopieces.logic.role

import com.example.autopieces.logic.Player
import com.example.autopieces.logic.map.GameMap
import com.example.autopieces.logic.map.Position
import org.junit.Test
import org.junit.Assert.*

class MapTest {

    @Test
    fun dragTest(){
        //初始化
        val player = Player()
        val gameMap = GameMap()
        gameMap.player = player

        //商店更新
        gameMap.updateStore(createRandomRoles(player.level))
        assertEquals(5,gameMap.storeZone.roleAmount())

        //商店区第一个棋子加入准备区
        gameMap.storeZone.getRoleByIndex(0)?.apply {
            assertEquals(Position.POSITION_STORE,this.position.where)
            //这次拖动应该要失败，因为没钱
            var dragResult = gameMap.dragRole(this,Position(Position.POSITION_READY,2))
            assertEquals(GameMap.DragResult.MONEY_NOT_ENOUGH,dragResult.result)
            //充点钱
            player.money = 10
            dragResult = gameMap.dragRole(this,Position(Position.POSITION_READY,2))
            assertEquals(GameMap.DragResult.SUCCESS,dragResult.result)
            assertEquals(Position.POSITION_READY,this.position.where)
            assertEquals(0,this.position.x)
            assertEquals(4,gameMap.storeZone.roleAmount())
        }

        //购买第二个棋子
        gameMap.storeZone.getRoleByIndex(1)?.apply {
            val dragResult = gameMap.dragRole(this, Position(Position.POSITION_READY,3))
            assertEquals(GameMap.DragResult.SUCCESS,dragResult.result)
            assertEquals(Position.POSITION_READY,this.position.where)
            assertEquals(1,this.position.x)
            assertEquals(3,gameMap.storeZone.roleAmount())
        }

        //将准备区[0]这里的棋子移动至战斗区域[6,7]
        gameMap.readyZone.getRoleByIndex(0)?.apply {
            var dragResult = gameMap.dragRole(this, Position(Position.POSITION_COMBAT,0,0))
            assertEquals(GameMap.DragResult.NO_CHANGE,dragResult.result)

            dragResult = gameMap.dragRole(this, Position(Position.POSITION_COMBAT,6,7))
            assertEquals(GameMap.DragResult.SUCCESS,dragResult.result)
            assertEquals(Position.POSITION_COMBAT,this.position.where)
            assertEquals(6,this.position.x)
            assertEquals(7,this.position.y)
        }

        //准备区[1]的棋子和战斗区域[6,7]交换
        gameMap.readyZone.getRoleByIndex(1)?.apply {
            var dragResult = gameMap.dragRole(this, Position(Position.POSITION_COMBAT,0,0))
            assertEquals(GameMap.DragResult.NO_CHANGE,dragResult.result)

            dragResult = gameMap.dragRole(this, Position(Position.POSITION_COMBAT,6,7))
            assertEquals(GameMap.DragResult.SUCCESS,dragResult.result)
            assertEquals(Position.POSITION_COMBAT,this.position.where)
            assertEquals(6,this.position.x)
            assertEquals(7,this.position.y)
        }

        //将准备区[1]的棋子拖入商店卖掉
        gameMap.readyZone.getRoleByIndex(1)?.apply {
            val dragResult = gameMap.dragRole(this, Position(Position.POSITION_STORE,0,0))
            assertEquals(GameMap.DragResult.SUCCESS,dragResult.result)
            assert(dragResult.removeRole.isNotEmpty())
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
            gameMap.dragRole(this, Position(Position.POSITION_COMBAT,0,0))
        }
        gameMap.storeZone.getRoleByIndex(1)?.apply {
            gameMap.dragRole(this, Position(Position.POSITION_COMBAT,0,0))
        }
        gameMap.storeZone.getRoleByIndex(2)?.apply {
            gameMap.dragRole(this, Position(Position.POSITION_COMBAT,0,0))
        }
        gameMap.readyZone.getRoleByIndex(0)?.apply {
            assertEquals(2,this.role.level)
        }
    }
}