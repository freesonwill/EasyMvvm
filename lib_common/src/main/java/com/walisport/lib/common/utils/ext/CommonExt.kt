package com.walisport.lib.common.utils.ext

import android.os.Looper


/**
 * Description:
 *
 **/
object CommonExt {


    //是否是主线程
    @JvmStatic
    inline val isMainThread: Boolean get() = Looper.myLooper() == Looper.getMainLooper()


}