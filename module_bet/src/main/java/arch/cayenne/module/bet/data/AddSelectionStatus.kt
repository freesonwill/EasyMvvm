package arch.cayenne.module.bet.data

import arch.cayenne.lib.common.utils.ext.ResourceExt.getString

/**
 * 投注状态
 */
sealed class AddSelectionStatus {

    // 成功狀態
    sealed class Success : AddSelectionStatus() {
        data object Single : Success()   // 單注成功
        data object Combo : Success()    // 串關成功
        data class Update(
            val lastSelectionId: Long,
            val currentSelectionId: Long
        ) : Success()                                                                   // 成功更新
    }

    // 失敗狀態
    sealed class Failure(val msg: String? = null) : AddSelectionStatus() {
        data object NetworkDisconnected : Failure(arch.cayenne.lib.common.R.string.toast_server_disconnected.getString()) // 網路斷線
        data object DisableComboForParlay : Failure(arch.cayenne.lib.common.R.string.disabled_to_combo.getString()) // 串關限制（非串關投注）
        data object DisableComboForProvider : Failure(arch.cayenne.lib.common.R.string.disabled_to_combo_for_provider.getString()) // 串關限制（供應商不同）
        data object MaxLimit : Failure(arch.cayenne.lib.common.R.string.disabled_to_combo_for_over_limit.getString(10))   // 超過最大選擇數量
        data object Fail : Failure()       // 其他錯誤
        data object AddAfterCancel : Failure() // 取消後再添加
    }

    sealed class Others : AddSelectionStatus() {
        data object Remove : Failure()     // 已選中，再次點選則移除
    }
}