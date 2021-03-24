package com.example.autopieces.logic.combat.search

class Node(
    var x : Int = 0,
    var y : Int = 0,
    var f : Float = 0f,
    //从起点A移动到指定位置T的移动代价
    var g : Float = 0f,
    //从指定位置T移动到终点的估算成本
    var h : Float = 0f,
    var parent : Node? = null
) {
    override fun equals(other: Any?): Boolean {
        if (other!=null && other is Node){
            if (this.x == other.x && this.y == other.y)
                return true
        }
        return false
    }
}