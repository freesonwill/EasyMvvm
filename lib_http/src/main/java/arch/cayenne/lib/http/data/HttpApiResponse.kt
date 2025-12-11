package arch.cayenne.lib.http.data

data class HttpApiResponse<T> (
    val code: Int,
    val message: String,
    val timestamp: Long,
    val data: T
)

