package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.cn.game.sdk.ToastUtli
import com.example.myapplication.ui.CeShiActivity
import com.example.myapplication.ui.GLShowImageActivity
import com.example.myapplication.util.MyJniClass

class MainActivity : Activity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var ddd=findViewById<TextView>(R.id.ceshi)


        ddd.setOnClickListener {

           var inte= Intent(this, GLShowImageActivity::class.java)
            startActivity(inte)
//            ToastUtli().showToast("ssss",this)
        }

    }
}