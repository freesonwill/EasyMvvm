package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.cn.game.sdk2.utils.ext.CommonExt.dp2px
import com.cn.game.sdk2.utils.ext.ViewExt.isAdd
import com.cn.game.sdk2.websocket.imp.GameApp
import com.xcjh.base_lib2.utils.loge
import kotlin.random.Random


class MainActivity : AppCompatActivity(), GameApp.OnSdkListener {
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
        btnOpen = findViewById<TextView>(R.id.btnOpen)
        llshow = findViewById<RelativeLayout>(R.id.llshow)
        val btnXiu = findViewById<Button>(R.id.btnXiu)
        val cpu = findViewById<Button>(R.id.cpu)
//        GameApp.setSocketStatesCallback(object : GameApp.SocketStatesCallback {
//            override fun onOpen() {
//                socketIsOpen = true
//                btnOpen.text = "服务器连接成功,点击登录"
//                btnOpen.isClickable = true
//            }
//
//            override fun onClose(isNeedReconnect: Boolean) {
//                socketIsOpen = false
//                if (isNeedReconnect) {
//                    btnOpen.text = "正在重新连接服务器"
//                } else {
//                    btnOpen.text = "token失效,点击重新登录"
//                    btnOpen.isClickable = true
//                    isLogin = false
//                }
//            }
//        })
        var index = 0
        btnOpen.setOnClickListener {
            when (index) {
                0 -> {
                    GameApp.loadGame(applicationContext, true, url = url, this)
                    index++
                }
                1 -> {
                    GameApp.login(token, "wali-internal", false)
                    btnOpen.text = "正在登录"
                    index++
                }
                2 -> {
                    GameApp.enterLive("1213", listOf(1), "")
                }
            }

        }

        btnXiu.setOnClickListener {
            GameApp.leaveLive()
        }

        cpu.setOnClickListener {
            GameApp.enterLive("1213", listOf(1), "")
        }

    }

    override fun customerServiceAction() {

    }

    override fun historyOfBetAction() {

    }

    override fun onEnterGame() {

    }

    override fun onEnterLive(type: Int, msg: String) {
        if (type == 1) {
            btnOpen.text = "已进入直播间"
            val context = this@MainActivity
            GameApp.createFloatEnterView(context).apply {
                if (!this.isAdd()) {
                    val lp = RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height)
                    lp.topMargin = 200.dp2px
                    lp.marginEnd = 0.dp2px
                    lp.addRule(RelativeLayout.ALIGN_PARENT_END)
                    llshow.addView(this, lp)
                }
            }

            GameApp.createFloatResultView(this@MainActivity).apply {
                if (!this.isAdd()) {
                    val lp = RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height)
                    lp.topMargin = 50.dp2px
                    lp.marginEnd = 0.dp2px
                    lp.addRule(RelativeLayout.ALIGN_PARENT_END)
                    llshow.addView(this, lp)
                }
            }

        }
    }

    override fun onLeaveLive(type: Int, str: String?) {
        "onLeaveLive->$str".loge()
        btnOpen.text = "已离开房间"
    }

    override fun initSuccessful() {
        btnOpen.text = "已连接服务器，点击登录"
    }

    override fun onLoginGame(i: Int, str: String?) {
        "main->$i".loge()
        btnOpen.isClickable = true
        if (i == 1) {
            isLogin = true
            btnOpen.text = "已登录，点击进入直播间"
        } else {
            btnOpen.text = "登录失败"
        }
    }

    override fun onTokenLoseEffectiveness() {
        btnOpen.isClickable = true
        btnOpen.text = "token失效"
    }

    override fun onGameFloatingDetailViewStatus(isShowUp: Boolean) {

    }

    override fun onInsufficientBalance() {

    }


}