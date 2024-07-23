package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.cn.game.sdk2.BuildConfig
import com.cn.game.sdk2.utils.ext.CommonExt.dp2px
import com.cn.game.sdk2.utils.ext.ViewExt.isAdd
import com.cn.game.sdk2.websocket.imp.GameApp
import com.cn.game.sdk2.websocket.token


class MainActivity : AppCompatActivity(), GameApp.OnSdkListener {
    private lateinit var btnOpen: TextView
    private lateinit var llshow: RelativeLayout

    private var socketIsOpen = false
    private var isLogin = false
    private val url = "wss://ws.qxe68.com:7001/api/game/5702" ///test

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnOpen = findViewById(R.id.btnOpen)
        llshow = findViewById(R.id.llshow)
        val btnXiu = findViewById<Button>(R.id.btnXiu)
        findViewById<Button>(R.id.btnSetBetResult).setOnClickListener {
//            gameAboutModel.manualLeopard = true
        }

        GameApp.setSocketStatesCallback(object : GameApp.SocketStatesCallback {
            override fun onOpen() {
                socketIsOpen = true
                btnOpen.post {
                    btnOpen.text = "服务器连接成功,点击登录"
                    btnOpen.isClickable = true
                }
            }

            override fun onClose(isNeedReconnect: Boolean) {
                socketIsOpen = false
                btnOpen.post {
                    if (isNeedReconnect) {
                        btnOpen.text = "正在重新连接服务器"
                    } else {
                        btnOpen.text = "token失效,点击重新登录"
                        btnOpen.isClickable = true
                        isLogin = false
                    }
                }
            }
        })

        btnOpen.setOnClickListener {
            btnOpen.isClickable = false
            if (socketIsOpen) {
                if (!isLogin) {
                    GameApp.login(
                        token, "wali-internal", false
                    )
                    btnOpen.text = "正在登录"
                } else {
                    GameApp.enterLive("1213", listOf(1), "")
                }
            } else {
                GameApp.loadGame(applicationContext, true, url = url, this)
            }
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

            GameApp.createFloatEnterView(this@MainActivity).apply {
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

    }

    override fun onLoginGame(i: Int, str: String?) {
        btnOpen.isClickable = true
        if (i == 1) {
            isLogin = true
            btnOpen.text = "进入直播间"
        } else {
            btnOpen.text = "登录失败"
        }
        TextView(this).apply {
            val lp = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            lp.topMargin = 200.dp2px
            lp.marginEnd = 0.dp2px
            setTextColor(ContextCompat.getColor(context, R.color.white))
            background = ContextCompat.getDrawable(context, com.cn.game.sdk2.R.color.blue_ed)
            setPadding(10.dp2px)
            lp.addRule(RelativeLayout.ALIGN_PARENT_START)
            llshow.addView(this, lp)
            text = "token:$token"
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