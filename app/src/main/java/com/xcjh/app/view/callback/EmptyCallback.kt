package com.xcjh.app.view.callback


import com.kingja.loadsir.callback.Callback
import com.xcjh.app.R


class EmptyCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.empty_view
    }

}