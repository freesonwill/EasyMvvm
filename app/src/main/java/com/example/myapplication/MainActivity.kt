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
import com.cn.game.sdk2.websocket.imp.GameApp
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

        btnOpen.setOnClickListener {
            //MyGameManager.showFastView(this)
            //ViewHelper.showFastView(this)

            if(gameAboutModel.isLoginSuccess.value == true){
                GameApp.enterLive("1213", listOf(1), "")
                ViewHelper.showFastView(this)
                ViewHelper.showFastViewOverlay(this)
            }else{
                //92:ZyBmhNCJ   87:MHxIHlYM
                GameApp.login(
                    token, "wali-internal", true
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