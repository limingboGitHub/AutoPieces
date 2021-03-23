package com.example.autopieces.extend

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.RectF
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import com.example.autopieces.logic.combat.Damage
import com.example.autopieces.view.Layer

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

/**
 * 伤害动画
 */
fun View.damageAni(rootViewGroup:ViewGroup,damage: Damage){
    val textView = TextView(rootViewGroup.context).apply {
        text = damage.value.toString()
        textSize = 12f
        setTextColor(Color.RED)
        elevation = Layer.DAMAGE
    }

    textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
    rootViewGroup.addView(textView)
    //让textView测量好宽高
    textView.measure(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)

    val startX = this.left + (this.width-textView.measuredWidth)/2
    val startY = this.top - textView.measuredHeight
    textView.translationX = startX.toFloat()
    textView.translationY = startY.toFloat()

    val animator = ValueAnimator.ofFloat(0f,1f)
    animator.duration = 600
    animator.addUpdateListener {
        val scale = it.animatedValue as Float
        textView.translationX = startX + scale * 100
        textView.translationY = startY - scale * 70
    }
    animator.addListener(object : AnimatorListenerAdapter(){
        override fun onAnimationEnd(animation: Animator?) {
            rootViewGroup.removeView(textView)
        }
    })
    animator.interpolator = AccelerateInterpolator()
    animator.start()
}