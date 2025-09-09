package arch.cayenne.module.bet.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.module.bet.data.OddsChangeEnum
import kotlinx.coroutines.CoroutineScope

class OddsChangeRepository(
    override val scope: CoroutineScope,
    private val userManager: UserDataManager
): BaseRepository(scope) {

    fun saveOddsChange(value: OddsChangeEnum) {
        userManager.setKeyValue(UserDataKey.KEY_ODDS_CHANGE, value.value)
    }

    fun getOddsChange(): OddsChangeEnum {
        val value = userManager.getValue(UserDataKey.KEY_ODDS_CHANGE, OddsChangeEnum.ANY.value)
        return OddsChangeEnum.entries.firstOrNull { it.value == value } ?: OddsChangeEnum.ANY
    }
}