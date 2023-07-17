package com.xcjh.tpInfo.view.callback

import android.content.Context
import android.view.View
import com.kingja.loadsir.callback.Callback


class LoadingCallback : Callback() {

    override fun onCreateView(): Int {
        return com.xcjh.base_lib.R.layout.common_loading_layout
    }

    override fun onReloadEvent(context: Context?, view: View?): Boolean {
        return true
    }
}