package com.xcjh.base_lib.utils

import android.graphics.drawable.Drawable
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xcjh.base_lib.ModuleInitializer
import com.xcjh.base_lib.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 作者　:
 * 时间　: 2020/4/8
 * 描述　:BaseViewModel请求协程封装
 */

/**
 *  调用携程
 * @param block 操作耗时操作任务
 * @param success 成功回调
 * @param error 失败回调 可不给
 */
fun <T> ViewModel.launch(
    block: () -> T,
    success: (T) -> Unit,
    error: (Throwable) -> Unit = {}
) {
    viewModelScope.launch {
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                block()
            }
        }.onSuccess {
            success(it)
        }.onFailure {
            error(it)
        }
    }
}
fun BaseViewModel.getString(@StringRes resId: Int): String {
    return ModuleInitializer.application.getString(resId)
}

fun BaseViewModel.getStringArray(@ArrayRes resId: Int): Array<String> {
    return ModuleInitializer.application.resources.getStringArray(resId)
}

fun BaseViewModel.getDrawable(@DrawableRes resId: Int): Drawable? {
    return ContextCompat.getDrawable(ModuleInitializer.application,resId)
}

fun BaseViewModel.getColor(@ColorRes resId: Int): Int {
    return ContextCompat.getColor(ModuleInitializer.application,resId)
}