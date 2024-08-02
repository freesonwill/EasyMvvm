package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.cn.game.sdk2.utils.ext.CommonExt.dp2px
import com.cn.game.sdk2.utils.ext.ViewExt.isAdd
import com.cn.game.sdk2.websocket.imp.GameApp
import com.xcjh.base_lib2.utils.loge
import com.xcjh.base_lib2.utils.screenWidth
import kotlin.random.Random


class MainActivity : AppCompatActivity(){
    private lateinit var btnOpen: TextView
    private lateinit var llshow: RelativeLayout

    private var socketIsOpen = false
    private var isLogin = false
    private val url = "wss://ws.qxe68.com:7001/api/game/5702" ///test
    private val token = "93:Ufx3Dy8y" ///test

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GameApp.apply {
            enterLive("1213", listOf(1), "")
            val container = findViewById<FrameLayout>(android.R.id.content)
            createFloatEnterView(this@MainActivity).apply {
                if (!this.isAdd()) {
                    val lp =
                        FrameLayout.LayoutParams(layoutParams.width, layoutParams.height)
                    lp.topMargin = 150.dp2px
                    lp.marginEnd = 0.dp2px
                    container.addView(this, lp)
                }
            }
            createFloatResultView(this@MainActivity).apply {
                if (!this.isAdd()) {
                    val lp =
                        FrameLayout.LayoutParams(layoutParams.width, layoutParams.height)
                    lp.topMargin = 70.dp2px
                    lp.marginStart = context.screenWidth - layoutParams.width
                    container.addView(this, lp)
                }
            }
        }
    }

    override fun onDestroy() {
        GameApp.leaveLive()
        super.onDestroy()
    }


}