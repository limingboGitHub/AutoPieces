package com.example.autopieces.logic.combat.search

import com.example.autopieces.logic.map.CombatZone
import com.example.autopieces.logic.map.Position
import kotlin.math.abs
import kotlin.math.sqrt

fun CombatZone.getMovePath(startPosition:Position,endPosition: Position):List<Pair<Int,Int>>{
    val movePath = ArrayList<Pair<Int,Int>>()
    //
    val endNode = Node().apply {
        x = endPosition.x
        y = endPosition.y
    }

    val startNode = Node().apply {
        x = startPosition.x
        y = startPosition.y
        g = 0
        h = distance(x,y,endNode.x,endNode.y)
        f = g + h
    }

    val listStart = ArrayList<Node>()
    val listEnd = ArrayList<Node>()

    listStart.add(startNode)

    var nowNode:Node? = null
    while (!listStart.containNode(endNode)){
        //在listStart中寻找权值最小的坐标
        nowNode = getNearestNode(listStart)
        if (nowNode==null)
            break

        val listNear = getNearNodeList(nowNode,listStart,listEnd,endNode)

        listEnd.add(nowNode)
        listStart.removeNode(nowNode)

        listNear.forEach {
            listStart.add(it)
        }
    }

    //没有找到路径
    if (nowNode == null)
        return movePath

    var nodeFind : Node? = null
    listStart.forEach {
        if (it.x == endNode.x && it.y == endNode.y){
            nodeFind = it
        }
    }
    if (nodeFind != null){
        while (nodeFind!!.parent!=null){
            movePath.add(Pair(nodeFind!!.parent!!.x,nodeFind!!.parent!!.y))
            nodeFind = nodeFind!!.parent
        }
        if (movePath.isNotEmpty())
            movePath.removeAt(movePath.size-1)
    }
    return movePath
}

fun CombatZone.getNearNodeList(
    pNode: Node,
    listStart:List<Node>,
    listEnd:List<Node>,
    endNode: Node
):List<Node>{
    val listNear = ArrayList<Node>()
    val offsetList = listOf(
        Pair(0,-1),
        Pair(0,1),
        Pair(-1,0),
        Pair(1,0)
    )
    for (offset in offsetList){
        val i = offset.first
        val j = offset.second

        val tempX = pNode.x + i
        val tempY = pNode.y + j
        //越界的坐标
        if (tempX<0 || tempX>col || tempY<0 || tempY>row)
            continue
        //有障碍
        if (getRoleByIndex(tempX,tempY)!=null){
            if (tempX!=endNode.x || tempY!=endNode.y)
                continue
        }

        val node = Node().apply {
            x = tempX
            y = tempY
        }
        if (listStart.contains(node))
            continue
        if (listEnd.contains(node))
            continue

        Node().apply {
            g = pNode.g + distance(pNode.x,pNode.y,tempX,tempY)
            h = distance(tempX,tempY,endNode.x,endNode.y)
            f = g + h
            x = tempX
            y = tempY
            parent = pNode
            listNear.add(this)
        }
    }

    return listNear
}

fun ArrayList<Node>.removeNode(nodeToRemove: Node){
    for (node in this){
        if (node.x == nodeToRemove.x && node.y == nodeToRemove.y){
            this.remove(node)
            break
        }
    }
}

fun List<Node>.containNode(node:Node):Boolean{
    forEach {
        if (it.x == node.x && it.y == node.y)
            return true
    }
    return false
}

fun getNearestNode(nodeList:List<Node>):Node?{
    if (nodeList.isEmpty())
        return null
    var tempNode : Node = nodeList[0]
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
    return (sqrt((x-x2)*(x-x2).toFloat() + (y-y2)*(y-y2))*10).toInt()
//    return abs(x-x2)+ abs(y-y2).toFloat()
}


