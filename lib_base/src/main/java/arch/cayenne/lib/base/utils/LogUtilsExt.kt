package arch.cayenne.lib.base.utils

import arch.cayenne.lib.base.utils.StringExt.safeSubstring

object LogUtilsExt {

    fun String.logd(tag: String = "",start:Int=0,len:Int=length) =
        LogUtils.dTag(tag, this.safeSubstring(start, len))
    fun String.logv(tag: String = "",start:Int=0,len:Int=length) =
        LogUtils.vTag(tag, this.safeSubstring(start, len))
    fun String.logi(tag: String = "",start:Int=0,len:Int=length) =
        LogUtils.iTag(tag, this.safeSubstring(start, len))
    fun String.logw(tag: String = "",start:Int=0,len:Int=length) =
        LogUtils.wTag(tag, this.safeSubstring(start, len))
    fun String.loge(tag: String = "",start:Int=0,len:Int=length) =
        LogUtils.eTag(tag, this.safeSubstring(start, len))

    /**
     * 打印错误堆栈
     * @param start
     * @param len
     */
    fun String.printStackTrace(tag: String="",start:Int=0,len:Int=length) {
        try {
            throw RuntimeException(this.safeSubstring(start,len))
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}
