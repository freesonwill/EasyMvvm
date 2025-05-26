package arch.cayenne.lib.http.data

/**
 * @author: zhangsan
 * @date: 2025/5/21 14:52
 * @description: 结果成功或者失败的链式调用
 */
sealed class Result<out T> {
    data object Start : Result<Nothing>()
    data class Progress(val progress: Int, val total: Int) : Result<Nothing>()
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val code: Int, val message: String?, val throwable: Throwable?) :
        Result<Nothing>()
}


