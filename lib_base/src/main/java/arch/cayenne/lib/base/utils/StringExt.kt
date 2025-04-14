package arch.cayenne.lib.base.utils

/**
 * 字符串扩展
 */
object StringExt {

    /**
     * 安全截取字符串
     */
    fun String.safeSubstring(start: Int, len: Int): String {
        return if (start == 0 && len == length) this
        else this.substring(start.coerceAtMost(length), (start+len).coerceAtMost(length))
    }
}