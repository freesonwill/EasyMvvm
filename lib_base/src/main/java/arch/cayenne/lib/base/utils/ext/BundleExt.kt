package arch.cayenne.lib.base.utils.ext

import android.os.Bundle

/**
 * @date: 2025/12/31 11:44
 * @description: Bundle扩展函数
 */
fun Bundle?.contentEquals(b: Bundle?): Boolean {
    val a = this
    if (a === b) return true
    if (a == null || b == null) return false
    if (a.size() != b.size()) return false

    val aKeys = a.keySet()
    val bKeys = b.keySet()
    if (aKeys != bKeys) return false

    for (key in aKeys) {
        val av = a.get(key)
        val bv = b.get(key)
        if (!av.valueEquals(bv)) return false
    }
    return true
}

private fun Any?.valueEquals(b: Any?): Boolean {
    val a = this
    if (a === b) return true
    if (a == null || b == null) return false

    return when {
        a is Bundle && b is Bundle -> a.contentEquals(b)

        a is IntArray && b is IntArray -> a.contentEquals(b)
        a is LongArray && b is LongArray -> a.contentEquals(b)
        a is FloatArray && b is FloatArray -> a.contentEquals(b)
        a is DoubleArray && b is DoubleArray -> a.contentEquals(b)
        a is BooleanArray && b is BooleanArray -> a.contentEquals(b)
        a is ByteArray && b is ByteArray -> a.contentEquals(b)
        a is CharArray && b is CharArray -> a.contentEquals(b)
        a is ShortArray && b is ShortArray -> a.contentEquals(b)

        a is Array<*> && b is Array<*> -> a.contentDeepEquals(b)

        else -> a == b
    }
}
