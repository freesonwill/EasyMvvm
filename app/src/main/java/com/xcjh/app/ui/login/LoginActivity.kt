package com.xcjh.app.ui.login


import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.engagelab.privates.core.api.MTCorePrivatesApi
import com.gyf.immersionbar.ImmersionBar
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.xcjh.app.R
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.LetterBeann
import com.xcjh.app.bean.PostLoaginBean
import com.xcjh.app.databinding.ActivityLoaginBinding
import com.xcjh.app.utils.SoundManager
import com.xcjh.app.view.slider.AESUtil
import com.xcjh.app.view.slider.CaptchaCheckOt
import com.xcjh.app.view.slider.ImageUtil
import com.xcjh.app.view.slider.WordImageView
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.bindViewPagerEr
import com.xcjh.base_lib.utils.initActivity
import com.xcjh.base_lib.utils.setOnclickNoRepeat
import com.xcjh.base_lib.utils.view.clickNoRepeat

/***
 * 登录
 */

class LoginActivity : BaseActivity<LoginVm, ActivityLoaginBinding>() {
    private val mFragments: ArrayList<Fragment> = ArrayList<Fragment>()
    var type = 1//1是手机号登录，2是邮箱登录
    private var models = mutableListOf<LetterBeann>()
    private var listStr = mutableListOf<String>()
    var mcode="+86"

    var customDialog:CustomDialog?=null
    var baseImageBase64: String = ""//背景图片
    var slideImageBase64: String = ""//滑动图片
    var key: String = ""//ase加密密钥
    var token:String=""
    var handler: Handler? = null
    //点击的文字
    var cryptedStrDate:String=""

    var wordView:WordImageView?=null
    var tv_delete:TextView?=null
    var tv_refresh:ImageView?=null
    var bottomTitle:TextView?=null
    var rl_pb_word:ProgressBar?=null

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .titleBar(mDatabind.titleTop.root)
            .navigationBarDarkIcon(true)
            .navigationBarColor(R.color.c_ffffff)
            .init()


        mDatabind.titleTop.tvTitle.text = resources.getString(R.string.loginandre)
        mDatabind.btn.clickNoRepeat {
//           var dd= MyGameManager.getFastThreeView(this)
//            mDatabind.llshow.addView(dd)

        }

