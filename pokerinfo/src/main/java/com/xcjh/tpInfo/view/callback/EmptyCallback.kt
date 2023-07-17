package com.xcjh.tpInfo.view.callback


import com.kingja.loadsir.callback.Callback
import com.xcjh.tpInfo.R


class EmptyCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.empty_view
    }

}