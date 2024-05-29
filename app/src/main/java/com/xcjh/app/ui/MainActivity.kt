package com.xcjh.app.ui


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.Choreographer
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.engagelab.privates.core.api.MTCorePrivatesApi
import com.engagelab.privates.push.api.MTPushPrivatesApi
import com.google.gson.Gson
import com.gyf.immersionbar.ktx.showStatusBar
import com.hjq.language.LocaleContract
import com.hjq.language.MultiLanguages
import com.king.app.dialog.AppDialog
import com.king.app.updater.AppUpdater
import com.king.app.updater.callback.UpdateCallback
import com.king.app.updater.http.OkHttpManager
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.interfaces.OnPermissionResult
import com.lzf.easyfloat.permission.PermissionUtils
import com.xcjh.app.BuildConfig
import com.xcjh.app.R
import com.xcjh.app.adapter.PushCardPopup
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.bean.JsonBean
import com.xcjh.app.bean.LoginInfo
import com.xcjh.app.bean.TimeConstantsDat
import com.xcjh.app.databinding.ActivityHomeBinding
import com.xcjh.app.event.AppViewModel
import com.xcjh.app.placeLoginDialog
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.app.ui.home.home.HomeFragment
import com.xcjh.app.ui.home.msg.MsgFragment
import com.xcjh.app.ui.home.my.MyUserFragment
import com.xcjh.app.ui.home.schedule.ScheduleFragment
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.utils.PerformanceMonitor
import com.xcjh.app.utils.SoundManager
import com.xcjh.app.utils.getVerCode
import com.xcjh.app.utils.getVerName
import com.xcjh.app.utils.judgeLogin
import com.xcjh.app.vm.MainVm
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.listener.NoReadMsgPushListener
import com.xcjh.app.websocket.listener.OtherPushListener
import com.xcjh.base_lib.App
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.initActivity
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.setOnclickNoRepeat
import com.xcjh.base_lib.utils.view.clickNoRepeat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit


/**
 * 版本2 主页
 *
 */
class MainActivity : BaseActivity<MainVm, ActivityHomeBinding>() {
    // 创建 Timer 对象并调度定时任务 1分钟刷新一下数据
    private var timer: Timer? = null
    private val delay: Long = 0  // 延迟时间，单位为毫秒
    private val period: Long = 1 * 60 * 1000 // 执行间隔时间，单位为毫秒（这里设置为1分钟）
    private var mAppUpdater: AppUpdater? = null
    private var currentPage: Int = 0
    private var popup: BasePopupView? = null
    private var exitTime: Long = 0
    //当首页获取到数据就是true
    private var isHomeDate:Boolean=false
    //是否显示卡片
    private var isShowPush: Boolean = true

    // 创建 Timer 对象并调度定时任务 10秒刷新一下
    private var timerDetails: Timer? = null

    private var mFragList: ArrayList<Fragment> = arrayListOf(
        HomeFragment(),
        ScheduleFragment(),//赛程
        MsgFragment(),
        MyUserFragment(),
    )


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        //MTPushPrivatesApi.clearNotification(this)
//        placeLoginDialog()
        showStatusBar()




        //初始化提示音类
        SoundManager.initialize(appContext)

        mDatabind.reDateShow.clickNoRepeat {}
        currentPage=0
//        getApiService(ApiComService.SERVER_URL)
        //实时cpu
//     var   performanceMonitor = PerformanceMonitor(this)
//        performanceMonitor.startMonitoring()
//
//        // 注册 FrameCallback  获取FPS
//        Choreographer.getInstance().postFrameCallback(frameCallback)
//        //打开显示
//        if (PermissionUtils.checkPermission(this)) {
//            showAppFloat2("CPU")
//
//
//        }else{
//            AlertDialog.Builder(this)
//                .setMessage("使用浮窗功能，需要您授权悬浮窗权限。")
//                .setPositiveButton("去开启") { _, _ ->
//                    showAppFloat2("CPU")
//                }
//                .setNegativeButton("取消") { _, _ -> }
//                .show()
//        }





