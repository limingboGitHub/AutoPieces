package com.example.autopieces

import android.graphics.Color
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.autopieces.adapter.StoreAdapter
import com.example.autopieces.role.Role
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val roleView = ImageView(this)
        roleView.setImageResource(R.mipmap.icon_dog)
        map_view.addView(roleView, Point(4,4))

        val adapter = StoreAdapter()
        store_rv.adapter = adapter
        adapter.setList(listOf(
            Role(0,"闪电"),
            Role(0,"疾风"),
            Role(0,"下雨"),
            Role(0,"惊雷"),
            Role(0,"落霞")
        ))
    }
}