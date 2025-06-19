package arch.cayenne.lib.base.data.remote

/***
 * 負責API請求的狀態 (Remote to ViewModel)
 */
sealed class ApiResponseState {
    // 無狀態
    data object Start : ApiResponseState()
    // api 請求中
    data class Processing(val process: Int = 0, val total: Int = 100) : ApiResponseState()
    // api 請求成功
    data class Succeeded<T>(val data: T) : ApiResponseState()
    // api 請求失敗，返回錯誤信息
    data class Failed(val error: ApiFailedState? = null) : ApiResponseState()
}

/***
 * API請求失敗的狀態, 由外部實作失敗狀態
 */
interface ApiFailedState {
    val code: Int
    val message: String
}
