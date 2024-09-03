package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.cn.game.sdk2.data.EventKey
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.ui.helper.Fast3ToastHelper
import com.cn.game.sdk2.ui.helper.ViewHelper
import com.cn.game.sdk2.utils.FlowBus
import com.cn.game.sdk2.utils.GsonUtils
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.utils.ext.ViewExt.isAdd
import com.cn.game.sdk2.websocket.imp.GameApp
import com.cn.game.sdk2.websocket.tokenArray
import com.xcjh.base_lib2.utils.LogUtilsExt.loge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random
import kotlin.random.nextInt


class TestActivity : AppCompatActivity(), GameApp.OnSdkListener {
    private lateinit var btnOpen: TextView
    private lateinit var llshow: RelativeLayout

    private var isLogin = false
    private val url = "wss://ws.qxe68.com:7001/api/game/5702" ///test
    //private val url = "ws://35.220.148.132:7642" ///联调
    private val token = "93:Ufx3Dy8y" ///test

    var btnIndex = 0;

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnOpen = findViewById(R.id.btnOpen)
        llshow = findViewById(R.id.llshow)
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
        var isLoadGame = false
        FlowBus.with<Boolean>(EventKey.SOCKET_CONNECTED).register(this) {
            btnOpen.text = "已连接服务器，点击登录"
            btnIndex = 1
        }
        btnOpen.setOnClickListener {
            when (btnIndex) {
                0 -> {
                    if(!isLoadGame) {
                        isLoadGame = true
                        GameApp.loadGame(applicationContext,true,  url, "", this).apply {
                            lifecycle.addObserver(object : DefaultLifecycleObserver {
                                override fun onDestroy(owner: LifecycleOwner) {
                                    super.onDestroy(owner)
                                    // GameApp.x()
                                }
                            })
                        }
                    }
                }

                1 -> {
                   // GameApp.login(token, "wali-internal", false)
                    GameApp.login(tokenArray[Random.nextInt(tokenArray.size)], "wali-internal", false,false)
                    btnOpen.text = "正在登录"
                }

                2 -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }

        }

        btnXiu.setOnClickListener {
            //GameApp.leaveLive()
        }

        cpu.setOnClickListener {
            //GameApp.enterLive("1213", listOf(1), "")
        }

    }

    override fun onCustomerServiceAction() {
        Fast3ToastHelper.showToastNormal("onCustomerServiceAction")
    }

    override fun onHistoryOfBetAction() {
        Fast3ToastHelper.showToastNormal("onHistoryOfBetAction")
    }

    override fun onEnterGame() {

    }

    override fun onEnterLive(type: Int, msg: String) {
        if (type == 1) {
            btnIndex = 2
            btnOpen.text = "已进入直播间"
            val context = this
            GameApp.createFloatEnterView(context)?.apply {
                if (!this.isAdd()) {
                    val lp = RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height)
                    lp.topMargin = 200.dp2px
                    lp.marginEnd = 0.dp2px
                    lp.addRule(RelativeLayout.ALIGN_PARENT_END)
                    llshow.addView(this, lp)
                }
            }

            GameApp.createFloatResultView(this).apply {
                if (!this.isAdd()) {
                    val lp = RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height)
                    lp.topMargin = 50.dp2px
                    lp.marginEnd = 0.dp2px
                    lp.addRule(RelativeLayout.ALIGN_PARENT_END)
                    llshow.addView(this, lp)
                }
            }
            lifecycleScope.launch {
                val items = withContext(Dispatchers.IO){
                    mutableListOf<GameHallItem>().apply {
                        repeat(200) { id ->
                            val gameType = Random.nextInt(6)
                            val weight = Random.nextInt(10)
                            val item = GameHallItem(
                                id,
                                gameType,
                                weight,
                                1,
                                if(id % 2 == 0) R.drawable.game_sdk_kuai_icon_logo.toString() else "https://www.baidu.com/img/flexible/logo/pc/result@2.png",
                                "快三${id}_$weight"
                            )
                            add(item)
                        }
                    }
                }
                GameApp.setMoreGames(GsonUtils.toJson(items));
            }
        }
    }

    override fun onLeaveLive(liveId: String, type: Int, msg: String?) {
        "onLeaveLive->$msg".loge()
        btnOpen.text = "已离开房间"
    }

    override fun onLoginGame(i: Int, str: String?) {
        "main->$i".loge()
        btnOpen.isClickable = true
        if (i == 1) {
            isLogin = true
            btnOpen.text = "已登录，点击进入直播间"
            btnIndex = 2
        } else {
            btnOpen.text = "登录失败"
            btnIndex = 1
        }
    }

    override fun onTokenLoseEffectiveness() {
        btnOpen.text = "token失效,点击重新登录"
        btnOpen.isClickable = true
        isLogin = false
        btnIndex = 1
    }

    override fun onGameFloatingDetailViewStatus(isShowUp: Boolean) {

    }

    override fun onInsufficientBalance() {

    }

    override fun onClickOtherGameWithBlock(json: String) {
        Toast.makeText(this,json,Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        GameApp.leaveLive()
        ViewHelper.instance.clearAllView()
        super.onDestroy()
    }
}