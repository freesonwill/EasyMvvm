package com.cn.game.sdk2.ui


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import com.cn.game.sdk2.R
import com.lxj.xpopup.core.BottomPopupView

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/13 18:18
 **/
open class HomeXPopupDialog(context: Context, private var contentView: View) : BottomPopupView(context) {

    override fun onCreate() {
        super.onCreate()
        setContentView(contentView)
    }

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_home_xpopup_container
    }

    fun setContentView(contentView: View) {
        if (popupImplView == null) return
        val container = popupImplView as ViewGroup
        this.contentView = contentView
        container.removeAllViews()
        container.addView(contentView)
    }

//    fun setContentView(contentView: View) {
//        if (popupImplView == null) return
//        val container = popupImplView as FragmentContainerView
//        this.contentView = contentView
//        container.removeAllViews()
//        container.addView(contentView)
//    }
}