        //全部比赛 0全部 1 是足球   2是篮球    3是赛果
        TimeConstantsDat.options1ItemsAll = ArrayList<JsonBean>()
        TimeConstantsDat.options2ItemsAll =  ArrayList<ArrayList<String>>()
        TimeConstantsDat.options3ItemsAll = ArrayList< ArrayList<ArrayList<String>>>()
        //足球比赛
        TimeConstantsDat.options1ItemsFootball = ArrayList<JsonBean>()
        TimeConstantsDat.options2ItemsFootball =  ArrayList<ArrayList<String>>()
        TimeConstantsDat.options3ItemsFootball = ArrayList< ArrayList<ArrayList<String>>>()
        //篮球比赛
        TimeConstantsDat.options1ItemsBasketball = ArrayList<JsonBean>()
        TimeConstantsDat.options2ItemsBasketball =  ArrayList<ArrayList<String>>()
        TimeConstantsDat.options3ItemsBasketball = ArrayList< ArrayList<ArrayList<String>>>()
        //赛果
        TimeConstantsDat.options1ItemsSaiguo  = ArrayList<JsonBean>()
        TimeConstantsDat.options2ItemsSaiguo =  ArrayList<ArrayList<String>>()
        TimeConstantsDat.options3ItemsSaiguo = ArrayList< ArrayList<ArrayList<String>>>()

        //收到通知其他地方登录
        appViewModel.quitTipsEvent.observeForever {

            if(CacheUtil.isLogin()){
                CacheUtil.setIsLogin(false, LoginInfo("","", ""))
//                if(isActivityRunning(this, WebActivity::class.java)){
//                    appViewModel.webEvent.postValue(true)
//                }else{
//                    placeLoginDialogNew()
//                }
//                com.xcjh.app.placeLoginDialog(this)
                GlobalScope.launch(Dispatchers.Main) { // 使用主线程的调度器
                    delay(500L) // 延迟1秒（1000毫秒）
                    placeLoginDialog(this@MainActivity)
//
                }

            }

        }

        //语言 0是中文  1是繁体  2是英文
        val locale = MultiLanguages.getAppLanguage(this)
        if(LocaleContract.getSimplifiedChineseLocale().equals(locale)|| LocaleContract.getChineseLocale().equals(locale)||locale.toString().equals("zh_CN_#Hans")){
            Constants.languageType=0
        }else if(LocaleContract.getTraditionalChineseLocale().equals(locale)){
            Constants.languageType=1
        }else{
            Constants.languageType=2
        }
        if(CacheUtil.isLogin()){
            mViewModel.setLanquage()
        }


        /* splashScreen.setKeepOnScreenCondition {
             //延迟2.5秒
             !mViewModel.mockDataLoading()
         }*/
        mDatabind.vLogoAnim.setAnimation("qidongye.json")
        lifecycleScope.launch {
            delay(500) // 延迟 10 秒
            mDatabind.vLogoAnim.playAnimation()
        }


