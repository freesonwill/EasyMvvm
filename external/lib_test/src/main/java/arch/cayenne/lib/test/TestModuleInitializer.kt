package arch.cayenne.lib.test

import DemoPopup
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.startup.Initializer
import arch.cayenne.lib.base.data.DefaultInitializer
import arch.cayenne.lib.base.ui._interface.SimpleActivityLifecycleCallbacks
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.CommonModuleInitializer
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.test.data.bean.DemoData
import arch.cayenne.lib.test.ui.popup.WsPopup
import arch.cayenne.lib.websocket.WebSocketManager
import com.blankj.utilcode.util.ScreenUtils
import com.lxj.xpopup.XPopup
import com.petterp.floatingx.assist.FxDisplayMode
import com.petterp.floatingx.assist.FxGravity
import com.petterp.floatingx.assist.helper.FxScopeHelper
import org.koin.core.context.loadKoinModules
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.mp.KoinPlatform.getKoin

/**
 * @date: 2025/8/25 11:04
 * @description: 测试模块初始化
 */
class TestModuleInitializer : DefaultInitializer<Unit> {
    private val TAG = "TestModuleInitializer"

    override fun create(context: Context) {
        initAnimationController()
        val app = context as Application
        app.registerActivityLifecycleCallbacks(object :SimpleActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                super.onActivityCreated(activity, savedInstanceState)
                if(activity.localClassName.contains("MainActivity") && activity is AppCompatActivity) {
                    app.unregisterActivityLifecycleCallbacks(this)
                    activity.supportFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
                        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
                            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                            if(f::class.java.simpleName.startsWith("MainFragment")){
                                fm.unregisterFragmentLifecycleCallbacks(this)
                                createAnimFloat(f.requireActivity())
                                createWSFloat(f.requireActivity())
                            }
                            "registerFragmentLifecycleCallbacks onFragmentViewCreated:${f}".logd(TAG)
                        }
                    },true)
                }
            }
        })
        loadKoinModules(listOf(socketModules,viewModules))
    }
    private val socketModules = module {
        factory(named("test")) { WebSocketManager(get(), get()) }
    }
    private val viewModules = module {
        includes(defaultModule)
    }

    private fun createAnimFloat(activity: Activity){
        val context: Context = activity
        FxScopeHelper.builder()
            .setLayout(R.layout.demo_popup_float)
            .setGravity(FxGravity.LEFT_OR_BOTTOM)
            .setDisplayMode(FxDisplayMode.Normal)
            .setBottomBorderMargin(200.dp2px.toFloat())
            .setOnClickListener {
                XPopup.Builder(context)
                    .moveUpToKeyboard(true)
                    .autoOpenSoftInput(true)
                    .maxHeight((ScreenUtils.getScreenHeight()*0.5f).toInt())
                    .isViewMode(false)
                    .asCustom(DemoPopup(context)).show()
            }
            .build()
            .toControl(activity)
            .show()
    }

    private fun createWSFloat(activity: Activity){
        val context: Context = activity
        FxScopeHelper.builder()
            .setLayout(R.layout.demo_ws_float)
            .setGravity(FxGravity.LEFT_OR_BOTTOM)
            .setDisplayMode(FxDisplayMode.Normal)
            .setBottomBorderMargin(130.dp2px.toFloat())
            .setOnClickListener {
                XPopup.Builder(context)
                    .moveUpToKeyboard(false)
                    .autoOpenSoftInput(false)
                    .maxHeight((ScreenUtils.getScreenHeight()).toInt())
                    .isViewMode(false)
                    .asCustom(WsPopup(context)).show()
            }
            .build()
            .toControl(activity)
            .show()
    }


    /**
     * 初始化动画参数
     */
    private fun initAnimationController() {
        val defaultData = mapOf(
            UserDataKey.KEY_ANIM_ROUTE to DemoData(300, 0.36f, 0.66f, 0.04f, 1f),
            UserDataKey.KEY_ANIM_ZOOM to DemoData(250, 0.5f, 1f, 0.89f, 1f),
            UserDataKey.KEY_ANIM_POPUP to DemoData(300, 0.33f, 1f, 0.5f, 1f),
            UserDataKey.KEY_ANIM_DRAWER to DemoData(250, 0.42f, 0.1f, 0.5f, 1f),
            UserDataKey.KEY_ANIM_SCROLLBAR to DemoData(210, 0f, 0f, 1f, 1f)
        )
        val manager = getKoin().get<UserDataManager>()
        defaultData.forEach { (key, data) ->
            //d == null第一次写入
            val d:DemoData? = DemoData.getDemoData(manager,key)
            DemoData.setDemoData(manager, key, d?:data, d == null)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return super.dependencies() + CommonModuleInitializer::class.java
    }
}