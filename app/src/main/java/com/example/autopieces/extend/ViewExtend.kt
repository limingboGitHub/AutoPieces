package com.example.autopieces.extend

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Rect
import android.graphics.RectF
import android.view.View

fun View.transAni(targetRect: RectF, endFun: () -> Unit = {}){
    val curLeft = left
    val curTop = top
    val curWith = width
    val curHeight = height

    val animation = ValueAnimator.ofFloat(1f, 0f)
    animation.addUpdateListener {
        val scale = it.animatedValue as Float
        left = (targetRect.left + (curLeft - targetRect.left) * scale).toInt()
        top = (targetRect.top + (curTop - targetRect.top) * scale).toInt()
        right = (left + targetRect.width() + (curWith - targetRect.width()) * scale).toInt()
        bottom = (top + targetRect.height() + (curHeight - targetRect.height()) * scale).toInt()
    }
    animation.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            endFun.invoke()
        }
    })
    animation.duration = 200
    animation.start()
}

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

fun View.deadAni(endFun:()->Unit = {}){
    val alphaAni = ObjectAnimator.ofFloat(this,"alpha",1f,0f)
    alphaAni.duration = 200
    alphaAni.addListener(object : AnimatorListenerAdapter(){
        override fun onAnimationEnd(animation: Animator?) {
            endFun.invoke()
        }
    })
    alphaAni.start()
}