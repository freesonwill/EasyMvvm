package com.xcjh.tpInfo

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.hjq.language.MultiLanguages
import com.hjq.toast.Toaster
import com.kingja.loadsir.callback.Callback
import com.kingja.loadsir.callback.SuccessCallback
import com.kingja.loadsir.core.LoadSir
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.style.MaterialStyle
import com.tencent.mmkv.MMKV
import com.xcjh.base_lib.App
import com.xcjh.base_lib.appContext
import com.xcjh.tpInfo.event.AppViewModel
import com.xcjh.tpInfo.event.EventViewModel
import com.xcjh.tpInfo.view.callback.EmptyCallback
import com.xcjh.tpInfo.view.callback.LoadingCallback

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
        appViewModelInstance = getAppViewModelProvider()[AppViewModel::class.java]
        eventViewModelInstance = getAppViewModelProvider()[EventViewModel::class.java]
        Toaster.init(this);
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    private fun initDialogX() {
        DialogX.init(this)
        DialogX.implIMPLMode = DialogX.IMPL_MODE.WINDOW
        DialogX.useHaptic = true
        DialogX.globalStyle = MaterialStyle()
        /*    DialogX.globalTheme = DialogX.THEME.AUTO
          DialogX.onlyOnePopTip = false*/
    }


}