        Constants.isLoading = true
        onIntent(intent)
        CacheUtil.setFirst(false)
        initUI()
        initTime()
        initWs()
        //极光推送绑定用户
        val registrationId: String = MTCorePrivatesApi.getRegistrationId(this)
        Log.i("============","======"+registrationId)


    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        onIntent(intent)
    }



    private fun onIntent(intent: Intent?) {
        try {
            Log.i("push===UserReceiver", "onIntent-extras:${intent?.extras?.toString()}")
            Log.i("push===UserReceiver", "onIntent-data:${intent?.data?.toString()}")

            if (intent == null) {
                return
            }
            if (intent.extras == null) {
                return
            }
            intent.extras?.apply {
                val matchId = getString("matchId", "")
                Log.i("push===UserReceiver", "matchId:${matchId}")
                val isPureFlow = getString("isPureFlow")
                val matchType = getString("matchType", "1")
                val liveId = getString("liveId")
                val anchorId = getString("anchorId", null)
                if (!matchId.isNullOrEmpty()) {
                    MatchDetailActivity.open(
                        matchType = matchType,
                        matchId = matchId,
                        anchorId = anchorId
                    )
                }
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    private fun initUI() {


        MTPushPrivatesApi.setNotificationBadge(this, 0)
        //检查app是否更新
        mViewModel.appUpdate()
        //runOnUiThread {  }
        //初始化viewpager2
        mDatabind.viewPager.initActivity(this, mFragList, false)
        mDatabind.viewPager.offscreenPageLimit = mFragList.size


        setOnclickNoRepeat(
            mDatabind.llHomeSelectMain, mDatabind.llHomeSelectSchedule,
            mDatabind.llHomeSelectMsg, mDatabind.llHomeSelectMine, interval = 200
        ) {
            when (it.id) {
                R.id.llHomeSelectMain -> {
                    changeTab(0)
//                    ToastUtli().oppenWeb(this)

                }

                R.id.llHomeSelectSchedule -> {
                    changeTab(1)

                }

                R.id.llHomeSelectMsg -> {

                    judgeLogin {
                        appViewModel.appMsgResum.postValue(true)
                        changeTab(2)
                    }
                }

                R.id.llHomeSelectMine -> {
                    if (popup != null) {
                        if (popup!!.isShow) {
                            popup!!.dismiss()
                        }
                    }
                    changeTab(3)
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - exitTime > 2000) {
                    myToast(getString(R.string.exit_app))
                    exitTime = System.currentTimeMillis()
                } else {
                    finish()
                }
            }
        })
        mDatabind.ivHomeCourse.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.tab_saicheng_no))
        mDatabind.ivHomeMsg.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.tab_xiaoxi_no))
        mDatabind.ivHomeMy.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.tab_wode_no))

    }



    private fun initTime() {
        //如果登录了就查询一下用户信息
        if (CacheUtil.isLogin()) {
            mViewModel.getUserInfo()
            mViewModel.jPushBind(MTCorePrivatesApi.getRegistrationId(this))
        }
        // 创建 Timer 对象
        timer = Timer()

        // 定义要执行的操作
        val task = object : TimerTask() {
            override fun run() {
                appViewModel.appPolling.postValue(true)
            }
        }

        // 安排 TimerTask 在一定时间后开始执行，然后每隔一定时间重复执行
        timer?.schedule(task, delay, period)
    }

    private fun initWs() {
        MyWsManager.getInstance(this)?.initService()
        MyWsManager.getInstance(this)?.setNoReadMsgListener(javaClass.name, object :
            NoReadMsgPushListener {
            override fun onNoReadMsgNums(nums: String) {
                super.onNoReadMsgNums(nums)
                //  initMsgNums(nums)
            }
        })
        //获取到要推送的比赛
        MyWsManager.getInstance(this)?.setOtherPushListener(javaClass.name, object :
            OtherPushListener {
            override fun onAnchorStartLevel(beingLiveBean: BeingLiveBean) {
                super.onAnchorStartLevel(beingLiveBean)
                if (currentPage != 3 && CacheUtil.isLogin()&&isHomeDate) {
                    Log.i("BBBB","=="+Gson().toJson(beingLiveBean))
                    showDialog(beingLiveBean)
                }
            }


        })

        //被挤下线
        MyWsManager.getInstance(this)?.setNoReadMsgListener(javaClass.name, object :NoReadMsgPushListener{
            override fun onUserIsKicked() {
                super.onUserIsKicked()
             if(CacheUtil.isLogin()){
                    appViewModel.quitTipsEvent.postValue(true)
                }
            }
        })

    }

    override fun createObserver() {
        super.createObserver()
        appViewModel.mainDateShowEvent.observeForever {
            mDatabind.vLogoAnim.cancelAnimation()
            mDatabind.reDateShow.visibility=View.GONE
            isHomeDate=true
            //如果登录了就查询一下用户信息
            if (CacheUtil.isLogin()) {
                //判断打开app的时候是否获取到了数据
                if(!mViewModel.isGetUserDate){
                    mViewModel.getUserInfo()
                }
                if(!mViewModel.isPushDate){
                    Log.i("SSSSSSs","========="+MTCorePrivatesApi.getRegistrationId(this))
                    mViewModel.jPushBind(MTCorePrivatesApi.getRegistrationId(this))
                }

            }
        }

        //登录或者登出
        appViewModel.updateLoginEvent.observeForever() {
            if (it) {
                mViewModel.getUserInfo()
            }
        }
        /**
         * 暂时隐藏获取到线上最新版本然后升级
         */
        mViewModel.update.observe(this) {
//            var code= getVerCode(this).toString()
            var code= getVerName(this).toString()
//            appUpdate(it.remarks,false,"")
            if(!it.version.equals(code)){
                // 是否强制更新：0 ：不强制 1：强制
                if(it.forcedUpdate.equals("0")){
                    appUpdate(it.remarks,true,it.sourceUrl)
                }else{
                    appUpdate(it.remarks,false,it.sourceUrl)
                }


            }
        }

        appViewModel.mainViewPagerEvent.observe(this) {
            //切换到首页并且要进入推荐
            if (it == -1) {
                changeTab(0, false)
//                mDatabind.magicIndicator.onPageSelected(0)
//                mDatabind.viewPager.currentItem = 0
                appViewModel.homeViewPagerEvent.value = 0
            }

        }

        appViewModel.updateMainMsgNum.observeForever {
            initMsgNums(it)
        }

        //  startNewActivity<MatchDetailActivity> {  }
    }

    /**
     * app升级
     * content 内容,
     * isForce  如果是false就是强制  true就是不强制,
     * url   下载地址
     */
    @SuppressLint("MissingInflatedId")
    private fun appUpdate(content: String, isForce: Boolean, url: String) {
        var view = LayoutInflater.from(this).inflate(R.layout.dialog_app_update_tips, null)
        var txtAppContent = view.findViewById<AppCompatTextView>(R.id.txtAppContent)
        var ivDelete = view.findViewById<AppCompatImageView>(R.id.ivDelete)
        var txtUpdateCommit = view.findViewById<AppCompatTextView>(R.id.txtUpdateCommit)
        //显示进度条
        var llShow = view.findViewById<ConstraintLayout>(R.id.llShow)
        var pbUpdate = view.findViewById<ProgressBar>(R.id.pbUpdate)
        var txtUpdateNum = view.findViewById<AppCompatTextView>(R.id.txtUpdateNum)


        txtAppContent.text = content
        if (!isForce) {
            ivDelete.visibility = View.GONE
        } else {
            ivDelete.visibility = View.VISIBLE
        }
        ivDelete.clickNoRepeat {
            AppDialog.INSTANCE.dismissDialog()
        }
        txtUpdateCommit.clickNoRepeat {
//            mAppUpdater!!=AppUpdater.
            txtUpdateCommit.visibility=View.GONE
            llShow.visibility=View.VISIBLE
     //   .setUrl("https://gitlab.com/jenly1314/AppUpdater/-/raw/master/app/release/app-release.apk") .setUrl(url)
            mAppUpdater = AppUpdater.Builder(this)
                .setUrl(url)
                .setNotificationIcon(R.drawable.app_logo)
                .setVersionCode(BuildConfig.VERSION_CODE.toLong())
                .setFilename("AppUpdater.apk")
                .setVibrate(true)
                .build()
            //                        .setApkMD5("3df5b1c1d2bbd01b4a7ddb3f2722ccca")// 支持MD5校验，如果缓存APK的MD5与此MD5相同，则直接取本地缓存安装，推荐使用MD5校验的方式
            mAppUpdater!!.setHttpManager(OkHttpManager.getInstance())
                .setUpdateCallback(object :UpdateCallback{
                    override fun onDownloading(isDownloading: Boolean) {

                    }

                    override fun onStart(url: String?) {
                        pbUpdate.progress = 0
                        txtUpdateNum.text= "0%"
                    }

                    override fun onProgress(progress: Long, total: Long, isChanged: Boolean) {
                        if (isChanged) {
                            if (pbUpdate == null || pbUpdate == null) {
                                return
                            }
                            if (progress > 0) {
                                val currProgress = (progress * 1.0f / total * 100.0f).toInt()
                                txtUpdateNum.text=(currProgress.toString() + "%")
                                pbUpdate.progress = currProgress
                            } else {
                                txtUpdateNum.text= "0%"
                            }

                        }
                    }

                    override fun onFinish(file: File?) {
                            //下载完成
                        txtUpdateCommit.visibility=View.VISIBLE
                        llShow.visibility=View.GONE
                    }

                    override fun onError(e: Exception?) {
                        //下载失败
                        txtUpdateCommit.visibility=View.VISIBLE
                        llShow.visibility=View.GONE

                    }

                    override fun onCancel() {

                    }

                }).start()
            if(!isForce){
                AppDialog.INSTANCE.dismissDialog()
            }
//
        }
        //false就是强制
        AppDialog.INSTANCE.showDialog(this, view, false)

    }

    private fun initMsgNums(nums: String) {
        when (nums.toInt()) {
            0 -> {
                mDatabind.tvnums.visibility = View.GONE
                mDatabind.tvnums2.visibility = View.GONE
            }

            in 1..9 -> {
                mDatabind.tvnums.text = nums
                mDatabind.tvnums.visibility = View.VISIBLE
                mDatabind.tvnums2.visibility = View.GONE
            }

            in 10..99 -> {
                mDatabind.tvnums2.text = nums
                mDatabind.tvnums.visibility = View.GONE
                mDatabind.tvnums2.visibility = View.VISIBLE
            }

            else -> {
                mDatabind.tvnums2.text = "99+"
                mDatabind.tvnums.visibility = View.GONE
                mDatabind.tvnums2.visibility = View.VISIBLE
            }
        }
    }

    private fun changeTab(pos: Int = 0, vibrate: Boolean = true) {
        if (currentPage != pos && vibrate) {
            if (CacheUtil.isNavigationVibrate()) {
                vibrate(this)
            }
            SoundManager.playMedia()
        }

        if (pos == 0&&currentPage != pos){
            mDatabind.ivHomeMain.setAnimation("tab_shouye_icon.json")
            mDatabind.ivHomeMain.playAnimation()
            mDatabind.ivHomeCourse.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.tab_saicheng_no))
            mDatabind.ivHomeMsg.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.tab_xiaoxi_no))
            mDatabind.ivHomeMy.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.tab_wode_no))
            mDatabind.ivHomeCourse.cancelAnimation()
            mDatabind.ivHomeMsg.cancelAnimation()
            mDatabind.ivHomeMy.cancelAnimation()
        }else if (pos == 1&&currentPage != pos){
            mDatabind.ivHomeCourse.setAnimation("tab_saicheng_icon.json")
            mDatabind.ivHomeCourse.playAnimation()
            mDatabind.ivHomeMain.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.tab_main_no))
            mDatabind.ivHomeMsg.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.tab_xiaoxi_no))
            mDatabind.ivHomeMy.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.tab_wode_no))

            mDatabind.ivHomeMain.cancelAnimation()
            mDatabind.ivHomeMsg.cancelAnimation()
            mDatabind.ivHomeMy.cancelAnimation()
        }else if (pos == 2&&currentPage != pos){
            mDatabind.ivHomeMsg.setAnimation("tab_xiaoxi_icon.json")
            mDatabind.ivHomeMsg.playAnimation()
            mDatabind.ivHomeMain.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.tab_main_no))
            mDatabind.ivHomeCourse.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.tab_saicheng_no))
            mDatabind.ivHomeMy.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.tab_wode_no))
            mDatabind.ivHomeMain.cancelAnimation()
            mDatabind.ivHomeCourse.cancelAnimation()
            mDatabind.ivHomeMy.cancelAnimation()
        }else if (pos == 3&&currentPage != pos){
            mDatabind.ivHomeMy.setAnimation("tab_wode_icon.json")
            mDatabind.ivHomeMy.playAnimation()
            mDatabind.ivHomeMain.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.tab_main_no))
            mDatabind.ivHomeCourse.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.tab_saicheng_no))
            mDatabind.ivHomeMsg.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.tab_xiaoxi_no))
            mDatabind.ivHomeMain.cancelAnimation()
            mDatabind.ivHomeCourse.cancelAnimation()
            mDatabind.ivHomeMsg.cancelAnimation()
        }


        mDatabind.txtHome.setTextColor(
            ContextCompat.getColor(
                this,
                if (pos == 0) R.color.c_37373d else R.color.c_aeb4ba
            )
        )
        mDatabind.txtHomeSchedule.setTextColor(
            ContextCompat.getColor(
                this,
                if (pos == 1) R.color.c_37373d else R.color.c_aeb4ba
            )
        )
        mDatabind.txtHomeMsg.setTextColor(
            ContextCompat.getColor(
                this,
                if (pos == 2) R.color.c_37373d else R.color.c_aeb4ba
            )
        )
        mDatabind.txtHomeMine.setTextColor(
            ContextCompat.getColor(
                this,
                if (pos == 3) R.color.c_37373d else R.color.c_aeb4ba
            )
        )

