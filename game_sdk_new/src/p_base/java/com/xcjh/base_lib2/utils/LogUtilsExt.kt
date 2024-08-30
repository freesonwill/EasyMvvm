package com.xcjh.base_lib2.utils

object LogUtilsExt {
    const val TAG = "GameSdk_"

    fun String.logd(tag: String = TAG) = LogUtils.dTag(tag, this)
    fun String.logv(tag: String = TAG) = LogUtils.vTag(tag, this)
    fun String.logi(tag: String = TAG) = LogUtils.vTag(tag, this)
    fun String.logw(tag: String = TAG) = LogUtils.vTag(tag, this)
    fun String.loge(tag: String = TAG) = LogUtils.vTag("$TAG$tag", this)
}
