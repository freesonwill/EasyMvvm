package com.xcjh.app.ui.home.my.operate

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.gyf.immersionbar.ImmersionBar
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.databinding.ActivityAccountNumberBinding
import com.xcjh.app.databinding.ActivityBindFacilityBinding
import com.xcjh.app.ui.login.LetterCountryActivity
import com.xcjh.app.view.slider.AESUtil
import com.xcjh.app.view.slider.CaptchaCheckOt
import com.xcjh.app.view.slider.ImageUtil
import com.xcjh.app.view.slider.WordImageView
import com.xcjh.app.vm.MainVm
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.bindadapter.CustomBindAdapter.afterTextChanged
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.view.clickNoRepeat

/**
 * 绑定邮箱或者手机好
 */
class BindFacilityActivity  : BaseActivity<AccountNumberVm, ActivityBindFacilityBinding>() {
    var type:Int=0 //0是手机号 1是邮箱
    private val mainVm: MainVm by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .titleBar(mDatabind.rltTop)
            .navigationBarDarkIcon(true)
            .navigationBarColor(R.color.c_ffffff)
            .init()

        //收到通知其他地方登录
        appViewModel.quitLoginEvent.observe(this){
            finish()
        }
        intent.extras?.apply {
            type = getInt("type", 0)
        }

        if(type==0){
            mDatabind.tvTitle.text=resources.getString(R.string.bind_txt_phone)
            mDatabind.rlBindShowPhone.visibility=View.VISIBLE
            mDatabind.llBindShowEmail.visibility=View.GONE
        }else{
            mDatabind.tvTitle.text=resources.getString(R.string.bind_txt_mailbox)
            mDatabind.rlBindShowPhone.visibility=View.GONE
            mDatabind.llBindShowEmail.visibility=View.VISIBLE
        }
        mDatabind.ivBack.clickNoRepeat {
            finish()
        }


        if(type==0){
            mDatabind.edtPhone.afterTextChanged{
                //语言 0是中文  1是繁体  2是英文
                setSave()


                    monitorPhone()


            }
            mDatabind.edtCode.afterTextChanged{
                setSave()
                    monitorPhone()


            }





        }else{
            mDatabind.edtEmail.afterTextChanged{
                setSave()
                monitorEmail()

            }
            mDatabind.edtCode.afterTextChanged{
                setSave()
                    monitorEmail()


            }
        }

        //点击保存
        mDatabind.tvOption.clickNoRepeat {
            if(type==0){
                if(mDatabind.edtPhone.text!!.trim().toString().isEmpty()||mDatabind.edtPhone.text!!.trim().length<11
                    ||mDatabind.edtCode.text.toString().trim().isEmpty()){
                    return@clickNoRepeat
                }

                mViewModel.bindPhone(mDatabind.edtPhone.text!!.trim().toString(),mDatabind.edtCode.text.toString().trim(),
                    mDatabind.tvgo.text!!.trim().toString(),this)


            }else{
                if(mDatabind.edtEmail.text!!.trim().toString().isEmpty()
                    ||mDatabind.edtCode.text!!.trim().toString().isEmpty()){
                    return@clickNoRepeat
                }
                mViewModel.bindEmail(mDatabind.edtEmail.text!!.trim().toString(),mDatabind.edtCode.text!!.trim().toString(),this)


            }
        }
        //点击发送验证码
        mDatabind.tvgetcodePhone.clickNoRepeat {
            if(type==0){
                //语言 0是中文  1是繁体  2是英文
                if(Constants.languageType==0|| Constants.languageType==1){
                    if (mDatabind.tvgetcodePhone.text.length == 5) {
                        if (mDatabind.edtPhone.text.toString().isEmpty()) {

                            myToast(resources.getString(R.string.please_input_phone_num))
                            return@clickNoRepeat
                        }
                        dialogText()
                    }
                }else{
                    if (mDatabind.tvgetcodePhone.text.length ==4) {
                        if (mDatabind.edtPhone.text.toString().isEmpty()) {

                            myToast(resources.getString(R.string.please_input_phone_num))
                            return@clickNoRepeat
                        }
                        dialogText()
                    }
                }
            }

        }



        mDatabind.tvgetcodeEmail.clickNoRepeat{
            if(type==1){
                //语言 0是中文  1是繁体  2是英文
                if(Constants.languageType==0|| Constants.languageType==1){
                    if (mDatabind.tvgetcodeEmail.text.length == 5) {
                        if (mDatabind.edtEmail.text.toString().isEmpty()) {

                            myToast(resources.getString(R.string.please_input_email))
                            return@clickNoRepeat
                        }
                        dialogText()
                    }
                }else{
                    if (mDatabind.tvgetcodeEmail.text.length ==4) {
                        if (mDatabind.edtEmail.text.toString().isEmpty()) {

                            myToast(resources.getString(R.string.please_input_email))
                            return@clickNoRepeat
                        }
                        dialogText()
                    }
                }
            }
        }

