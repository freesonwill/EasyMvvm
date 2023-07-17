package com.xcjh.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.xcjh.app.view.LoadProgressDialog


//loading框
@SuppressLint("StaticFieldLeak")
private var loadingDialog: LoadProgressDialog? = null

/**
 * 打开等待框
 */
fun AppCompatActivity.showLoadingExt(message: String = "请求网络中") {
    try {
        if (!this.isFinishing) {
            if (loadingDialog == null) {
                loadingDialog = LoadProgressDialog(this, message)
            }
            loadingDialog?.show()
        }
    }catch (e:Exception){

    }
}

/**
 * 打开等待框
 */
fun Fragment.showLoadingExt(message: String = "请求网络中") {
    try {
        activity?.let {
            if (!it.isFinishing) {
                if (loadingDialog == null) {
                    loadingDialog = LoadProgressDialog(it, message)
                }
                loadingDialog?.show()
            }
        }
    }catch (e:Exception){

    }
}

/**
 * 关闭等待框
 */
fun Activity.dismissLoadingExt() {
    loadingDialog?.dismiss()
    loadingDialog = null
}

/**
 * 关闭等待框
 */
fun Fragment.dismissLoadingExt() {
    loadingDialog?.dismiss()
    loadingDialog = null
}