        mFragments.add(Fragment())
        mFragments.add(Fragment())
        mDatabind.vp.initActivity(this, mFragments, false)
        mDatabind.magicIndicator.bindViewPagerEr(
            mDatabind.vp, arrayListOf(
                resources.getString(R.string.txt_phone_lagoin),
                resources.getString(R.string.txt_email_login)
            ),
            R.color.black,
            R.color.black,
            16f, 16f, false, scrollEnable=true,
            R.color.c_34a853, 38, margin = 30
        )
        mDatabind.vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == 0) {
                   // mDatabind.edtphone.requestFocus()
                    mDatabind.edtcodePhone.setText("")
                    type = 1
                    mDatabind.linphone.visibility = View.VISIBLE
                    mDatabind.linemaile.visibility = View.GONE
                } else {
                    //mDatabind.edtemail.requestFocus()
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

                               // myToast(resources.getString(R.string.please_input_phone_num))
                                return@setOnclickNoRepeat
                            }
                            if (mDatabind.edtcodePhone.text.toString().isEmpty()) {
                              //  myToast(resources.getString(R.string.please_input_phone_code))
                                return@setOnclickNoRepeat
                            }
                            SoundManager.playMedia()
                            mViewModel.getLogin(
                                PostLoaginBean(
                                     mDatabind.edtphone.text.toString(),
                                   code= mDatabind.edtcodePhone.text.toString(),
                                   "",
                                    null,
                                    type,areaCode=mcode
                                )
                            )
                        }

                        2 -> {
                            if (mDatabind.edtemail.text.toString().isEmpty()) {

                              //  myToast(resources.getString(R.string.please_input_email))
                                return@setOnclickNoRepeat
                            }
                            if (mDatabind.edtcodeEmalie.text.toString().isEmpty()) {
                               // myToast(resources.getString(R.string.please_input_phone_code))
                                return@setOnclickNoRepeat
                            }
                            SoundManager.playMedia()
                            mViewModel.getLogin(
                                PostLoaginBean(
                                    mDatabind.edtemail.text.toString(),
                                    code=mDatabind.edtcodeEmalie.text.toString(),
                                   "",
                                    null,
                                    type,areaCode=""
                                )
                            )
                        }
                    }

                }

                R.id.tvgetcode_emalie -> {
                    //语言 0是中文  1是繁体  2是英文
                    if(Constants.languageType==0||Constants.languageType==1){
                        if (mDatabind.tvgetcodeEmalie.text.length == 5) {

                            if (mDatabind.edtemail.text.toString().isEmpty()) {

//                                myToast(resources.getString(R.string.please_input_email))
                                return@setOnclickNoRepeat
                            }
                            if (!isEmailValid(mDatabind.edtemail.text.toString())) {

//                                myToast(resources.getString(R.string.please_inputcureet_email))
                                return@setOnclickNoRepeat
                            }

                            dialogText()

                        }

                    }else{
                        if (mDatabind.tvgetcodeEmalie.text.length == 4) {

                            if (mDatabind.edtemail.text.toString().isEmpty()) {

//                                myToast(resources.getString(R.string.please_input_email))
                                return@setOnclickNoRepeat
                            }
                            if (!isEmailValid(mDatabind.edtemail.text.toString())) {

//                                myToast(resources.getString(R.string.please_inputcureet_email))
                                return@setOnclickNoRepeat
                            }

                            dialogText()

                        }
                    }


//                    if (mDatabind.tvgetcodeEmalie.text.length == 5) {
//
//                        if (mDatabind.edtemail.text.toString().isEmpty()) {
//
//                            myToast(resources.getString(R.string.please_input_email))
//                            return@setOnclickNoRepeat
//                        }
//                        if (!isEmailValid(mDatabind.edtemail.text.toString())) {
//
//                            myToast(resources.getString(R.string.please_inputcureet_email))
//                            return@setOnclickNoRepeat
//                        }
//
//                        dialogText()
////                        mDatabind.tvgetcodeEmalie.setTextColor(
////                            ContextCompat.getColor(
////                                this@LoginActivity,
////                                R.color.c_a6a6b2
////                            )
////                        )
////                        startCountdown(mDatabind.tvgetcodeEmalie, 60)
//
////                            mViewModel.getLoagin(PostLoaginBean(mDatabind.edtphone.text.toString(),mDatabind.edtcode.text.toString()
////                                ,null,null,type))
//                    }


                }

                R.id.tvgetcode_phone -> {
                    //语言 0是中文  1是繁体  2是英文
                    if(Constants.languageType==0||Constants.languageType==1){
                        if (mDatabind.tvgetcodePhone.text.length == 5) {
                            if (mDatabind.edtphone.text.toString().isEmpty()) {

//                                myToast(resources.getString(R.string.please_input_phone_num))
                                return@setOnclickNoRepeat
                            }
                            SoundManager.playMedia()
                            dialogText()
                        }
                    }else{
                        if (mDatabind.tvgetcodePhone.text.length ==4) {
                            if (mDatabind.edtphone.text.toString().isEmpty()) {

//                                myToast(resources.getString(R.string.please_input_phone_num))
                                return@setOnclickNoRepeat
                            }
                            SoundManager.playMedia()
                            dialogText()
                        }
                    }

//                    if (mDatabind.tvgetcodePhone.text.length == 5) {
//                        if (mDatabind.edtphone.text.toString().isEmpty()) {
//
//                            myToast(resources.getString(R.string.please_input_phone_num))
//                            return@setOnclickNoRepeat
//                        }
//                        dialogText()
////                        return@setOnclickNoRepeat
//
//
//
////                        mDatabind.tvgetcodePhone.setTextColor(
////                            ContextCompat.getColor(
////                                this@LoginActivity,
////                                R.color.c_a6a6b2
////                            )
////                        )
////                        startCountdown(mDatabind.tvgetcodePhone, 60)
//
////                            mViewModel.getLoagin(PostLoaginBean(mDatabind.edtphone.text.toString(),mDatabind.edtcode.text.toString()
////                                ,null,null,type))
//
//
//                    }
                }
                R.id.tvgo-> {
                    SoundManager.playMedia()
                    com.xcjh.base_lib.utils.startNewActivity<LetterCountryActivity>()
                }
                R.id.ivgo -> {
                     com.xcjh.base_lib.utils.startNewActivity<LetterCountryActivity>()
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
                            mDatabind.tvlogin.setBackgroundResource(R.drawable.search_bac_logain)
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
                            mDatabind.tvlogin.setBackgroundResource(R.drawable.search_bac_logain)
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
                            mDatabind.tvlogin.setBackgroundResource(R.drawable.search_bac_logain)
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
                            mDatabind.tvlogin.setBackgroundResource(R.drawable.search_bac_logain)
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
        if (Constants.PHONE_CODE.isNotEmpty()) {
            mcode=Constants.PHONE_CODE
            mDatabind.tvgo.text = Constants.PHONE_CODE
            Constants.PHONE_CODE=""
        }

        val windowManager = (this@LoginActivity as Activity).windowManager
        val display = windowManager.defaultDisplay
        val lp = window!!.attributes
        lp.width = display.width
    }



    override fun createObserver() {
        //准备发送短信或者邮箱
        mViewModel.sendingMessage.observe(this){
//            type = 1//1是手机号登录，2是邮箱登录
            if(type==1){
                mViewModel.getMessage(mDatabind.edtphone.text.toString().trim(),mDatabind.tvgo.text.toString().trim())

            }else{
                mViewModel.getEmail(mDatabind.edtemail.text.toString().trim())
            }
        }

        //发送短信成功
        mViewModel.sendingMessageSuccess.observe(this){
                        mDatabind.tvgetcodePhone.setTextColor(
                            ContextCompat.getColor(
                                this@LoginActivity,
                                R.color.c_a6a6b2
                            )
                        )
                        startCountdown(mDatabind.tvgetcodePhone, 60)
        }

        //发送邮箱成功
        mViewModel.sendingEmailSuccess.observe(this){
              mDatabind.tvgetcodeEmalie.setTextColor(
                            ContextCompat.getColor(
                                this@LoginActivity,
                                R.color.c_a6a6b2
                            )
                        )
                        startCountdown(mDatabind.tvgetcodeEmalie, 60)
        }


        mViewModel.logain.observe(this) {
            if (it.isNotEmpty()) {
                //极光推送绑定用户
                val registrationId: String = MTCorePrivatesApi.getRegistrationId(this)
                Log.i("============","======"+registrationId)
                mViewModel.jPUSHbIND(registrationId)

                finish()

            } else {

            }

        }
                      //获取到成功了
        mViewModel.codeVerify.observe(this@LoginActivity){
                        when (it.repCode) {
                            "0000" -> {
                                bottomTitle!!.text = "验证成功"
                                bottomTitle!!.setTextColor(Color.GREEN)
                                wordView!!.ok()
//                                runUIDelayed(
//                                    Runnable {
//
//                                        customDialog!!.dismiss()
//
////                                        loadCaptcha()
//                                    }, 1000
//                                )
                                customDialog!!.dismiss()
                                val result = token + "---" + cryptedStrDate
                                Log.e("wuyan","result:"+result)
                                mViewModel.sendingMessage.value=result


                            }
                            else -> {
                                bottomTitle!!.text = "验证失败"
                                bottomTitle!!.setTextColor(Color.RED)
                                wordView!!.fail()
                                runUIDelayed(
                                    Runnable {
                                        //刷新验证码
                                        mViewModel.getText("clickWord")
                                    }, 1500
                                )

                            }

                        }


                    }

    }


    fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$")
        return email.matches(emailRegex)
    }



    fun  dialogText(){
        val windowManager = (this@LoginActivity as Activity).windowManager
        val display = windowManager.defaultDisplay
        val lp = window!!.attributes
        lp.width = display.width * 9 / 10//设置宽度为屏幕的0.9

            customDialog= CustomDialog.build()
                .setCustomView(object : OnBindView<CustomDialog?>(R.layout.dialog_word_captcha) {
                    //                var dragView :DragImageView?=null
//                var tv_delete :TextView?=null
                    var shifting:Double=0.0

                    override fun onBind(dialog: CustomDialog?, v: View) {
                       wordView= v.findViewById<WordImageView>(R.id.wordView)
                          tv_delete = v.findViewById<TextView>(R.id.tv_delete)
                          tv_refresh = v.findViewById<ImageView>(R.id.tv_refresh)
                           bottomTitle = v.findViewById<TextView>(R.id.bottomTitle)
                        rl_pb_word = v.findViewById<ProgressBar>(R.id.rl_pb_word)



                        tv_refresh!!.setOnClickListener {
                            wordView!!.reset()
                            bottomTitle!!.text = "数据加载中......"
                            bottomTitle!!.setTextColor(Color.BLACK)
                            wordView!!.visibility = View.INVISIBLE
                             rl_pb_word!!.visibility = View.VISIBLE
                            token=""
                            mViewModel.getText("clickWord")

                        }
                        tv_delete!!.setOnClickListener {
                            dialog?.dismiss()
                        }
                        //设置默认图片
                        val bitmap: Bitmap = ImageUtil.getBitmap(this@LoginActivity, R.drawable.bg_default)
//                    wordView.setUp(
//                        ImageUtil.base64ToBitmap(ImageUtil.bitmapToBase64(bitmap))!!
//                    )
                        mViewModel.getText("clickWord")
                        //获取到图片了
                        mViewModel.codeText.observe(this@LoginActivity){data->
                            when (data?.repCode) {
                                "0000" -> {
                                    baseImageBase64 = data.repData!!.originalImageBase64
                                    token = data.repData!!.token
                                    key= data.repData!!.secretKey
                                    var wordStr: String = ""
                                    var i = 0;
                                    data.repData!!.wordList!!.forEach {
                                        i++
                                        wordStr += it
                                        if (i < data.repData!!.wordList!!.size)
                                            wordStr += ","
                                    }
                                    wordView!!.setSize(data.repData!!.wordList!!.size)
                                    bottomTitle!!.text = "请依此点击【" + wordStr + "】"
                                    bottomTitle!!.setTextColor(Color.BLACK)
                                    wordView!!.setUp(
                                        ImageUtil.base64ToBitmap(baseImageBase64)!!
                                    )
                                    wordView!!.setWordListenner(object : WordImageView.WordListenner {
                                        override fun onWordClick(cryptedStr: String) {
                                            if (cryptedStr != null) {
                                                cryptedStrDate=cryptedStr
                                                Log.e("wuyan", AESUtil.encode(cryptedStr,key))
                                                val o = CaptchaCheckOt(
                                                    captchaType = "clickWord",
                                                    pointJson = AESUtil.encode(cryptedStr,key),
                                                    token =  token
                                                )
                                                mViewModel.setText(o)
                                            }
                                        }
                                    })

                                }
                                else -> {
                                    bottomTitle!!.text = "加载失败,请刷新"
                                    bottomTitle!!.setTextColor(Color.RED)
                                    wordView!!.setSize(-1)
                                }

                            }

                            wordView!!.visibility = View.VISIBLE
                            rl_pb_word!!.visibility = View.GONE

                        }


                        //获取到成功了
//                    mViewModel.codeVerify.observe(this@LoginActivity){
////                        val point = Point(cryptedStrDate, 5.0)
////                        var pointStr = Gson().toJson(point).toString()
//
//                        when (it.repCode) {
//                            "0000" -> {
//                                bottomTitle.text = "验证成功"
//                                bottomTitle.setTextColor(Color.GREEN)
//                                wordView.ok()
//                                runUIDelayed(
//                                    Runnable {
//
//                                        customDialog!!.dismiss()
//
////                                        loadCaptcha()
//                                    }, 2000
//                                )
//
//                                val result = token + "---" + cryptedStrDate
//
//                                Log.e("wuyan","result:"+result)
//                                mViewModel.sendingMessage.value=result
//
//
//                            }
//                            else -> {
//                                bottomTitle.text = "验证失败"
//                                bottomTitle.setTextColor(Color.RED)
//                                wordView.fail()
//                                runUIDelayed(
//                                    Runnable {
//                                        //刷新验证码
//                                        mViewModel.getText("clickWord")
//                                    }, 1500
//                                )
//
//                            }
//
//                        }
//
//
//                    }
                    }






                }).setAlign(CustomDialog.ALIGN.CENTER).setCancelable(false)
                .setWidth(display.width * 9 / 10)
                .setMaskColor(//背景遮罩
                    ContextCompat.getColor(this, com.xcjh.base_lib.R.color.blacks_tr)

                ).show()




    }

    //图片
