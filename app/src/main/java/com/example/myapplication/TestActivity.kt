package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        findViewById<TextView>(R.id.btnOpen).apply {
            setOnClickListener {
                startActivity(Intent(this@TestActivity, MainActivity::class.java).apply {
                })
            }
        }
    }
}