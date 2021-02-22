package com.example.autopieces

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lmb.lmbkit.extend.transNavAndStatus

open class BaseActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transNavAndStatus()
    }
}