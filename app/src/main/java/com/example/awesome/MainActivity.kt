package com.example.awesome

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this).apply {
            text = "Hello, AI App Builder Pro!"
            textSize = 24f
            gravity = android.view.Gravity.CENTER
        }
        setContentView(textView)
    }
}