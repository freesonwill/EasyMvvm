package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.cn.game.sdk2.BuildConfig
import com.cn.game.sdk2.ui.view.FastLogoView
import com.cn.game.sdk2.utils.ext.CommonExt.dp2px
import com.cn.game.sdk2.utils.ext.ViewExt.isAdd
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.imp.GameApp
import com.cn.game.sdk2.websocket.isTokenValid
import com.cn.game.sdk2.websocket.token
import com.cn.game.sdk2.websocket.viewmodel.GameAboutModel
import com.gyf.immersionbar.ImmersionBar
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {
    var views: FastLogoView? = null
    private var mFragList = ArrayList<Fragment>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //ImmersionBar.with(this).statusBarColor(com.cn.game.sdk2.R.color.blue).init()
        val btnOpen = findViewById<TextView>(R.id.btnOpen)
        val llshow = findViewById<RelativeLayout>(R.id.llshow)
        val btnXiu = findViewById<Button>(R.id.btnXiu)
        findViewById<Button>(R.id.btnSetBetResult).setOnClickListener {
//            gameAboutModel.manualLeopard = true
        }

        GameApp.setSocketStatesCallback(object : GameApp.SocketStatesCallback{
            override fun onOpen() {
                btnOpen.post{
                    btnOpen.text = "服务器连接成功,点击登录"
                    btnOpen.isClickable = true
                }
            }

            override fun onClose(isNeedReconnect: Boolean) {
                btnOpen.post{
                    if(isNeedReconnect){
                        btnOpen.text = "正在重新连接服务器"
                    }else{
                        btnOpen.text = "token失效,点击重新登录"
                        btnOpen.isClickable = true
                    }
                }
            }
        })
        btnOpen.setOnClickListener {
            btnOpen.isClickable = false;
            if(gameAboutModel.isLoginSuccess.value == true){

            }else{
                //92:ZyBmhNCJ   87:MHxIHlYM
                if(!isTokenValid){
                    btnOpen.text = "正在重新连接服务器"
                    //GameSocketManager.getInstance()?.initSocketClient("wss://ws.qxe68.com:7001/api/game/5702")
                }else{
                    GameApp.login(
                        token, "wali-internal", false)
                    btnOpen.text = "正在登录"
                }
            }
        }
        gameAboutModel.isLoginSuccess.observe(this){result->
            if(result){
                btnOpen.text = "正在进入直播间"
            }else{
                btnOpen.isClickable = true
                btnOpen.text = "登录失败"
            }
        }
        gameAboutModel.isSitDown.observe(this){
            GameApp.enterLive("1213", listOf(1), "")
        }

        if(BuildConfig.BUILD_TYPE == "debug"){
            GameApp.createFloatEnterView(this@MainActivity).apply {
                if(!this.isAdd()) {
                    val lp =
                        RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height)
                    lp.topMargin = 200.dp2px
                    lp.marginEnd = 0.dp2px
                    lp.addRule(RelativeLayout.ALIGN_PARENT_END)
                    llshow.addView(this, lp)
                }
            }
            GameApp.createFloatResultView(this@MainActivity).apply {
                if(!this.isAdd()) {
                    val lp =
                        RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height)
                    lp.topMargin = 50.dp2px
                    lp.marginEnd = 0.dp2px
                    lp.addRule(RelativeLayout.ALIGN_PARENT_END)
                    llshow.addView(this, lp)
                }
            }
        }
        gameAboutModel.isEnterGroup.observe(this){result->
            if(result){
                btnOpen.text = "已进入直播间"
                val context = this@MainActivity
                GameApp.createFloatEnterView(context).apply {
                    if(!this.isAdd()) {
                        val lp = RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height)
                        lp.topMargin = 200.dp2px
                        lp.marginEnd = 0.dp2px
                        lp.addRule(RelativeLayout.ALIGN_PARENT_END)
                        llshow.addView(this, lp)
                    }
                }
                GameApp.createFloatResultView(context).apply {
                    if(!this.isAdd()) {
                        val lp =
                            RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height)
                        lp.topMargin = 50.dp2px
                        lp.marginEnd = 0.dp2px
                        lp.addRule(RelativeLayout.ALIGN_PARENT_END)
                        llshow.addView(this, lp)
                    }
                }
                TextView(context).apply {
                    val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT)
                    lp.topMargin = 200.dp2px
                    lp.marginEnd = 0.dp2px
                    setTextColor(ContextCompat.getColor(context,R.color.white))
                    background = ContextCompat.getDrawable(context,com.cn.game.sdk2.R.color.blue_ed)
                    setPadding(10.dp2px)
                    lp.addRule(RelativeLayout.ALIGN_PARENT_START)
                    llshow.addView(this, lp)
                    text = "token:$token"
                }

            }
        }

        btnXiu.setOnClickListener {
//            MyWsManager.getInstance(this)?.onTest()
            //MyGameManager.setToast(this)

        }
        //appGameViewModel.ceshEvent.postValue(true)
        //初始化尾部
//        MyWsManager.getInstance(this)?.initService()

    }


}