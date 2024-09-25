package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.utils.GsonUtils
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.utils.ext.ViewExt.isAdd
import com.cn.game.sdk2.websocket.imp.GameApp
import com.cn.game.sdk2.websocket.tokenArray
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.xcjh.base_lib2.utils.LogUtilsExt.loge
import com.xcjh.base_lib2.utils.screenWidth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Properties
import kotlin.random.Random
import kotlin.random.nextInt


class TestActivity : AppCompatActivity() {
    private lateinit var btnOpen: TextView
    private lateinit var llshow: RelativeLayout
    private var isLogin = false
    private var btnIndex = 0
    private val token = listOf(
        "138:u0GOKGuZ",//zhangsan
        "139:WpN53o8K",//xiaoyang
        "15:DbMcxyy1", //link
        "134:vdfjXERY",//jeremy
        "8:mLp8oVPC" ,//kc
        "133:RRv82JLC",//ricky
        "92:FVcRRDlj",//joseph
    ).let {
        BuildConfig.token.ifEmpty {
            it[Random.nextInt(it.size)]
        }

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        //setContentView(R.layout.activity_scrollview)
        btnOpen = findViewById(R.id.btnOpen)
        llshow = findViewById(R.id.llshow)
        val btnXiu = findViewById<Button>(R.id.btnXiu)
        val cpu = findViewById<Button>(R.id.cpu)
        ProxyApplication.instance.apply {
            onSocketConnected.observe(this@TestActivity) {
                if (it) {
                    btnOpen.text = "已连接服务器，点击登录"
                    btnIndex = 1
                } else {
                    btnOpen.text = "未连接服务器，点击连接"
                    btnIndex = 0
                }
            }
            onGameSdkEvent.observe(this@TestActivity) {
                when (it.first) {
                    "onCustomerServiceAction" -> onCustomerServiceAction()
                    "onHistoryOfBetAction" -> onHistoryOfBetAction()
                    "onEnterGame" -> onEnterGame()
                    "onEnterLive" -> onEnterLive(
                        it.second["type"] as Int,
                        it.second["msg"] as String
                    )

                    "onLeaveLive" -> onLeaveLive(
                        it.second["liveId"] as String,
                        it.second["type"] as Int,
                        it.second["msg"] as String?
                    )

                    "onLoginGame" -> onLoginGame(
                        it.second["type"] as Int,
                        it.second["msg"] as String?
                    )

                    "onTokenLoseEffectiveness" -> onTokenLoseEffectiveness()
                    "onGameFloatingDetailViewStatus" -> onGameFloatingDetailViewStatus(it.second["isShowUp"] as Boolean)
                    "onInsufficientBalance" -> onInsufficientBalance()
                    "onClickOtherGameWithBlock" -> onClickOtherGameWithBlock(it.second["json"] as String)
                }
            }
            lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    GameApp.leaveLive()
                }
            })
        }
        btnOpen.setOnClickListener {
            when (btnIndex) {
                -1 -> {
                    Toast.makeText(this, "正在连接请稍后", Toast.LENGTH_SHORT).show()
                }

                0 -> {
                    ProxyApplication.instance.loadGame()
                    btnIndex = -1
                    btnOpen.text = "正在连接请稍后"
                }

                1 -> {
                    // GameApp.login(token, "wali-internal", false)
                    GameApp.login(
                        token,
                        "wali-internal",
                        false,
                        false
                    )
                    btnOpen.text = "正在登录"
                }

                2 -> {
                    btnOpen.visibility = View.GONE
                    GameApp.enterLive("1213", listOf(1), "")
                }
            }
        }

        btnXiu.setOnClickListener {
            //GameApp.leaveLive()
        }

        cpu.setOnClickListener {
            //GameApp.enterLive("1213", listOf(1), "")
        }
        val needPermissions = mutableListOf<String>().also {
            if (applicationInfo.targetSdkVersion >= Build.VERSION_CODES.TIRAMISU) {
                it.add(Manifest.permission.READ_MEDIA_IMAGES)
                it.add(Manifest.permission.READ_MEDIA_VIDEO)
                it.add(Manifest.permission.READ_MEDIA_AUDIO)
            } else {
                it.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        val activity = this
        XXPermissions.with(this).permission(needPermissions).request(object : OnPermissionCallback {
            override fun onGranted(permissions: List<String>, all: Boolean) {

            }

            override fun onDenied(permissions: List<String>, never: Boolean) {
                Toast.makeText(activity, "请允许读取sdcard权限", Toast.LENGTH_SHORT).show()
            }
        })
        findViewById<View>(R.id.toGameMain).setOnClickListener {
            GameApp.openGameDialog(-1, false)
        }
        findViewById<View>(R.id.toGameList).setOnClickListener {
            GameApp.openGameDialog(100, true)
        }
        findViewById<View>(R.id.tvOnline).setOnClickListener {
            /*测试游戏大厅在线人数代码
           mainScope.launchWithCustomContext(tag) {
               while (true) {
                   delay(1000)
                   gameAboutModel.setMoreGameOnlines(listOf(Random.nextInt(10000)))
               }
           }*/
        }
    }

    private fun onCustomerServiceAction() {
        Toast.makeText(this, "onCustomerServiceAction", Toast.LENGTH_SHORT).show()
    }

    private fun onHistoryOfBetAction() {
        Toast.makeText(this, "onHistoryOfBetAction", Toast.LENGTH_SHORT).show()
    }

    private fun onEnterGame() {

    }

    private fun onEnterLive(type: Int, msg: String) {
        if (type == 1) {
            btnIndex = 2
            btnOpen.text = "已进入直播间"
            val context = this
            GameApp.createFloatEnterView(context).apply {
                if (!this.isAdd()) {
                    val lp = RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height)
                    lp.topMargin = 468.dp2px
                    lp.marginEnd = 0.dp2px
                    lp.addRule(RelativeLayout.ALIGN_PARENT_END)
                    llshow.addView(this, lp)
                }
            }

            GameApp.createFloatResultView(this).apply {
                if (!this.isAdd()) {
                    val lp = RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height)
                    lp.topMargin = 400.dp2px
                    lp.marginEnd = 0.dp2px
                    lp.addRule(RelativeLayout.ALIGN_PARENT_END)
                    llshow.addView(this, lp)
                }
            }
            lifecycleScope.launch {
                val items = withContext(Dispatchers.IO) {
                    mutableListOf<GameHallItem>().apply {
                        repeat(200) { id ->
                            val gameType = Random.nextInt(5) + 1
                            val weight = Random.nextInt(10)
                            val item = GameHallItem(
                                id,
                                gameType,
                                weight,
                                1,
                                when (id % 3) {
                                    0 -> com.cn.game.sdk2.R.mipmap.game_sdk_icon_kuai_logo.toString()
                                    1 -> "https://www.baidu.com/img/flexible/logo/pc/result@2.png"
                                    else -> Environment.getExternalStorageDirectory().absolutePath + "/134.jpg"
                                },
                                "快三${gameType}_${id}_$weight"
                            )
                            add(item)
                        }
                    }
                }
                GameApp.setMoreGames(GsonUtils.toJson(items));
            }
        }
    }

    private fun onLeaveLive(liveId: String, type: Int, msg: String?) {
        "onLeaveLive->$msg".loge()
        btnOpen.text = "已离开房间"
        btnIndex = 1
    }

    private fun onLoginGame(i: Int, msg: String?) {
        "main->$i".loge()
        btnOpen.isClickable = true
        if (i == 1) {
            isLogin = true
            btnOpen.text = "已登录，点击进入直播间"
            btnIndex = 2
        } else {
            btnOpen.text = "登录失败"
            btnIndex = 1
            Toast.makeText(this,"登录失败:$msg",Toast.LENGTH_SHORT).show()
        }
    }

    private fun onTokenLoseEffectiveness() {
        btnOpen.text = "token失效,点击重新登录"
        btnOpen.isClickable = true
        isLogin = false
        btnIndex = 1
    }

    private fun onGameFloatingDetailViewStatus(isShowUp: Boolean) {

    }

    private fun onInsufficientBalance() {
        Toast.makeText(this,"模拟跳转充值界面",Toast.LENGTH_SHORT).show()
    }

    private fun onClickOtherGameWithBlock(json: String) {
        Toast.makeText(this, json, Toast.LENGTH_SHORT).show()
    }

}