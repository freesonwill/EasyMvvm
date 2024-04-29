package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.cn.game.sdk.appGameViewModel
import com.cn.game.sdk.common.GameReqCode
import com.cn.game.sdk.utils.MyGameManager
import com.cn.game.sdk.view.FastLogoView
import com.cn.game.sdk.websocket.MyWsManager
import game.common.proto.ClientReq.LoginReq

class MainActivity : Activity() {
      var  views: FastLogoView?=null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var ddd=findViewById<TextView>(R.id.ceshi)
        var llshow=findViewById<LinearLayout>(R.id.llshow)
        var btnXiu=findViewById<Button>(R.id.btnXiu)


        ddd.setOnClickListener {
                var ddd= com.cn.game.sdk.utils.MyGameManager
            views=   ddd.getFastThreeView(this)
            llshow.addView(views)

            views?.setFastLogoClickListener(object :FastLogoView.OnFastLogoClickListener{
                override fun onButtonClick() {
                    Toast.makeText(this@MainActivity,"111111111",Toast.LENGTH_SHORT).show()
                }

            })
        }
        btnXiu.setOnClickListener {
//            MyWsManager.getInstance(this)?.onTest()
            MyGameManager.setToast(this)

        }
        appGameViewModel.ceshEvent.postValue(true)
        //初始化尾部
//        MyWsManager.getInstance(this)?.initService()

    }
}