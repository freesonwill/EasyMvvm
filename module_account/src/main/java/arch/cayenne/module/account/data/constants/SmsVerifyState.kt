package arch.cayenne.module.account.data.constants

import arch.cayenne.lib.base.data.constants.DataState

/**
 *
 * @date: 2026/1/10 14:33
 * @description:
 */
sealed class SmsVerifyState : DataState {
    data object Success : SmsVerifyState()
    data object ToNickName : SmsVerifyState()
    data object Failure : SmsVerifyState()
}