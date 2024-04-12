package com.xcjh.app.base

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.gyf.immersionbar.ImmersionBar
import com.hjq.language.MultiLanguages
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.xcjh.app.R
import com.xcjh.app.utils.dismissLoadingExt
import com.xcjh.app.utils.showLoadingExt
import com.xcjh.app.view.PopupKickOut
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.base.activity.BaseVmDbActivity

/**
 * @author zobo
 * 2023.02.15
 */
abstract class BaseActivity<VM : BaseViewModel, DB : ViewDataBinding> : BaseVmDbActivity<VM, DB>() {


    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)//黑色
            .navigationBarColor(R.color.c_ffffff)
            .navigationBarDarkIcon(true)
            .init()

    }


    override fun attachBaseContext(newBase: Context?) {
//        super.attachBaseContext(newBase)

        // 绑定语种
        super.attachBaseContext(MultiLanguages.attach(newBase))
    }
    /**
     * 创建liveData观察者
     */
    override fun createObserver() {}

    /**
     * 打开等待框
     */
    override fun showLoading(message: String) {
        showLoadingExt(message)
    }

    /**
     * 关闭等待框
     */
    override fun dismissLoading() {
        dismissLoadingExt()
    }

    open fun finishTopClick(view: View?) {
        finish()
    }

    /**
     * 泛型的高级特性 泛型实例化
     * 跳转
     */
    inline fun <reified T> startNewActivity(block: Intent.() -> Unit = {}) {
        val intent = Intent(this, T::class.java)
        //把intent实例 传入block 函数类型参数
        intent.block()
        startActivity(intent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.fontScale != 1f) { //设置字体大小不跟随系统
            resources
        }
        super.onConfigurationChanged(newConfig)
    }





//    override fun getResources(): Resources {
//        val resources = super.getResources()
//        val configContext = createConfigurationContext(resources.configuration)
//
//        return configContext.resources.apply {
//            configuration.fontScale = 1.0f
//            displayMetrics.scaledDensity = displayMetrics.density * configuration.fontScale
//        }
//    }

    // 字体大小不跟随系统
    override fun getResources(): Resources  {
        val res = super.getResources()
        val config = Configuration()
        config.setToDefaults() // 设置为默认值
        res.updateConfiguration(config, res.displayMetrics)
        return res
    }

      var  showDialog: PopupKickOut?=null
      var popwindow: BasePopupView?=null
    fun placeLoginDialogNew(){
        if(showDialog==null){
            showDialog= PopupKickOut(this)
            popwindow= XPopup.Builder(this)
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
}