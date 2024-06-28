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
import com.cn.game.sdk2.websocket.gameAboutModel
import com.cn.game.sdk2.websocket.imp.GameSDK
import com.cn.game.sdk2.websocket.interfaces.SDKEnterLiveCallbackListener
import com.cn.game.sdk2.websocket.interfaces.SDKLoginCallbackListener
import com.cn.game.sdk2.websocket.token
import com.cn.game.sdk2.websocket.viewmodel.GameAboutModel
import com.xcjh.base_lib.utils.loge

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

        btnOpen.setOnClickListener {
            //MyGameManager.showFastView(this)
            ViewHelper.showFastView(this)
            if(gameAboutModel.isLoginSuccess.value == true){
                GameSDK.enterLive("1213", listOf(1), "", object : SDKEnterLiveCallbackListener {
                    override fun callback(code: Int, message: String?) {
                        "enterLive:code-$code,message$message".loge()
                    }
                })
                ViewHelper.showFastView(this)
                ViewHelper.showFastViewOverlay(this)
            }else{
                //92:ZyBmhNCJ   87:MHxIHlYM
                GameSDK.loginGameWithAgentName(
                    "wali-internal", token,"Gregg Denesik", object : SDKLoginCallbackListener {
                        override fun callback(code: Int, message: String?) {
                            "login:code-${code},message-${message}".loge()
                        }

                    }
                )
            }
        }
        gameAboutModel.isLoginSuccess.observe(this){result->
            if(result){
                btnOpen.text = "进入直播间"
            }
        }
        gameAboutModel.isEnterGroup.observe(this){result->
            if(result){
                btnOpen.text = "已进入直播间"
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