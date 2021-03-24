package com.example.autopieces.logic.combat

import com.example.autopieces.logic.map.CombatZone
import com.example.autopieces.logic.map.GameMap
import com.example.autopieces.logic.map.MapRole
import com.example.autopieces.logic.role.Role
import com.example.autopieces.logic.role.RoleName
import org.junit.Test

class SearchPathTest {

    @Test
    fun searchPathTest(){
        val combatZone = CombatZone(GameMap.COMBAT_ROW_NUM, GameMap.COMBAT_COL_NUM)

        val teamOneRole = MapRole(
            Role(RoleName.MING_REN),
            belongTeam = 1)
        val teamOneRole2 = MapRole(
            Role(RoleName.MING_REN),
            belongTeam = 1)

        val teamTwoRole = MapRole(
            Role(RoleName.ZUO_ZU),
            belongTeam = 2)

        //两个阵营分别添加两个角色
        combatZone.addRole(teamOneRole,1,2)
        combatZone.addRole(teamOneRole2,3,3)
        combatZone.addRole(teamTwoRole,4,3)

        val startNode = SearchPathTool.Node().apply {
            x = teamOneRole.position.x
            y = teamOneRole.position.y
            g = 0
            h = distance(x,y,teamTwoRole.position.x,teamTwoRole.position.y)
            f = h
        }

        val endNode = SearchPathTool.Node().apply {
            x = teamTwoRole.position.x
            y = teamTwoRole.position.y
        }

        val listStart = ArrayList<SearchPathTool.Node>()
        val listEnd = ArrayList<SearchPathTool.Node>()

        listStart.add(startNode)

        var nowNode:SearchPathTool.Node? = null
        while (!listStart.containNode(endNode)){
            //在listStart中寻找权值最小的坐标
            nowNode = getNearestNode(listStart)
            if (nowNode==null)
                break

            val listNear = ArrayList<SearchPathTool.Node>()
            combatZone.getNearNodeList(nowNode,listNear,listStart,listEnd,endNode)

            listEnd.add(nowNode)
            listStart.removeNode(nowNode)

            listNear.forEach {
                listStart.add(it)
            }
        }

        if (nowNode == null){
            //没有找到路径
        }

        var nodeFind : SearchPathTool.Node? = null
        listStart.forEach {
            if (it.x == endNode.x && it.y == endNode.y){
                nodeFind = it
            }
        }
        if (nodeFind == null){
            //未找到
        }
    }
}