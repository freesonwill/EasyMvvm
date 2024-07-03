package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.cn.game.sdk2.ui.helper.ViewHelper
import com.cn.game.sdk2.ui.view.FastLogoView
import com.cn.game.sdk2.websocket.GameSocketManager
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.imp.GameApp
import com.cn.game.sdk2.websocket.isTokenValid
import com.cn.game.sdk2.websocket.token

class MainActivity : AppCompatActivity() {
    var views: FastLogoView? = null
    private var mFragList = ArrayList<Fragment>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var btnOpen = findViewById<TextView>(R.id.btnOpen)
        var llshow = findViewById<LinearLayout>(R.id.llshow)
        var btnXiu = findViewById<Button>(R.id.btnXiu)
        /*ViewHelper.showHelpDialog(this)
        ViewHelper.showFastView(this)
        return@setOnClickListener*/
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
            //MyGameManager.showFastView(this)
            //ViewHelper.showFastView(this)
            btnOpen.isClickable = false;
            if(gameAboutModel.isLoginSuccess.value == true){

            }else{
                //92:ZyBmhNCJ   87:MHxIHlYM
                if(!isTokenValid){
                    btnOpen.text = "正在重新连接服务器"
                    GameSocketManager.getInstance()?.initSocketClient()
                }else{
                    GameApp.login(
                        token, "wali-internal", true
                    )
                    btnOpen.text = "正在登录"
                }
            }
        }
        gameAboutModel.isLoginSuccess.observe(this){result->
            if(result){
                btnOpen.text = "进入直播间"
            }else{
                btnOpen.isClickable = true
                btnOpen.text = "登录失败"
            }
        }
        gameAboutModel.isEnterGroup.observe(this){result->
            if(result){
                btnOpen.text = "已进入直播间"
                //GameApp.enterLive("1213", listOf(1), "")
                /*ViewHelper.showFastView(this)
                ViewHelper.showFastViewOverlay(this)*/
                val v = GameApp.createFloatEnterView(this@MainActivity)
                val v1 = GameApp.createFloatResultView(this@MainActivity)
                findViewById<LinearLayout>(R.id.llshow).apply {
                    addView(v)
                    addView(v1)
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