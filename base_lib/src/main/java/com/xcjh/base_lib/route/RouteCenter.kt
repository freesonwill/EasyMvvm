package com.xcjh.base_lib.route

import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter

/**
 * @author Alwyn
 * @Date 2020/10/23
 * @Description
 */
object RouteCenter {
    fun navigate(path: String, bundle: Bundle? = null): Any? {
        val build = ARouter.getInstance().build(path)
        return if (bundle == null) build.navigation() else build.with(bundle).navigation()
    }
}