//        mDatabind.ivHomeMain.setImageDrawable(
//            ContextCompat.getDrawable(
//                this, if (pos == 0) R.drawable.tab_main_select else R.drawable.tab_main_no
//            )
//        )
//        mDatabind.ivHomeCourse.setImageDrawable(
//            ContextCompat.getDrawable(
//                this, if (pos == 1) R.drawable.tab_saicheng_select else R.drawable.tab_saicheng_no
//            )
//        )
//        mDatabind.ivHomeMsg.setImageDrawable(
//            ContextCompat.getDrawable(
//                this, if (pos == 2) R.drawable.tab_xiaoxi_select else R.drawable.tab_xiaoxi_no
//            )
//        )
//        mDatabind.ivHomeMy.setImageDrawable(
//            ContextCompat.getDrawable(
//                this, if (pos == 3) R.drawable.tab_wode_select else R.drawable.tab_wode_no
//            )
//        )





        currentPage = pos
        mDatabind.viewPager.setCurrentItem(pos, false)

    }

    /**
     * 内部推送
     */
    fun showDialog(beingLiveBean: BeingLiveBean) {
        var pushCardPopup = PushCardPopup(this, beingLiveBean)
        if (popup != null) {
            if (popup!!.isShow) {
                return
            }
        }


        if (CacheUtil.isAnchorVibrate()) {
            vibrate(this)
        }

        popup = XPopup.Builder(this)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.TranslateFromTop)
            .offsetY(90)
            .hasShadowBg(false)
            .isTouchThrough(true)
            .isLightStatusBar(true)
            .dismissOnTouchOutside(false)
            .asCustom(pushCardPopup)
            .show()
        pushCardPopup.pushCardPopupListener = object : PushCardPopup.PushCardPopupListener {
            override fun clicktClose() {
                popup!!.dismiss()
            }

            override fun selectGoto(beingLiveBean: BeingLiveBean) {
                popup!!.dismiss()
                MatchDetailActivity.open(
                    matchType = beingLiveBean.matchType, matchId = beingLiveBean.matchId,
                    matchName = "${beingLiveBean.homeTeamName}VS${beingLiveBean.awayTeamName}",
                    anchorId = beingLiveBean.userId
                )

            }

        }
