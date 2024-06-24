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
import com.cn.game.sdk2.utils.MyGameManager
import com.cn.game.sdk2.websocket.imp.GameSDK
import com.cn.game.sdk2.websocket.interfaces.SDKLoginCallbackListener
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
            ViewHelper.showFastView(this)
            //MyGameManager.showFastView(this)
            GameSDK.loginGameWithAgentName(
                "wali-internal", "81:zdw2oSuv", object : SDKLoginCallbackListener {
                    override fun callback(code: Int, message: String?) {
                        "login:code-${code},message-${message}".loge()
                    }

                }
            )
        }
        btnXiu.setOnClickListener {
//            MyWsManager.getInstance(this)?.onTest()
            MyGameManager.setToast(this)

        }
        //appGameViewModel.ceshEvent.postValue(true)
        //初始化尾部
//        MyWsManager.getInstance(this)?.initService()

    }
}