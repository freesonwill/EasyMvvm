package arch.cayenne.module.account.data.constants

import arch.cayenne.lib.base.data.constants.DataState

/**
 *
 * @date: 2026/1/10 17:00
 * @description:
 */
sealed class NicknameInitialState: DataState {
    data object Success : NicknameInitialState()
    data object Failure : NicknameInitialState()
}