package com.walisport.app.data.repo

import arch.cayenne.lib.base.data.model.SkinType
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ImmersionBarUtils.immersionBarSkinTypeExt
import kotlinx.coroutines.CoroutineScope

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:58
 * @description:
 */
class MainRepository(
    override val scope: CoroutineScope,
    private val userDataManager: UserDataManager
) : BaseRepository() {
    //获取皮肤背景
    fun getSkinType(): String {
        StatusBarConfig.keySkin =userDataManager.getValue(UserDataKey.KEY_SKIN, SkinType.SKIN_WHITE_BLUE.value)
        StatusBarConfig.statusBarDarkFont = immersionBarSkinTypeExt(StatusBarConfig.keySkin)
        return userDataManager.getValue(UserDataKey.KEY_SKIN, SkinType.SKIN_WHITE_BLUE.value)
    }
}