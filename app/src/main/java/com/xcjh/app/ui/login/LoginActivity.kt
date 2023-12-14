package com.xcjh.app.ui.login

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.fastjson.JSONObject
import com.drake.brv.utils.models
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.LetterBeann
import com.xcjh.app.bean.PostLoaginBean
import com.xcjh.app.databinding.ActivityLoaginBinding
import com.xcjh.app.ui.Index.IndexLetterActivity
import com.xcjh.app.utils.selectCountry
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.bindViewPager2
import com.xcjh.base_lib.utils.initActivity
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.setOnclickNoRepeat
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/***
 * 登录
 */

class LoginActivity : BaseActivity<LoginVm, ActivityLoaginBinding>() {
    private val mFragments: ArrayList<Fragment> = ArrayList<Fragment>()
    var type = 1//1是手机号登录，2是邮箱登录
    private var models = mutableListOf<LetterBeann>()
    private var listStr = mutableListOf<String>()
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .titleBar(mDatabind.titleTop.root)
            .init()
        mDatabind.titleTop.tvTitle.text = resources.getString(R.string.loginandre)
        initMaps()
        mFragments.add(Fragment())
        mFragments.add(Fragment())
        mDatabind.vp.initActivity(this, mFragments, false)
        mDatabind.magicIndicator.bindViewPager2(
            mDatabind.vp, arrayListOf(
                resources.getString(R.string.txt_phone_lagoin),
                resources.getString(R.string.txt_email_login)
            ),
            R.color.black,
            R.color.black,
            16f, 16f, true, false,
            R.color.c_34a853,38, margin = 30
        )
        mDatabind.vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == 0) {
                    mDatabind.edtcodePhone.setText("")
                    type = 1
                    mDatabind.linphone.visibility = View.VISIBLE
                    mDatabind.linemaile.visibility = View.GONE
                } else {
                    mDatabind.edtcodePhone.setText("")
                    type = 2
                    mDatabind.linphone.visibility = View.GONE
                    mDatabind.linemaile.visibility = View.VISIBLE
                }
            }

        })
        setOnclickNoRepeat(
            mDatabind.tvlogin, mDatabind.tvgetcodeEmalie,
            mDatabind.tvgetcodePhone, mDatabind.tvgo, mDatabind.ivgo
        ) {
            when (it.id) {

                R.id.tvlogin -> {

                    when (type) {//1是手机号登录，2是邮箱登录
                        1 -> {
                            if (mDatabind.edtphone.text.toString().isEmpty()) {

                                myToast(resources.getString(R.string.please_input_phone_num))
                                return@setOnclickNoRepeat
                            }
                            if (mDatabind.edtcodePhone.text.toString().isEmpty()) {
                                myToast(resources.getString(R.string.please_input_phone_code))
                                return@setOnclickNoRepeat
                            }
                            mViewModel.getLogin(
                                PostLoaginBean(
                                    mDatabind.edtphone.text.toString(),
                                    null,
                                    mDatabind.edtcodePhone.text.toString(),
                                    null,
                                    type
                                )
                            )
                        }

                        2 -> {
                            if (mDatabind.edtemail.text.toString().isEmpty()) {

                                myToast(resources.getString(R.string.please_input_email))
                                return@setOnclickNoRepeat
                            }
                            if (mDatabind.edtcodePhone.text.toString().isEmpty()) {
                                myToast(resources.getString(R.string.please_input_phone_code))
                                return@setOnclickNoRepeat
                            }
                            mViewModel.getLogin(
                                PostLoaginBean(
                                    mDatabind.edtemail.text.toString(),
                                    null,
                                    mDatabind.edtcodePhone.text.toString(),
                                    null,
                                    type
                                )
                            )
                        }
                    }

                }

                R.id.tvgetcode_emalie -> {
                    if (mDatabind.tvgetcodeEmalie.text.length == 5) {

                        if (mDatabind.edtemail.text.toString().isEmpty()) {

                            myToast(resources.getString(R.string.please_input_email))
                            return@setOnclickNoRepeat
                        }
                        mDatabind.tvgetcodeEmalie.setTextColor(
                            ContextCompat.getColor(
                                this@LoginActivity,
                                R.color.c_a6a6b2
                            )
                        )
                        startCountdown(mDatabind.tvgetcodeEmalie, 60)

//                            mViewModel.getLoagin(PostLoaginBean(mDatabind.edtphone.text.toString(),mDatabind.edtcode.text.toString()
//                                ,null,null,type))
                    }


                }

                R.id.tvgetcode_phone -> {
                    if (mDatabind.tvgetcodePhone.text.length == 5) {

                        if (mDatabind.edtphone.text.toString().isEmpty()) {

                            myToast(resources.getString(R.string.please_input_phone_num))
                            return@setOnclickNoRepeat
                        }
                        mDatabind.tvgetcodePhone.setTextColor(
                            ContextCompat.getColor(
                                this@LoginActivity,
                                R.color.c_a6a6b2
                            )
                        )
                        startCountdown(mDatabind.tvgetcodePhone, 60)

//                            mViewModel.getLoagin(PostLoaginBean(mDatabind.edtphone.text.toString(),mDatabind.edtcode.text.toString()
//                                ,null,null,type))


                    }
                }

                R.id.tvgo, R.id.ivgo -> {

                    selectCountry(this, listStr) {
                        mDatabind.tvgo.text = it.substring(it.indexOf("(") + 1, it.length - 1)

                    }
                    // com.xcjh.base_lib.utils.startNewActivity<IndexLetterActivity>()
                }
            }
        }
        mDatabind.edtphone.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {


                    if (s.toString().isEmpty()) {
                        mDatabind.tvgetcodePhone.setTextColor(
                            ContextCompat.getColor(
                                this@LoginActivity,
                                R.color.c_a6a6b2
                            )
                        )
                        mDatabind.tvlogin.setBackgroundResource(R.drawable.shape_r43_f2f3f7)
                        mDatabind.tvlogin.setTextColor(
                            ContextCompat.getColor(
                                this@LoginActivity,
                                R.color.c_94999f
                            )
                        )
                    } else {
                        mDatabind.tvgetcodePhone.setTextColor(
                            ContextCompat.getColor(
                                this@LoginActivity,
                                R.color.c_34a853
                            )
                        )
                        if (mDatabind.edtcodePhone.text.toString().isEmpty()) {
                            mDatabind.tvlogin.setBackgroundResource(R.drawable.shape_r43_f2f3f7)
                            mDatabind.tvlogin.setTextColor(
                                ContextCompat.getColor(
                                    this@LoginActivity,
                                    R.color.c_94999f
                                )
                            )
                        } else {
                            mDatabind.tvlogin.setBackgroundResource(R.drawable.shape_r43_34a853)
                            mDatabind.tvlogin.setTextColor(
                                ContextCompat.getColor(
                                    this@LoginActivity,
                                    com.xcjh.base_lib.R.color.white
                                )
                            )

                        }

                    }

                }

            })
        mDatabind.edtcodePhone.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {


                    if (s.toString().isEmpty()) {
                        mDatabind.tvlogin.setBackgroundResource(R.drawable.shape_r43_f2f3f7)
                        mDatabind.tvlogin.setTextColor(
                            ContextCompat.getColor(
                                this@LoginActivity,
                                R.color.c_94999f
                            )
                        )
                    } else {

                        if (mDatabind.edtphone.text.toString().isEmpty()) {
                            mDatabind.tvlogin.setBackgroundResource(R.drawable.shape_r43_f2f3f7)
                            mDatabind.tvlogin.setTextColor(
                                ContextCompat.getColor(
                                    this@LoginActivity,
                                    R.color.c_94999f
                                )
                            )
                        } else {
                            mDatabind.tvlogin.setBackgroundResource(R.drawable.shape_r43_34a853)
                            mDatabind.tvlogin.setTextColor(
                                ContextCompat.getColor(
                                    this@LoginActivity,
                                    com.xcjh.base_lib.R.color.white
                                )
                            )
                        }

                    }


                }

            })


        mDatabind.edtemail.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {


                    if (s.toString().isEmpty()) {
                        mDatabind.tvgetcodeEmalie.setTextColor(
                            ContextCompat.getColor(
                                this@LoginActivity,
                                R.color.c_a6a6b2
                            )
                        )
                        mDatabind.tvlogin.setBackgroundResource(R.drawable.shape_r43_f2f3f7)
                        mDatabind.tvlogin.setTextColor(
                            ContextCompat.getColor(
                                this@LoginActivity,
                                R.color.c_94999f
                            )
                        )
                    } else {
                        mDatabind.tvgetcodeEmalie.setTextColor(
                            ContextCompat.getColor(
                                this@LoginActivity,
                                R.color.c_34a853
                            )
                        )
                        if (mDatabind.tvgetcodeEmalie.text.toString().isEmpty()) {
                            mDatabind.tvlogin.setBackgroundResource(R.drawable.shape_r43_f2f3f7)
                            mDatabind.tvlogin.setTextColor(
                                ContextCompat.getColor(
                                    this@LoginActivity,
                                    R.color.c_94999f
                                )
                            )
                        } else {
                            mDatabind.tvlogin.setBackgroundResource(R.drawable.shape_r43_34a853)
                            mDatabind.tvlogin.setTextColor(
                                ContextCompat.getColor(
                                    this@LoginActivity,
                                    com.xcjh.base_lib.R.color.white
                                )
                            )
                        }
                    }

                }

            })
        mDatabind.edtcodeEmalie.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {


                    if (s.toString().isEmpty()) {
                        mDatabind.tvlogin.setBackgroundResource(R.drawable.shape_r43_f2f3f7)
                        mDatabind.tvlogin.setTextColor(
                            ContextCompat.getColor(
                                this@LoginActivity,
                                R.color.c_94999f
                            )
                        )
                    } else {

                        if (mDatabind.edtemail.text.toString().isEmpty()) {
                            mDatabind.tvlogin.setBackgroundResource(R.drawable.shape_r43_f2f3f7)
                            mDatabind.tvlogin.setTextColor(
                                ContextCompat.getColor(
                                    this@LoginActivity,
                                    R.color.c_94999f
                                )
                            )
                        } else {
                            mDatabind.tvlogin.setBackgroundResource(R.drawable.shape_r43_34a853)
                            mDatabind.tvlogin.setTextColor(
                                ContextCompat.getColor(
                                    this@LoginActivity,
                                    com.xcjh.base_lib.R.color.white
                                )
                            )
                        }
                    }


                }

            })
    }

    fun startCountdown(textView: TextView, totalSeconds: Long) {
        val countDownTimer = object : CountDownTimer(totalSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                textView.text = secondsLeft.toString() + "S"

            }

            override fun onFinish() {
                textView.text = resources.getString(R.string.get_code)
                textView.setTextColor(
                    ContextCompat.getColor(
                        this@LoginActivity,
                        R.color.c_34a853
                    )
                )
            }
        }

        countDownTimer.start()
    }

    override fun onResume() {
        super.onResume()
        mDatabind.tvgo.text = Constants.PHONE_CODE
    }

    override fun createObserver() {

        mViewModel.logain.observe(this) {
            if (it.isNotEmpty()) {
                //成功
                finish()

            } else {

            }

        }

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

        for (i in 0 until models.size) {
           var county= getGQ(models[i].abbreviate)
            listStr.add(county+"  "+models[i].selfName + " (" + models[i].areaCode + ")")
        }

    }
    fun getGQ(country:String):String{
        try {
            val flagOffset = 0x1F1E6
            val asciiOffset = 0x41
            val firstChar =
                Character.codePointAt(country, 0) - asciiOffset + flagOffset
            val secondChar =
                Character.codePointAt(country, 1) - asciiOffset + flagOffset
            return String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
        }catch (e:Exception){
            return ""
        }

    }
}