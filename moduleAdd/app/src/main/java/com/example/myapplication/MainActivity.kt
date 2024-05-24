package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cn.game.sdk.appGameViewModel
import com.cn.game.sdk.utils.MyGameManager
import com.cn.game.sdk.view.FastLogoView

class MainActivity :  AppCompatActivity() {
      var  views: FastLogoView?=null
    private var mFragList = ArrayList<Fragment>()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var ddd=findViewById<TextView>(R.id.ceshi)
        var llshow=findViewById<LinearLayout>(R.id.llshow)
        var btnXiu=findViewById<Button>(R.id.btnXiu)






        ddd.setOnClickListener {
                var ddd= com.cn.game.sdk.utils.MyGameManager
//            views=   ddd.getFastThreeView(this)
//            llshow.addView(views)
//
//            views?.setFastLogoClickListener(object :FastLogoView.OnFastLogoClickListener{
//                override fun onButtonClick() {
//                 }
//
//            })
            ddd.showFastView(this)
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