        //选择区号
        mDatabind.tvgo.clickNoRepeat {
            startNewActivity<LetterCountryActivity>()
        }


    }

    override fun createObserver() {
        super.createObserver()

        mViewModel.bindSucceed.observe(this){
            mainVm.getUserInfo()
            finish()
        }
        //准备发送短信或者邮箱
        mViewModel.sendingMessage.observe(this){
//            type = 1//1是手机号登录，2是邮箱登录
            if(type==0){
                mViewModel.getMessage(mDatabind.edtPhone.text.toString().trim(),mDatabind.tvgo.text.toString().trim())

            }else{
                mViewModel.getEmail(mDatabind.edtEmail.text.toString().trim())
            }
        }


        //发送短信成功
        mViewModel.sendingMessageSuccess.observe(this){
            mDatabind.tvgetcodePhone.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.c_a6a6b2
                )
            )
            startCountdown(mDatabind.tvgetcodePhone, 60)
        }

        //发送邮箱成功
        mViewModel.sendingEmailSuccess.observe(this){
            mDatabind.tvgetcodeEmail.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.c_a6a6b2
                )
            )
            startCountdown(mDatabind.tvgetcodeEmail, 60)
        }

        //获取到成功了
        mViewModel.codeVerify.observe(this){
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
    var handler: Handler? = null
    fun runUIDelayed(run: Runnable, de: Int) {
        if (handler == null)
            handler = Handler(Looper.getMainLooper())
        handler!!.postDelayed(run, de.toLong())
    }
    /**
     * 监听手机号的输入
     */
    fun monitorPhone(){
        if(mDatabind.edtCode.text.toString().trim().isNotEmpty()){

            mDatabind.tvOption.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.tvOption.background= ContextCompat.getDrawable(this,R.drawable.shape_r28_34a853)
        }else{

            mDatabind.tvOption.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))
            mDatabind.tvOption.background= ContextCompat.getDrawable(this,R.drawable.shape_r28_f2f3f7)
        }
    }

    /**
     * 监听邮箱的输入
     */
    fun monitorEmail(){
        if(mDatabind.edtCode.text.toString().trim().isNotEmpty()){

            mDatabind.tvOption.setTextColor(ContextCompat.getColor(this,R.color.c_ffffff))
            mDatabind.tvOption.background= ContextCompat.getDrawable(this,R.drawable.shape_r28_34a853)
        }else{

            mDatabind.tvOption.setTextColor(ContextCompat.getColor(this,R.color.c_94999f))
            mDatabind.tvOption.background= ContextCompat.getDrawable(this,R.drawable.shape_r28_f2f3f7)
        }





    }

    var mcode="+86"
    override fun onResume() {
        super.onResume()
        if (Constants.PHONE_CODE.isNotEmpty()) {
            mcode=Constants.PHONE_CODE
            mDatabind.tvgo.text = Constants.PHONE_CODE
            Constants.PHONE_CODE=""
        }
    }

    var customDialog:CustomDialog?=null
    var wordView:WordImageView?=null
    var tv_delete:TextView?=null
    var tv_refresh:ImageView?=null
    var bottomTitle:TextView?=null
    var rl_pb_word:ProgressBar?=null
    var token:String=""
    var baseImageBase64: String = ""//背景图片
    var key: String = ""//ase加密密钥
    //点击的文字
    var cryptedStrDate:String=""

    fun  dialogText(){
//        val windowManager = (this@LoginActivity as Activity).windowManager
//        val display = windowManager.defaultDisplay
//        val lp = window!!.attributes
//        lp.width = display.width * 9 / 10//设置宽度为屏幕的0.9

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
                    val bitmap: Bitmap = ImageUtil.getBitmap(this@BindFacilityActivity, R.drawable.bg_default)
//                    wordView.setUp(
//                        ImageUtil.base64ToBitmap(ImageUtil.bitmapToBase64(bitmap))!!
//                    )
                    mViewModel.getText("clickWord")
                    //获取到图片了
                    mViewModel.codeText.observe(this@BindFacilityActivity){data->
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

                }

            }).setAlign(CustomDialog.ALIGN.CENTER).setCancelable(false)
//                .setWidth(display.width * 9 / 10)
            .setMaskColor(//背景遮罩
                ContextCompat.getColor(this, com.xcjh.base_lib.R.color.blacks_tr)

            ).show()




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
                        this@BindFacilityActivity,
                        R.color.c_34a853
                    )
                )
            }
        }

        countDownTimer.start()
    }

    /**
     * 设置保存按钮状态
      */
    fun setSave(){
        if(type==0){
            if(Constants.languageType==0|| Constants.languageType==1){

                    if(mDatabind.edtPhone.text.toString().trim().isEmpty()
                        ||mDatabind.edtPhone.text.toString().trim().length<11){
                        mDatabind.tvgetcodePhone.setTextColor(
                            ContextCompat.getColor(
                                this,
                                R.color.c_a6a6b2
                            )
                        )
                    }else{
                        mDatabind.tvgetcodePhone.setTextColor(
                            ContextCompat.getColor(
                                this,
                                R.color.c_34a853
                            )
                        )
                    }

            }else{
                if(mDatabind.edtPhone.text.toString().trim().isEmpty()
                    ||mDatabind.edtPhone.text.toString().trim().length<11){
                    mDatabind.tvgetcodePhone.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.c_a6a6b2
                        )
                    )
                }else{
                    mDatabind.tvgetcodePhone.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.c_34a853
                        )
                    )
                }
            }
        }else{
            if(Constants.languageType==0|| Constants.languageType==1){

                    if(mDatabind.edtEmail.text.toString().trim().isEmpty()){
                        mDatabind.tvgetcodeEmail.setTextColor(
                            ContextCompat.getColor(
                                this,
                                R.color.c_a6a6b2
                            )
                        )
                    }else{
                        mDatabind.tvgetcodeEmail.setTextColor(
                            ContextCompat.getColor(
                                this,
                                R.color.c_34a853
                            )
                        )
                    }

            }else{
                if(mDatabind.edtEmail.text.toString().trim().isEmpty()
                    ||mDatabind.edtEmail.text.toString().trim().length<11){
                    mDatabind.tvgetcodeEmail.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.c_a6a6b2
                        )
                    )
                }else{
                    mDatabind.tvgetcodeEmail.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.c_34a853
                        )
                    )
                }
            }

        }

    }
}