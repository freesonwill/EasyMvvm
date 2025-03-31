package com.walisport.lib_base.data.remote

sealed class ApiResponseState {
    // 無狀態
    data object Idle : ApiResponseState()
    // api 請求中
    data object Processing : ApiResponseState()
    // api 請求成功
    data object Succeeded : ApiResponseState()
    // api 請求失敗，返回錯誤信息
    data class Failed(val code: Int? = null, val desc: String? = null) : ApiResponseState()
}
