package com.example.autopieces.logic.combat.search

class Node(
    var x : Int = 0,
    var y : Int = 0,
    var f : Int = 0,
    //从起点A移动到指定位置T的移动代价
    var g : Int = 0,
    //从指定位置T移动到终点的估算成本
    var h : Int = 0,
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