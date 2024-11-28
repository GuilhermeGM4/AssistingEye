package com.assistingeye.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.assistingeye.R
import com.assistingeye.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}