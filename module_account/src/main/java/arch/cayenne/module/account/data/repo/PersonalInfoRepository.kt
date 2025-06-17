package arch.cayenne.module.account.data.repo

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.module.account.R
import arch.cayenne.module.account.data.model.PersonalInfoData
import kotlinx.coroutines.CoroutineScope

/**
 * @author: ricky.chang
 * @date: 2025/6/11 下午4:17
 * @description:
 */
class PersonalInfoRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val userDataManager: UserDataManager
) : BaseRepository() {
    fun getPersonalInfoData(): List<PersonalInfoData> {
        val personalInfoData = mutableListOf<PersonalInfoData>()
        for (i in 0..7) {
            personalInfoData.add(
                PersonalInfoData(
                    resId = when (i) {
                        0 -> R.drawable.ic_head_info_1
                        1 -> R.drawable.ic_head_info_2
                        2 -> R.drawable.ic_head_info_3
                        3 -> R.drawable.ic_head_info_4
                        4 -> R.drawable.ic_head_info_5
                        5 -> R.drawable.ic_head_info_6
                        6 -> R.drawable.ic_head_info_7
                        7 -> R.drawable.ic_head_info_8
                        else -> R.drawable.ic_head_info_1
                    }
                    ,i
                )
            )
        }
        return personalInfoData
    }
    fun saveData(nickName: String, resId: Int, position: Int) {
        // Save the personal info data to user data manager or database
        // This is a placeholder for the actual implementation
        val savedNickname = userDataManager.getValue(UserDataKey.KEY_PERSONAL_INFO_NICKNAME, "")
        if (savedNickname.isEmpty()) {
            userDataManager.setKeyValue(UserDataKey.KEY_PERSONAL_INFO_NICKNAME, nickName)
        }
        userDataManager.setKeyValue(UserDataKey.KEY_PERSONAL_INFO_RES_ID, resId)
        userDataManager.setKeyValue(UserDataKey.KEY_PERSONAL_INFO_POSITION, position)
    }
    fun getDefaultNickName(): String {
        return userDataManager.getValue(UserDataKey.KEY_PERSONAL_INFO_NICKNAME, "")
    }
    fun getDefaultPosition(): Int {
        return userDataManager.getValue(UserDataKey.KEY_PERSONAL_INFO_POSITION, -1)
    }
}