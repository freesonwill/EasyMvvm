package com.xcjh.app.ui.Index

import android.os.Bundle
import com.alibaba.fastjson.JSONObject
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.LetterBeann
import com.xcjh.app.databinding.FragmentLetterIndexBinding
import com.xcjh.app.databinding.ItemCityBinding
import com.xcjh.app.ui.feed.FeedVm
import com.xcjh.base_lib.Constants.Companion.PHONE_CODE
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/***
 * 反馈通知
 */

class IndexLetterActivity : BaseActivity<FeedVm, FragmentLetterIndexBinding>() {
    private var models = mutableListOf<LetterBeann>()
    override fun initView(savedInstanceState: Bundle?) {
//        ImmersionBar.with(this)
//            .statusBarDarkFont(false)
//            .keyboardEnable(true)
//            .titleBar(mDatabind.titleTop.root)
//            .init()
//        mDatabind.rv.linear().setup {
//            addType<LetterBeann>(R.layout.item_city)
//            onBind {
//                when (itemViewType) {
//                    //左边文字消息
//                    R.layout.item_city -> {
//                        var binding = getBinding<ItemCityBinding>()
//                        var ad = _data as LetterBeann
//
//                        binding.tvname.text = ad.cn +"  ("+ad.phone_code +")"
//                        binding.tvname.setOnClickListener {
//                            PHONE_CODE=ad.phone_code
//                            finish()
//                        }
//
//                    }
//
//
//                }
//
//
//            }
//        }.models=models


        initMaps()

    }
    private fun initMaps() {
        // 解析Json数据
        val newstringBuilder = StringBuilder()
        var inputStream: InputStream? = null
        try {
            inputStream = resources.assets.open("JHAreaCode.json")
            val isr = InputStreamReader(inputStream)
            val reader = BufferedReader(isr)
            var jsonLine: String?
            while (reader.readLine().also { jsonLine = it } != null) {
                newstringBuilder.append(jsonLine)
            }
            reader.close()
            isr.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
           // LogUtil.("得到数据chuck==$e")
        }
        val str = newstringBuilder.toString()
        str.length
        //LogUtil.d("得到数据==$str")
        models =
            JSONObject.parseArray(str, LetterBeann::class.java)
        mDatabind.rv.models = models // 覆盖列表


    }
}