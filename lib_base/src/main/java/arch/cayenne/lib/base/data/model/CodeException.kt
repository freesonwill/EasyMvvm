package arch.cayenne.lib.base.data.model

/**
 * @date: 2025/12/25 15:06
 * @description:带 code 的异常类型
 */
class CodeException(
    val code: Int,
    override val message: String,
    cause: Throwable? = null,
) : Exception(message, cause)