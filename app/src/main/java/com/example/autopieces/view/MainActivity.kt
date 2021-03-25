package com.example.autopieces.view

import android.content.Intent
import android.os.Bundle
import android.text.method.MovementMethod
import com.example.autopieces.cpp.MoveMethod
import com.example.autopieces.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setContentView(binding.root)

        val result = MoveMethod.calculateMovePath(0,0,0,0)

        binding.startBt.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
//            finish()
        }

    }
}