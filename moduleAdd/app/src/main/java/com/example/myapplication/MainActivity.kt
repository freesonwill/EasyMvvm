package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var ddd=findViewById<TextView>(R.id.ceshi)


        ddd.setOnClickListener {
                var ddd= com.cn.game.sdk.utils.MyGameManager
            ddd.setToast(this)
//           var inte= Intent(this, GLShowImageActivity::class.java)
//            startActivity(inte)
//            ToastUtli().showToast("ssss",this)
        }

    }
}