package com.example.autopieces.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.customview.widget.ViewDragHelper
import com.example.autopieces.R
import com.example.autopieces.role.Role
import com.example.autopieces.utils.*
import com.lmb.lmbkit.utils.getDensity
import com.lmb.lmbkit.utils.getScreenHeight
import com.lmb.lmbkit.utils.getScreenWidth
import java.lang.RuntimeException

class MapView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    private val TAG = "MapView"

    private val mapDraw = MapDraw()
    /**
     * 系统参数
     */
    private val screenWidth = getScreenWidth()
    private val screenHeight = getScreenHeight()
    private val density = getDensity()

    /**
     * 地图相关参数
     */
    private var roleCoordinates = HashMap<View,Rect>()

    /**
     * 商店角色
     */
    private val storeRoles = ArrayList<Role>()

    /**
     * 子View拖动
     */
    private val dragCallback = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(view: View, i: Int): Boolean {
            return true
        }

        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
            capturedChild.elevation = 5*density
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            //归位
            val curLeft = releasedChild.left
            val curTop = releasedChild.top
            val childWith = releasedChild.width
            val childHeight = releasedChild.height

            roleCoordinates[releasedChild]?.apply {
                val targetLeft = left
                val targetTop = top

                val animation = ValueAnimator.ofFloat(1f,0f)
                animation.addUpdateListener {
                    val scale = it.animatedValue as Float
                    releasedChild.left = (targetLeft + (curLeft - targetLeft)*scale).toInt()
                    releasedChild.top = (targetTop + (curTop - targetTop)*scale).toInt()
                    releasedChild.right = releasedChild.left + childWith
                    releasedChild.bottom = releasedChild.top + childHeight
                }
                animation.addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                        releasedChild.elevation = 3*density
                    }
                })
                animation.duration = 200
                animation.start()
            }
            logE(TAG,"x:$curLeft y:$curTop")

        }

        override fun getViewHorizontalDragRange(view: View): Int {
            return view.width
        }

        override fun getViewVerticalDragRange(view: View): Int {
            return view.height
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return left
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top
        }

    }
    private val viewDragHelper = ViewDragHelper.create(this,dragCallback)

    init {
        setWillNotDraw(false)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (index in 0 until childCount){
            val childView = getChildAt(index)
            roleCoordinates[childView]?.apply {
                childView.layout(left,top,right,bottom)
            }

            logD(TAG,"onLayout $childView to:${childView.left}")
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制地图
        mapDraw.drawMap(canvas)
        //绘制棋子准备区域
        mapDraw.drawReadyZone(canvas)
        //绘制商店区域
        mapDraw.drawStore(canvas)
    }



    fun addStore(role: Role){
        val storeItemWidth = mapDraw.getStoreItemWidth()

        val roleView = LayoutInflater.from(context).inflate(R.layout.item_store,this,false)
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        layoutParams.width = storeItemWidth
        layoutParams.height = storeItemWidth
        roleView.layoutParams = layoutParams

        addView(roleView)
        //计算每个卡片对应在商店区域的坐标
        val x = mapDraw.getStoreZone().left + mapDraw.getStoreCellWith() * storeRoles.size
        val y = mapDraw.getStoreZone().top

        roleCoordinates[roleView] = Rect(
                x.toInt(), y.toInt(), (x+storeItemWidth).toInt(), (y+storeItemWidth).toInt()
        )

        storeRoles.add(role)
    }

    fun addView(view:View,point:Point){
//        if (point.x>=cellCols)
//            throw RuntimeException("x must < $cellCols")
//        if (point.y>=cellRows)
//            throw RuntimeException("y must < $cellRows")
        val cellWidth = mapDraw.getMapCellWidth()
        roleCoordinates[view] = Rect(
                point.x*cellWidth, point.y*cellWidth,
                (point.x+1)*cellWidth, (point.y+1)*cellWidth
        )
        addView(view)
        logD(TAG,"add $view to:${point.x},${point.y}")
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return viewDragHelper.shouldInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewDragHelper.processTouchEvent(event)
//        return super.onTouchEvent(event)
        return true
    }



}