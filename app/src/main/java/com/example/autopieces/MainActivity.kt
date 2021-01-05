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
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val roleView = ImageView(this)
        roleView.setImageResource(R.mipmap.icon_dog)
        map_view.addView(roleView, Point(4,4))

        val card = layoutInflater.inflate(R.layout.item_store,null)
        map_view.addView(card, Point(5,5))
    }

}