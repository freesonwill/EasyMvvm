package com.xcjh.app.ui


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.engagelab.privates.core.api.MTCorePrivatesApi
import com.engagelab.privates.push.api.MTPushPrivatesApi
import com.google.gson.Gson
import com.gyf.immersionbar.ktx.showStatusBar
import com.king.app.dialog.AppDialog
import com.king.app.updater.AppUpdater
import com.king.app.updater.http.OkHttpManager
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.xcjh.app.BuildConfig
import com.xcjh.app.R
import com.xcjh.app.adapter.PushCardPopup
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.databinding.ActivityHomeBinding
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.app.ui.home.home.HomeFragment
import com.xcjh.app.ui.home.msg.MsgFragment
import com.xcjh.app.ui.home.my.MyUserFragment
import com.xcjh.app.ui.home.schedule.ScheduleFragment
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.utils.judgeLogin
import com.xcjh.app.vm.MainVm
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.listener.NoReadMsgPushListener
import com.xcjh.app.websocket.listener.OtherPushListener
import com.xcjh.base_lib.utils.initActivity
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.setOnclickNoRepeat
import com.xcjh.base_lib.utils.view.clickNoRepeat
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*


/**
 * 版本2 主页
 *
 */
class MainActivity : BaseActivity<MainVm, ActivityHomeBinding>() {
    // 创建 Timer 对象并调度定时任务 1分钟刷新一下数据
    private var timer: Timer? = null
    val delay: Long = 0  // 延迟时间，单位为毫秒
    val period: Long = 1 * 60 * 1000 // 执行间隔时间，单位为毫秒（这里设置为1分钟）
    private var mAppUpdater: AppUpdater? = null
    private var currentPage: Int = 0
    var popup: BasePopupView? = null

    //是否显示卡片
    var isShowPush: Boolean = true

    private var mFragList: ArrayList<Fragment> = arrayListOf(
        HomeFragment(),
        ScheduleFragment(),//赛程
        MsgFragment(),
        MyUserFragment(),
    )

    @SuppressLint("SuspiciousIndentation")
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        //MTPushPrivatesApi.clearNotification(this)
        showStatusBar()
       /* splashScreen.setKeepOnScreenCondition {
            //延迟2.5秒
            !mViewModel.mockDataLoading()
        }*/
        onIntent(intent)
        MTPushPrivatesApi.setNotificationBadge(this, 0)
        CacheUtil.setFirst(false)

        mViewModel.appUpdate()
        //runOnUiThread {  }
        //初始化viewpager2
        mDatabind.viewPager.initActivity(this, mFragList, false)

