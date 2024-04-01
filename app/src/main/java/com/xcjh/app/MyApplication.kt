package com.xcjh.app

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.core.app.ActivityCompat.recreate
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.room.Room
import com.drake.statelayout.StateConfig
import com.engagelab.privates.core.api.MTCorePrivatesApi
import com.engagelab.privates.push.api.MTPushPrivatesApi
import com.hjq.language.LocaleContract
import com.hjq.language.MultiLanguages
import com.hjq.toast.Toaster
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.style.MaterialStyle
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.tencent.mmkv.MMKV
import com.xcjh.app.event.AppViewModel
import com.xcjh.app.event.EventViewModel
import com.xcjh.app.ui.room.MyRoomChatList
import com.xcjh.app.ui.room.MyRoomDataBase
import com.xcjh.app.view.callback.EmptyCallback
import com.xcjh.app.view.callback.LoadingCallback
import com.xcjh.base_lib.App
import com.xcjh.base_lib.BuildConfig
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.manager.KtxActivityManger
import java.util.Locale


//Application全局的ViewModel，里面存放了一些账户信息，基本配置信息等
/*val appViewModel: AppViewModel by lazy {
    appContext.getAppViewModelProvider()[AppViewModel::class.java]
}*/

//Application全局的ViewModel，里面存放了一些账户信息，基本配置信息等
val appViewModel: AppViewModel by lazy { MyApplication.appViewModelInstance }

//Application全局的ViewModel，用于发送全局通知操作
val eventViewModel: EventViewModel by lazy {
    MyApplication.eventViewModelInstance
}
/** 在 Activity 更改状态时初始化、加载和显示广告的应用程序类 */
class MyApplication : App() , LifecycleObserver{

    companion object {
        lateinit var dataBase: MyRoomDataBase
        lateinit var dataChatList: MyRoomChatList
        lateinit var appViewModelInstance: AppViewModel
        lateinit var eventViewModelInstance: EventViewModel

    }

    init {

        // 设置默认的语种（越早设置越好）
//        MultiLanguages.setDefaultLanguage(LocaleContract.getEnglishLocale())
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(appContext)

        appViewModelInstance = getAppViewModelProvider()[AppViewModel::class.java]
        eventViewModelInstance = getAppViewModelProvider()[EventViewModel::class.java]
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        //ijk内核，默认模式
        PlayerFactory.setPlayManager(IjkPlayerManager::class.java)
        initDataBase()
        initUI()
        initPush()
    }

    private fun loadBrandingTheme(languageCode: String) {
        // 根据语言代码生成对应的主题名称
        val themeName = "AppTheme_$languageCode" // 假设主题名称格式为 AppTheme_en、AppTheme_zh 等

        // 设置应用主题为对应语言的主题
        setTheme(getResId(themeName, R.style::class.java))


    }
    private fun getResId(resName: String, c: Class<*>): Int {
        try {
            val field = c.getField(resName)
            return field.getInt(null)
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
    }
    override fun attachBaseContext(base: Context?) {
        // 绑定语种
//        super.attachBaseContext(base)
        super.attachBaseContext(MultiLanguages.attach(base))
    }

    private fun initUI() {



        // 初始化语种切换框架
        MultiLanguages.init(this)

        //界面加载管理 初始化
        LoadSir.beginBuilder()
            .addCallback(LoadingCallback())//加载
            //.addCallback(ErrorCallback())//错误
            .addCallback(EmptyCallback())//空
            .setDefaultCallback(SuccessCallback::class.java)//设置默认加载状态页
            .commit()
        Toaster.init(this);

        /**
         *  推荐在Application中进行全局配置缺省页, 当然同样每个页面可以单独指定缺省页.
         *  具体查看 https://github.com/liangjingkanji/StateLayout
         */
        StateConfig.apply {
            emptyLayout = R.layout.layout_empty
            errorLayout = R.layout.layout_empty
            loadingLayout = R.layout.layout_state_loading
    //            setRetryIds(R.id.ivEmptyIcon, R.id.txtEmptyName)

            onLoading {
                // 此生命周期可以拿到LoadingLayout创建的视图对象, 可以进行动画设置或点击事件.
            }
        }
        /* PageRefreshLayout.refreshEnableWhenError = false
        PageRefreshLayout.refreshEnableWhenEmpty = false*/
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout -> MaterialHeader(this) }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout -> ClassicsFooter(this) }
        initDialogX()

    }

    private fun initDataBase() {
        dataBase =
            Room.databaseBuilder(this, MyRoomDataBase::class.java, "userDataBase").build()
        //.addMigrations(object : Migration(1, 2) {
        //    override fun migrate(database: SupportSQLiteDatabase) {
        //        // 执行从旧版本升级到新版本的 SQL 语句
        //        database.execSQL("ALTER TABLE my_table ADD COLUMN new_column INTEGER DEFAULT 0 NOT NULL")
        //    }
        //} ) //迁移脚本
        //.fallbackToDestructiveMigration() //数据库升级过程中删除旧表格并创建新表格，但可能会导致数据丢失，因此请谨慎使用
        dataChatList =
            Room.databaseBuilder(this, MyRoomChatList::class.java, "chatLiSTBase").build()
    }

    private fun initPush() {
        // 必须在application.onCreate中配置，不要判断进程，sdk内部有判断
        MTCorePrivatesApi.configDebugMode(this, BuildConfig.DEBUG)
        // 后台没升级tag: V3.5.4-newportal-20210823-gamma.57版本，前端必须调用此方法，否则通知点击跳转有问题
        //MTPushPrivatesApi.configOldPushVersion(this)
        // 初始化推送
        MTPushPrivatesApi.init(this)
    }

    private fun initDialogX() {
        DialogX.init(this)
        DialogX.implIMPLMode = DialogX.IMPL_MODE.VIEW
        DialogX.useHaptic = true
        DialogX.globalTheme = DialogX.THEME.LIGHT
        DialogX.globalStyle = MaterialStyle()
        /*    DialogX.globalTheme = DialogX.THEME.AUTO
          DialogX.onlyOnePopTip = false*/
    }


}

/**
 * 当前activity 是否为最顶栈
 */
fun isTopActivity(activity: Activity?): Boolean {
    return KtxActivityManger.currentActivity.toString() == activity.toString()
}

