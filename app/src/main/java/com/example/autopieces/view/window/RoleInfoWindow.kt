package com.example.autopieces.view.window

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.example.autopieces.R
import com.example.autopieces.databinding.WindowRoleInfoBinding
import com.example.autopieces.logic.role.Role
import com.lmb.lmbkit.utils.getDensity

class RoleInfoWindow(val attachView:ViewGroup) {

    val binding = WindowRoleInfoBinding.inflate(LayoutInflater.from(attachView.context))

    val popupWindow = PopupWindow(binding.root,
        attachView.context.resources.getDimensionPixelOffset(R.dimen.window_role_info_width),
        ViewGroup.LayoutParams.WRAP_CONTENT)

    init {
        popupWindow.apply {
            isFocusable = true
            isOutsideTouchable = true
            elevation = 10 * getDensity()
            animationStyle = R.style.WindowAniStyle
        }
    }

    fun show(role: Role){
        binding.role = role

        popupWindow.showAtLocation(attachView,Gravity.CENTER,0,0)
    }
}