package com.example.autopieces.logic.combat

import com.example.autopieces.logic.map.CombatZone
import kotlin.math.abs

class SearchPathTool {



    class Node(
        var x : Int = 0,
        var y : Int = 0,
        var f : Int = 0,
        var g : Int = 0,
        var h : Int = 0,
        var parent : Node? = null
    ){
        override fun equals(other: Any?): Boolean {
            if (other!=null && other is Node){
                if (this.x == other.x && this.y == other.y)
                    return true
            }
            return false
        }
    }
}

fun CombatZone.getNearNodeList(
    pNode: SearchPathTool.Node,
    listNear:ArrayList<SearchPathTool.Node>,
    listStart:List<SearchPathTool.Node>,
    listEnd:List<SearchPathTool.Node>,
    endNode: SearchPathTool.Node
){
    for (i in -1..1){
        for (j in -1..1){
            if (i == 0 && j==0)
                continue
            val tempX = pNode.x + i
            val tempY = pNode.y + j
            //越界的坐标
            if (tempX<0 || tempX>col || tempY<0 || tempY>row)
                continue
            //有障碍
            if (getRoleByIndex(tempX,tempY)!=null)
                continue

            val node = SearchPathTool.Node().apply {
                x = tempX
                y = tempY
            }
            if (listStart.contains(node))
                continue
            if (listEnd.contains(node))
                continue

            SearchPathTool.Node().apply {
                g = pNode.g + distance(pNode.x,pNode.y,tempX,tempY)
                h = distance(tempX,tempY,endNode.x,endNode.y)
                f = g + h
                x = tempX
                y = tempY
                parent = pNode
                listNear.add(this)
            }
        }
    }
}

fun ArrayList<SearchPathTool.Node>.removeNode(nodeToRemove: SearchPathTool.Node){
    for (node in this){
        if (node.x == nodeToRemove.x && node.y == nodeToRemove.y){
            this.remove(node)
            break
        }
    }
}

fun List<SearchPathTool.Node>.containNode(node:SearchPathTool.Node):Boolean{
    forEach {
        if (it.x == node.x && it.y == node.y)
            return true
    }
    return false
}

fun getNearestNode(nodeList:List<SearchPathTool.Node>):SearchPathTool.Node?{
    if (nodeList.isEmpty())
        return null
    var tempNode : SearchPathTool.Node = nodeList[0]
    var tempF = tempNode.f
    nodeList.forEach {
        if (it.f<tempF){
            tempNode = it
            tempF = it.f
        }
    }
    return tempNode
}

fun distance(x:Int,y:Int,x2:Int,y2:Int):Int{
    return abs(x-x2) + abs(y-y2)
}