//    fun delMsgDilog(context: Context) {
//
//
//        customDialog= CustomDialog.build()
//            .setCustomView(object : OnBindView<CustomDialog?>(R.layout.dialog_verify_login) {
////                var dragView :DragImageView?=null
////                var tv_delete :TextView?=null
//                var shifting:Double=0.0
//
//                override fun onBind(dialog: CustomDialog?, v: View) {
//                    var dragView= v.findViewById<DragImageView>(R.id.dragView)
//                    var tv_delete = v.findViewById<TextView>(R.id.tv_delete)
//                    var rl_pb = v.findViewById<ProgressBar>(R.id.rl_pb)
//                    var tv_refresh = v.findViewById<ImageView>(R.id.tv_refresh)
//
//                    tv_refresh.setOnClickListener {
//                        mViewModel.getImage("blockPuzzle")
//                    }
//                      tv_delete.setOnClickListener {
//                        dialog?.dismiss()
//                    }
//                    //设置默认图片
//                    val bitmap: Bitmap = ImageUtil.getBitmap(context, R.drawable.bg_default)
//                    dragView!!.setUp(bitmap!!, bitmap!!)
//                    dragView!!.setSBUnMove(false)
//                    mViewModel.getImage("blockPuzzle")
//
//                    mViewModel.codeImage.observe(this@LoginActivity){
//                        when (it?.repCode) {
//                            "0000" -> {
//                                baseImageBase64 = it.repData!!.originalImageBase64
//                                slideImageBase64 = it.repData!!.jigsawImageBase64
//                                 token = it.repData!!.token
//                                key = it.repData!!.secretKey
//
//                                dragView.setUp(
//                                    ImageUtil.base64ToBitmap(baseImageBase64)!!,
//                                    ImageUtil.base64ToBitmap(slideImageBase64)!!
//                                )
//                                dragView.setSBUnMove(true)
//                                //设置滑动
//                                dragView.setDragListenner(object : DragImageView.DragListenner {
//                                    override fun onDrag(position: Double) {
//                                        shifting=position
//                                        checkCaptcha(position)
//                                    }
//                                })
//                            }
//                            else -> {
//                                dragView.setSBUnMove(false)
//                            }
//
//                        }
//                        dragView.visibility = View.VISIBLE
//                        rl_pb.visibility = View.GONE
//                    }
//                    //获取到成功了
//                    mViewModel.codeVerify.observe(this@LoginActivity){
//                        val point = Point(shifting, 5.0)
//                        var pointStr = Gson().toJson(point).toString()
//
//                        when (it.repCode) {
//                            "0000" -> {
//                                dragView.ok()
//                                runUIDelayed(
//                                    Runnable {
//                                        dragView.reset()
//                                        customDialog?.dismiss()
//
//                                    }, 2000
//                                )
//                                val result = token + "---" + pointStr
//                                Log.e("wuyan","======"+Gson().toJson(it))
//                                Log.e("wuyan","result:"+result);
////                                mOnResultsListener!!.onResultsClick(AESUtil.encode(result, key))
//                            }
//                            else -> {
//                                GlobalScope.launch(Dispatchers.Main) {
//                                        dragView.fail()
//                                    //刷新验证码
//                                    mViewModel.getImage("blockPuzzle")
//
//                                }
//
//                            }
//
//                        }
//                    }
//
//                }
//
//                private fun checkCaptcha(sliderXMoved: Double) {
//                    val point = Point(sliderXMoved, 5.0)
//                    var pointStr = Gson().toJson(point).toString()
//                    Log.e("wuyan", pointStr)
//                    Log.e("wuyan", AESUtil.encode(pointStr, key))
//                    //获取到滑动的偏移
//                    mViewModel.getCaptcha(sliderXMoved,key,token)
//
//
//                }
//
//            }).setAlign(CustomDialog.ALIGN.CENTER).setCancelable(false).
//        setMaskColor(//背景遮罩
//            ContextCompat.getColor(context, com.xcjh.base_lib.R.color.blacks_tr)
//
//        ).show()
//    }

    fun runUIDelayed(run: Runnable, de: Int) {
        if (handler == null)
            handler = Handler(Looper.getMainLooper())
        handler!!.postDelayed(run, de.toLong())
    }

}