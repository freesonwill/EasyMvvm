package com.walisport.module.setting.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.OddsDisplayEnum
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class OddsDisplayRepository(
    override val scope: CoroutineScope,
    private val manager: UserDataManager
) : BaseRepository() {

    //设置赔率显示方式
    fun setOddsType(type: OddsDisplayEnum) {
        scope.launch {
            manager.setKeyValue(UserDataKey.KEY_ODDS, type.value)
        }
    }

    //获取赔率显示方式
    fun getOddsType(): OddsDisplayEnum {
        val value = manager.getValue(UserDataKey.KEY_ODDS, OddsDisplayEnum.EU.value)
        return OddsDisplayEnum.entries[value]
    }
}