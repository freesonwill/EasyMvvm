package com.xcjh.app

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.room.Room
import com.drake.engine.base.app
import com.drake.statelayout.StateConfig
import com.engagelab.privates.core.api.MTCorePrivatesApi
import com.engagelab.privates.push.api.MTPushPrivatesApi
import com.hjq.language.LocaleContract
import com.hjq.language.MultiLanguages
import com.hjq.toast.Toaster
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.kongzue.dialogx.style.MaterialStyle
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.tencent.mmkv.MMKV
import com.xcjh.app.event.AppViewModel
import com.xcjh.app.event.EventViewModel
import com.xcjh.app.ui.login.LoginActivity
import com.xcjh.app.ui.room.MyRoomChatList
import com.xcjh.app.ui.room.MyRoomDataBase
import com.xcjh.app.view.PopupKickOut
import com.xcjh.app.view.callback.EmptyCallback
import com.xcjh.app.view.callback.LoadingCallback
import com.xcjh.base_lib.App
import com.xcjh.base_lib.BuildConfig
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.manager.KtxActivityManger
import com.xcjh.base_lib.utils.startNewActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
var  showDialog: PopupKickOut?=null
var popwindow: BasePopupView?=null
fun placeLoginDialogNewNEW(){
    if(showDialog==null){
        showDialog= PopupKickOut(appContext)
        popwindow= XPopup.Builder(appContext)
            .hasShadowBg(true)
            .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
            .isViewMode(false)
            .isClickThrough(false)
            .dismissOnBackPressed(false)
            .dismissOnTouchOutside(false)
            .isDestroyOnDismiss(false) //对于只使用一次的弹窗，推荐设置这个
            //                        .isThreeDrag(true) //是否开启三阶拖拽，如果设置enableDrag(false)则无效
            .asCustom(showDialog)
    }

    if(!popwindow!!.isShow){
        popwindow!!.show()
    }
}



var placeDialog: CustomDialog?=null

/***
 * 异地登录弹出
 */
public fun placeLoginDialog(context: Context) {
    //判断现在是否在登录状态
    placeDialog= CustomDialog.build()
        .setCustomView(object : OnBindView<CustomDialog?>(R.layout.layout_dialogx_delmsg_new) {
            override fun onBind(dialog: CustomDialog?, v: View) {
                val tvcancle = v.findViewById<TextView>(R.id.tvcancle)
                val textName = v.findViewById<TextView>(R.id.textName)
                val tvsure = v.findViewById<TextView>(R.id.tvsure)
                val viewGen = v.findViewById<View>(R.id.viewGen)
//                //语言 0是中文  1是繁体  2是英文
//                if( Constants.languageType==0){
//                    Context
//                }
                //语言 0是中文  1是繁体  2是英文
                if( Constants.languageType==0){
                    textName.text= context.getString(R.string.place_txt_login_zhong)
                    tvsure.text= context.getString(R.string.ensure_zhong)
                }else if( Constants.languageType==1){
                    textName.text= context.getString(R.string.place_txt_login_Fanti)
                      tvsure.text= context.getString(R.string.ensure_fanti)
                }else{
                    textName.text= context.getString(R.string.place_txt_login_ying)
                    tvsure.text= context.getString(R.string.ensure_yingwen)
                }


                tvcancle.visibility= View.GONE
                viewGen.visibility= View.GONE
                tvsure.setOnClickListener {
//                    appViewModel.quitLoginEvent.postValue(true)
                    startNewActivity<LoginActivity> {}
                    GlobalScope.launch(Dispatchers.Main) { // 使用主线程的调度器
                        delay(500L) // 延迟1秒（1000毫秒）
                        appViewModel.mainViewPagerEvent.postValue(-1)
                        appViewModel.quitLoginEvent.postValue(true)
                    }
                    placeDialog?.dismiss()

                }
            }
        }).setAlign(CustomDialog.ALIGN.CENTER).setCancelable(false).
        setMaskColor(//背景遮罩
            ContextCompat.getColor(context, com.xcjh.base_lib.R.color.blacks_tr)

        )
    if( !placeDialog?.isShow!!){
        placeDialog?.show()
    }
}
