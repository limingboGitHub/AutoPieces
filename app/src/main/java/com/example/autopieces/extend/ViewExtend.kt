package com.example.autopieces.extend

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View

fun View.attackAni(targetView:View?,endFun:()->Unit = {}){
    targetView?:return

    val targetCenterX = targetView.right - (targetView.right - targetView.left)/2
    val targetCenterY = targetView.bottom - (targetView.bottom - targetView.top)/2

    val startCenterX = right -(right - left)/2
    val startCenterY = bottom -(bottom - top)/2

    val transAni = ValueAnimator.ofFloat(0f,1f,0f)
    transAni.duration = 500
    transAni.addUpdateListener {
        val scale = it.animatedValue as Float
        translationX = (targetCenterX - startCenterX) * scale * 0.2f
        translationY = (targetCenterY - startCenterY) * scale * 0.2f
    }
    transAni.addListener(object : AnimatorListenerAdapter(){
        override fun onAnimationEnd(animation: Animator?) {
            endFun.invoke()
        }
    })
    transAni.start()
}