//        if(popup!=null){
//            if( popup!!.isShow){
//                Handler(Looper.getMainLooper()).postDelayed(
//                    Runnable {
//                        if(popup!=null){
//                            if( popup!!.isShow){
//                                popup!!.dismiss()
//                            }
//                        }
//                    }, 3000)
//            }
//        }
    }

    /**
     * 颤动
     */
    private fun vibrate(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0及以上版本可以使用VibrationEffect来定义震动模式
            val amplitude = 30 // 自定义震动强度（0-255）
            val duration: Long = 100 // 震动持续时间（毫秒）
            val effect = VibrationEffect.createOneShot(duration, amplitude)
            vibrator.vibrate(effect)
        } else {
            // Android 7.0及以下版本可以使用常规的震动模式
            vibrator.vibrate(100)
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        isShowPush = true

     }

    override fun onStop() {
        super.onStop()
        isShowPush = false
        if (popup != null) {
            if (popup!!.isShow) {
                popup!!.dismiss()
            }
        }
    }

    override fun onDestroy() {
        //MyWsManager.getInstance(this)?.stopService()
        // 销毁 Timer 对象
        if (timer != null) {
            timer?.cancel()
            timer?.purge()
        }
        MyWsManager.getInstance(this)?.stopService()
        MTPushPrivatesApi.setNotificationBadge(this, 0)
        super.onDestroy()
    }



    fun isActivityRunning(context: Context, activityClass: Class<out Activity>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningTasks = activityManager.getRunningTasks(Int.MAX_VALUE)

        for (taskInfo in runningTasks) {
            val topActivity = taskInfo.topActivity
            if (topActivity?.className == activityClass.name) {
                return true
            }
        }
        return false
    }
