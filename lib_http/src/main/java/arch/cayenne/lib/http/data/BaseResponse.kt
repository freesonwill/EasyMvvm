package arch.cayenne.lib.http.data

/**
 * @author: zhangsan
 * @date: 2025/5/21 14:49
 * @description: 请求结果
 */
abstract class BaseResponse<T> {
    abstract fun code(): Int
    abstract fun data(): T?
    abstract fun message(): String?
    abstract fun isSuccess(): Boolean
}