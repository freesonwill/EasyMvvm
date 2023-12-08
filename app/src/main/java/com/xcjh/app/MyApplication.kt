package com.xcjh.app

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.room.Room
import com.drake.statelayout.StateConfig
import com.hjq.language.MultiLanguages
import com.hjq.toast.Toaster
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.style.MaterialStyle
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.tencent.mmkv.MMKV
import com.xcjh.app.event.AppViewModel
import com.xcjh.app.event.EventViewModel
import com.xcjh.app.ui.room.MyRoomChatList
import com.xcjh.app.ui.room.MyRoomDataBase
import com.xcjh.app.utils.DynamicTimeFormat
import com.xcjh.app.view.callback.EmptyCallback
import com.xcjh.app.view.callback.LoadingCallback
import com.xcjh.base_lib.App
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.manager.KtxActivityManger


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

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(appContext)
        // 初始化语种切换框架
        MultiLanguages.init(this)
        //界面加载管理 初始化
        LoadSir.beginBuilder()
            .addCallback(LoadingCallback())//加载
            //.addCallback(ErrorCallback())//错误
            .addCallback(EmptyCallback())//空
            .setDefaultCallback(SuccessCallback::class.java)//设置默认加载状态页
            .commit()
        initDialogX()
        dataBase =
            Room.databaseBuilder(this, MyRoomDataBase::class.java, "userDataBase").build()
        dataChatList =
            Room.databaseBuilder(this, MyRoomChatList::class.java, "chatLiSTBase").build()
        appViewModelInstance = getAppViewModelProvider()[AppViewModel::class.java]
        eventViewModelInstance = getAppViewModelProvider()[EventViewModel::class.java]
        Toaster.init(this);
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        //

        //ijk内核，默认模式
        PlayerFactory.setPlayManager(IjkPlayerManager::class.java)
        /**
         *  推荐在Application中进行全局配置缺省页, 当然同样每个页面可以单独指定缺省页.
         *  具体查看 https://github.com/liangjingkanji/StateLayout
         */
        StateConfig.apply {
            emptyLayout = R.layout.layout_empty
            errorLayout = R.layout.layout_empty
            loadingLayout = R.layout.layout_loading
            setRetryIds(R.id.ivEmptyIcon, R.id.txtEmptyName)

            onLoading {
                // 此生命周期可以拿到LoadingLayout创建的视图对象, 可以进行动画设置或点击事件.
            }
        }
       /* PageRefreshLayout.refreshEnableWhenError = false
        PageRefreshLayout.refreshEnableWhenEmpty = false*/
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout -> MaterialHeader(this) }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout -> ClassicsFooter(this) }




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