//    var placeDialog: CustomDialog?=null
//
//    /***
//     * 异地登录弹出
//     */
//    fun placeLoginDialog(context: Context) {
//        //判断现在是否在登录状态
//        placeDialog= CustomDialog.build()
//            .setCustomView(object : OnBindView<CustomDialog?>(R.layout.layout_dialogx_delmsg_new) {
//                override fun onBind(dialog: CustomDialog?, v: View) {
//                    val tvcancle = v.findViewById<TextView>(R.id.tvcancle)
//                    val textName = v.findViewById<TextView>(R.id.textName)
//                    val tvsure = v.findViewById<TextView>(R.id.tvsure)
//                    val viewGen = v.findViewById<View>(R.id.viewGen)
////                //语言 0是中文  1是繁体  2是英文
////                if( Constants.languageType==0){
////                    Context
////                }
//
//                    textName.text= context.getString(R.string.place_txt_login)
//                    tvsure.text= context.getString(R.string.ensure)
//                    tvcancle.visibility= View.GONE
//                    viewGen.visibility= View.GONE
//                    tvsure.setOnClickListener {
////                    appViewModel.quitLoginEvent.postValue(true)
//                        com.xcjh.base_lib.utils.startNewActivity<LoginActivity> {}
//                        GlobalScope.launch(Dispatchers.Main) { // 使用主线程的调度器
//                            delay(500L) // 延迟1秒（1000毫秒）
//                            appViewModel.mainViewPagerEvent.postValue(-1)
//                            appViewModel.quitLoginEvent.postValue(true)
//                        }
//                        placeDialog?.dismiss()
//
//                    }
//                }
//            }).setAlign(CustomDialog.ALIGN.CENTER).setCancelable(false).
//            setMaskColor(//背景遮罩
//                ContextCompat.getColor(context, com.xcjh.base_lib.R.color.blacks_tr)
//
//            )
//        if( !placeDialog?.isShow!!){
//            placeDialog?.show()
//        }
//    }

    /**
     * 显示cpu
     */
    private fun showAppFloat2(tag: String) {
        EasyFloat.with(this.applicationContext)
            .setTag(tag)
            .setShowPattern(ShowPattern.FOREGROUND)
            .setLocation(100, 100)
            .setAnimator(null)
            .setLayout(R.layout.float_app_scale) {
                val txtCpu = it.findViewById<TextView>(R.id.txtCpu)
                val txtNpc = it.findViewById<TextView>(R.id.txtNpc)
                val txtFPS = it.findViewById<TextView>(R.id.txtFPS)
                it.findViewById<ImageView>(R.id.ivClose).setOnClickListener {
                    EasyFloat.dismiss(tag)
                }
                appViewModel.cpuEvent.observeForever {
                    if(txtCpu!=null){
                        txtCpu.text="CPU："+it.cpu
                        txtNpc.text="内存："+it.memory
                    }

                }

                appViewModel.fpsEvent.observeForever {
                    if(txtFPS!=null){
                        txtFPS.text="FPS："+it
                    }

                }

            }.show()

    }
    private var lastFrameTimeNanos: Long = 0
    private var frameCount = 0
    private val FRAME_RATE_INTERVAL = TimeUnit.SECONDS.toNanos(1) // 输出帧率的时间间隔为1秒
    private val frameCallback = object : Choreographer.FrameCallback {
        private var lastUpdateTimeNanos = System.nanoTime()

        override fun doFrame(frameTimeNanos: Long) {
            // 计算帧间隔时间
            if (lastFrameTimeNanos != 0L) {
                val frameIntervalNanos = frameTimeNanos - lastFrameTimeNanos
                val frameRate = TimeUnit.SECONDS.toNanos(1) / frameIntervalNanos.toDouble()

                // 获取当前时间
                val currentTimeNanos = System.nanoTime()

                // 检查是否到达输出时间间隔
                if (currentTimeNanos - lastUpdateTimeNanos >= FRAME_RATE_INTERVAL) {
                    // 将浮点数帧率转换为整数
                    val roundedFrameRate = frameRate.toInt()
//                    Log.d(TAG, "Frame rate: $roundedFrameRate")
                    appViewModel.fpsEvent.postValue(roundedFrameRate.toString())
                    lastUpdateTimeNanos = currentTimeNanos // 更新上次输出时间
                }
            }

            // 更新上一帧时间
            lastFrameTimeNanos = frameTimeNanos

            // 继续注册下一帧回调
            Choreographer.getInstance().postFrameCallback(this)
        }
    }




}