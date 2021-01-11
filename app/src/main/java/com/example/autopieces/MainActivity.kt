package com.example.autopieces

import android.graphics.Color
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.customview.widget.ViewDragHelper
import com.example.autopieces.adapter.StoreAdapter
import com.example.autopieces.role.Role
import com.google.android.material.card.MaterialCardView
import com.lmb.lmbkit.extend.useNotchTransStatus
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        useNotchTransStatus()

        //商店进货 5个角色
        repeat(5){
            val role = Role("鸣人")
            map_view.addStore(role)
        }
    }

    private fun createCard(){

    }
}