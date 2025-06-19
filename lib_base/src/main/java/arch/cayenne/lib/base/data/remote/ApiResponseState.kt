package arch.cayenne.lib.base.data.remote

/***
 * 負責API請求的狀態 (Remote to ViewModel)
 */
sealed class ApiResponseState {
    // 無狀態
    data object Start : ApiResponseState()
    // api 請求中
    data object Processing : ApiResponseState()
    // api 請求成功
    data class Succeeded<T>(val data: T) : ApiResponseState()
    // api 請求失敗，返回錯誤信息
    data class Failed(val error: ApiFailedState? = null) :
        ApiResponseState()
}

sealed class ApiFailedState {
    // 網路異常 ex. 無連線
    data object NetworkUnavailable : ApiFailedState()

    // 伺服器異常 ex. 500, 503
    data object ServerUnavailable : ApiFailedState()

    // 請求超時
    data object Timeout : ApiFailedState()
}
