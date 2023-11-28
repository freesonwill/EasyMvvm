package com.xcjh.app.ui


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.gson.Gson
import com.king.app.dialog.AppDialog
import com.king.app.updater.AppUpdater
import com.king.app.updater.http.OkHttpManager
import com.xcjh.app.BuildConfig
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.databinding.ActivityHomeBinding
import com.xcjh.app.ui.home.home.HomeFragment
import com.xcjh.app.ui.home.msg.MsgFragment
import com.xcjh.app.ui.home.my.MyUserFragment
import com.xcjh.app.ui.home.schedule.ScheduleFragment
import com.xcjh.app.ui.login.LoginActivity
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.utils.judgeLogin
import com.xcjh.app.vm.MainVm
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.SendCommonWsBean
import com.xcjh.base_lib.utils.initActivity
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.view.clickNoRepeat
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*


/**
 * 版本2 主页
 *
 */
class MainActivity : BaseActivity<MainVm, ActivityHomeBinding>() {
    // 创建 Timer 对象并调度定时任务
    private var timer: Timer? = null
    val delay: Long = 0  // 延迟时间，单位为毫秒
    val period: Long = 1 * 60 * 1000 // 执行间隔时间，单位为毫秒（这里设置为1分钟）
    private var mAppUpdater: AppUpdater? = null
    private var currentPage:Int=0


    private var mFragList: ArrayList<Fragment> = arrayListOf(
        HomeFragment(),
        ScheduleFragment(),//赛程
        MsgFragment(),
        MyUserFragment(),
    )


    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

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

        //点击首页
        mDatabind.txtHome.setOnClickListener {
            if(currentPage!=0){
                vibrate(this)
            }
            setHome(0)
        }
        //点击赛程
        mDatabind.txtHomeSchedule.setOnClickListener {
            if(currentPage!=1){
                vibrate(this)
            }
            setHome(1)
        }
        //点击消息
        mDatabind.txtHomeMsg.setOnClickListener {
            judgeLogin {
                if(currentPage!=2){
                    vibrate(this)
                }
                setHome(2)
            }
        }
        //点击我的
        mDatabind.txtHomeMine.setOnClickListener {
            if(currentPage!=3){
                vibrate(this)
            }
            setHome(3)
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
            if (it>0){
                mDatabind.tvnums.text=it.toString()
                mDatabind.tvnums.visibility=View.VISIBLE
            }else{
                mDatabind.tvnums.visibility=View.GONE
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
            val amplitude =30 // 自定义震动强度（0-255）
            val  duration:Long = 100 // 震动持续时间（毫秒）
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
        currentPage=page
        if (page == 0) {
            mDatabind.txtHome.setTextColor(ContextCompat.getColor(this, R.color.c_f5f5f5))
            mDatabind.txtHomeSchedule.setTextColor(ContextCompat.getColor(this, R.color.c_8e8c9d))
            mDatabind.txtHomeMsg.setTextColor(ContextCompat.getColor(this, R.color.c_8e8c9d))
            mDatabind.txtHomeMine.setTextColor(ContextCompat.getColor(this, R.color.c_8e8c9d))
        } else if (page == 1) {
            mDatabind.txtHome.setTextColor(ContextCompat.getColor(this, R.color.c_8e8c9d))
            mDatabind.txtHomeSchedule.setTextColor(ContextCompat.getColor(this, R.color.c_f5f5f5))
            mDatabind.txtHomeMsg.setTextColor(ContextCompat.getColor(this, R.color.c_8e8c9d))
            mDatabind.txtHomeMine.setTextColor(ContextCompat.getColor(this, R.color.c_8e8c9d))
        } else if (page == 2) {
            mDatabind.txtHome.setTextColor(ContextCompat.getColor(this, R.color.c_8e8c9d))
            mDatabind.txtHomeSchedule.setTextColor(ContextCompat.getColor(this, R.color.c_8e8c9d))
            mDatabind.txtHomeMsg.setTextColor(ContextCompat.getColor(this, R.color.c_f5f5f5))
            mDatabind.txtHomeMine.setTextColor(ContextCompat.getColor(this, R.color.c_8e8c9d))
        } else if (page == 3) {
            mDatabind.txtHome.setTextColor(ContextCompat.getColor(this, R.color.c_8e8c9d))
            mDatabind.txtHomeSchedule.setTextColor(ContextCompat.getColor(this, R.color.c_8e8c9d))
            mDatabind.txtHomeMsg.setTextColor(ContextCompat.getColor(this, R.color.c_8e8c9d))
            mDatabind.txtHomeMine.setTextColor(ContextCompat.getColor(this, R.color.c_f5f5f5))
        }
        mDatabind.viewPager.currentItem = page
    }


}