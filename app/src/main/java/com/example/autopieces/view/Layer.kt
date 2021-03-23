package com.example.autopieces.view

import com.lmb.lmbkit.utils.getDensity

/**
 * 层级
 * 管理各个元素显示的先后顺序
 */
class Layer {

    companion object{
        val density = getDensity()

        val DAMAGE = 5 * density
    }
}