        val pageChangeCallback: OnPageChangeCallback = object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewPager.post(Runnable {
                    // 立即切换页面，取消动画
                    val currentItem: Int = viewPager.currentItem
                    viewPager.setCurrentItem(currentItem, false)
                })
            }
        }
        mDatabind.viewPager.registerOnPageChangeCallback(pageChangeCallback)

        setOnclickNoRepeat(
            mDatabind.llHomeSelectMain, mDatabind.llHomeSelectSchedule,
            mDatabind.llHomeSelectMsg, mDatabind.llHomeSelectMine
        ) {
            when (it.id) {
                R.id.llHomeSelectMain -> {
                    if (currentPage != 0) {
                        if (CacheUtil.isNavigationVibrate()) {
                            vibrate(this)
                        }

                    }

                    setHome(0)
                }
                R.id.llHomeSelectSchedule -> {
                    if (currentPage != 1) {
                        if (CacheUtil.isNavigationVibrate()) {
                            vibrate(this)

                        }
                    }
                    setHome(1)
                }
                R.id.llHomeSelectMsg -> {
                    judgeLogin {
                        if (currentPage != 2) {
                            if (CacheUtil.isNavigationVibrate()) {
                                vibrate(this)

                            }
                        }
                        setHome(2)
                    }
                }
                R.id.llHomeSelectMine -> {
                    if (popup != null) {
                        if (popup!!.isShow) {
                            popup!!.dismiss()
                        }
                    }

                    if (currentPage != 3) {
                        if (CacheUtil.isNavigationVibrate()) {
                            vibrate(this)

                        }
                    }
                    setHome(3)
                }
            }

        }


        mDatabind.viewPager.offscreenPageLimit = mFragList.size
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
        MyWsManager.getInstance(this)?.initService()
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

        appViewModel.updateMainMsgNum.observeForever {
            initMsgNums(it)
        }
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
                if (currentPage != 3 && CacheUtil.isLogin()) {
                    showDialog(beingLiveBean)
                }

            }
        })


    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        onIntent(intent)
    }

    private fun onIntent(intent: Intent?) {
        try {
            Log.i("push===UserReceiver", "onIntent:${intent?.extras?.toString()}")
            Log.i("push===UserReceiver", "onIntentgson:${Gson().toJson(intent?.extras)}")
            val notificationMessage = intent?.getStringExtra("message_json")
            Log.i("push===UserReceiver", "message_json:${notificationMessage}")
            if (intent == null) {
                return
            }
            if (intent.extras == null) {
                return
            }
            intent.extras?.apply {
                val matchId = getString("matchId", "")
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

    fun initMsgNums(nums: String) {
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
                mDatabind.tvnums.text = nums
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

    public override fun onStart() {
        super.onStart()
    }


    fun vibrate(context: Context) {
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

    public override fun onDestroy() {
        //MyWsManager.getInstance(this)?.stopService()
        // 销毁 Timer 对象
        if (timer != null) {
            timer?.cancel()
            timer?.purge()
        }
        MTPushPrivatesApi.setNotificationBadge(this, 0)
        super.onDestroy()
    }

    private var exitTime: Long = 0

    override fun createObserver() {
        super.createObserver()
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
//            var code= getVerCode(this).toInt()
//            if(it.version.toInt()>code){
//                appUpdate(it.remarks,true,"")
//            }

        }

        appViewModel.mainViewPagerEvent.observe(this) {
            //切换到首页并且要进入推荐
            if (it == -1) {
                setHome(0)
//                mDatabind.magicIndicator.onPageSelected(0)
//                mDatabind.viewPager.currentItem = 0
                appViewModel.homeViewPagerEvent.value = 0
            }

        }


        //  startNewActivity<MatchDetailActivity> {  }
    }

    /**
     * app升级
     * content 内容,
     * isForce  如果是false就是强制  true就是不强制,
     * url   下载地址
     */
    fun appUpdate(content: String, isForce: Boolean, url: String) {
        var view = LayoutInflater.from(this).inflate(R.layout.dialog_app_update_tips, null)
        var txtAppContent = view.findViewById<AppCompatTextView>(R.id.txtAppContent)
        var txtUpdateCancel = view.findViewById<AppCompatTextView>(R.id.txtUpdateCancel)
        var txtUpdateCommit = view.findViewById<AppCompatTextView>(R.id.txtUpdateCommit)

        txtAppContent.text = content
        if (!isForce) {
            txtUpdateCancel.visibility = View.GONE
        } else {
            txtUpdateCancel.visibility = View.VISIBLE
        }
        txtUpdateCancel.clickNoRepeat {
            AppDialog.INSTANCE.dismissDialog()
        }
        txtUpdateCommit.clickNoRepeat {
//            mAppUpdater!!=AppUpdater.
            mAppUpdater = AppUpdater.Builder(this)
                .setUrl("https://gitlab.com/jenly1314/AppUpdater/-/raw/master/app/release/app-release.apk")
                .setVersionCode(BuildConfig.VERSION_CODE.toLong())
                .setFilename("AppUpdater.apk")
                .setVibrate(true)
                .build()
            //                        .setApkMD5("3df5b1c1d2bbd01b4a7ddb3f2722ccca")// 支持MD5校验，如果缓存APK的MD5与此MD5相同，则直接取本地缓存安装，推荐使用MD5校验的方式
            mAppUpdater!!.setHttpManager(OkHttpManager.getInstance()).start()
            AppDialog.INSTANCE.dismissDialog()
        }
        AppDialog.INSTANCE.showDialog(this, view, isForce)

    }

    fun setHome(page: Int) {
        currentPage = page
        if (page == 0) {
            mDatabind.txtHome.setTextColor(ContextCompat.getColor(this, R.color.c_37373d))
            mDatabind.txtHomeSchedule.setTextColor(ContextCompat.getColor(this, R.color.c_aeb4ba))
            mDatabind.txtHomeMsg.setTextColor(ContextCompat.getColor(this, R.color.c_aeb4ba))
            mDatabind.txtHomeMine.setTextColor(ContextCompat.getColor(this, R.color.c_aeb4ba))

            mDatabind.ivHomeMain.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tab_main_select
                )
            )
            mDatabind.ivHomeCourse.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tab_saicheng_no
                )
            )
            mDatabind.ivHomeMsg.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tab_xiaoxi_no
                )
            )
            mDatabind.ivHomeMy.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tab_wode_no
                )
            )

        } else if (page == 1) {
            mDatabind.txtHome.setTextColor(ContextCompat.getColor(this, R.color.c_aeb4ba))
            mDatabind.txtHomeSchedule.setTextColor(ContextCompat.getColor(this, R.color.c_37373d))
            mDatabind.txtHomeMsg.setTextColor(ContextCompat.getColor(this, R.color.c_aeb4ba))
            mDatabind.txtHomeMine.setTextColor(ContextCompat.getColor(this, R.color.c_aeb4ba))

            mDatabind.ivHomeMain.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tab_main_no
                )
            )
            mDatabind.ivHomeCourse.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tab_saicheng_select
                )
            )
            mDatabind.ivHomeMsg.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tab_xiaoxi_no
                )
            )
            mDatabind.ivHomeMy.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tab_wode_no
                )
            )
        } else if (page == 2) {
            mDatabind.txtHome.setTextColor(ContextCompat.getColor(this, R.color.c_aeb4ba))
            mDatabind.txtHomeSchedule.setTextColor(ContextCompat.getColor(this, R.color.c_aeb4ba))
            mDatabind.txtHomeMsg.setTextColor(ContextCompat.getColor(this, R.color.c_37373d))
            mDatabind.txtHomeMine.setTextColor(ContextCompat.getColor(this, R.color.c_aeb4ba))

            mDatabind.ivHomeMain.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tab_main_no
                )
            )
            mDatabind.ivHomeCourse.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tab_saicheng_no
                )
            )
            mDatabind.ivHomeMsg.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tab_xiaoxi_select
                )
            )
            mDatabind.ivHomeMy.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tab_wode_no
                )
            )
        } else if (page == 3) {
            mDatabind.txtHome.setTextColor(ContextCompat.getColor(this, R.color.c_aeb4ba))
            mDatabind.txtHomeSchedule.setTextColor(ContextCompat.getColor(this, R.color.c_aeb4ba))
            mDatabind.txtHomeMsg.setTextColor(ContextCompat.getColor(this, R.color.c_aeb4ba))
            mDatabind.txtHomeMine.setTextColor(ContextCompat.getColor(this, R.color.c_37373d))

            mDatabind.ivHomeMain.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tab_main_no
                )
            )
            mDatabind.ivHomeCourse.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tab_saicheng_no
                )
            )
            mDatabind.ivHomeMsg.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tab_xiaoxi_no
                )
            )
            mDatabind.ivHomeMy.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.tab_wode_select
                )
            )
        }
        mDatabind.viewPager.currentItem = page
    }

    fun showDialog(beingLiveBean: BeingLiveBean) {
        var pushCardPopup = PushCardPopup(this, beingLiveBean)
        if (popup != null) {
            if (popup!!.isShow) {
                popup!!.dismiss()
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


}