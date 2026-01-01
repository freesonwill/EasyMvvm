package arch.cayenne.module.account.data.repo

import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.AvatarEmbedded
import arch.cayenne.lib.database.entity.UserDataBean
import arch.cayenne.lib.database.entity.WalletBean
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.http._interface.IAccount
import arch.cayenne.lib.http.data.AccountInfo
import arch.cayenne.lib.http.data.ApiNickname
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.module.account.R
import arch.cayenne.module.account.data.model.PersonalInfoData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @author: ricky.chang
 * @date: 2025/6/11 下午4:17
 * @description:
 */
class PersonalInfoRepository(
    override val scope: CoroutineScope,
    private val socketManager: WebSocketManager,
    private val userDataManager: UserDataManager,
    private val database: GameDatabase,
    private val httpClient: HttpClient ,
    private val mockHttpClient: HttpClient ,
) : BaseRepository() {
     fun getAccountNicknameRecommendations(): UnPeekLiveData<List<String>> {
        val nicknameRecommenListLiveData = UnPeekLiveData<List<String>>()
        val api = mockHttpClient.create(IAccount::class.java)
        scope.launch(Dispatchers.IO) {
            mockHttpClient.safeRequest(
                request = { api.recommendNickname() },
                onSuccess = { resp ->
                    if (resp.code == 0) {
                        nicknameRecommenListLiveData.postValue(resp.data.nicknames)
                    }
                },
                onFailure = { code, msg, throwable -> }
            )
        }
        return nicknameRecommenListLiveData
    }

    fun changeNickname(nickNames: String) : UnPeekLiveData<Boolean>{
        val changeNickNameLiveData = UnPeekLiveData<Boolean>()
        scope.launch(Dispatchers.IO) {
            val api = httpClient.create(IAccount::class.java)
            httpClient.safeRequest(
                request = {
                    val apiNickname = ApiNickname(nickname = nickNames)
                    api.changeNickname(apiNickname) },
                onSuccess = { resp ->
                    LogUtils.d("ChangeNickname", "Response: $resp")
                    if (resp.code == 0) {
                        saveUserInfo(nickNames)
                        changeNickNameLiveData.postValue(true)
                    }
                },
                onFailure = { code, msg, throwable ->
                    LogUtils.d("ChangeNickname", "Response: $code, $msg")
                    changeNickNameLiveData.postValue(false) }
            )
        }
        return changeNickNameLiveData
    }
    fun getAccountInfo() {
        val api = httpClient.create(IAccount::class.java)
        scope.launch(Dispatchers.IO) {
            httpClient.safeRequest(
                request = {
                    api.profileInfo()
                },
                onSuccess = { resp ->
                    if (resp.code == 0) {
                        launch {
                            saveAccountInfo(resp.data)
                        }
                    }
                },
                onFailure = { code, msg, throwable ->
                }
            )
        }
    }
    private suspend fun saveAccountInfo(profileInfo: AccountInfo) {
        database.userDataDao().insert(
            UserDataBean(
                nickname = profileInfo.nickname,
                avatar = AvatarEmbedded(
                    url = profileInfo.avatar.url,
                    thumbhash = profileInfo.avatar.thumbhash
                ),
                Uid = 100L,
                registerTime = profileInfo.registerTime,
                vipLevel = profileInfo.vipLevel,
                score = profileInfo.score,
                ccy = profileInfo.ccy,
                list = profileInfo.list.map { WalletBean(it.ccy, it.score, it.exchangeScore) },
                admittedBetScore = profileInfo.admittedBetScore,
                requiredAdmittedBetScore = profileInfo.requiredAdmittedBetScore,
                vipStage = profileInfo.vipStage,
                nicknameChangeCount = profileInfo.nicknameChangeCount,
            )
        )
        //更新余额信息
        database.infoDao().updateBalance(profileInfo.score)
    }
    fun saveUserInfo(nickName: String) {
        scope.launch(Dispatchers.IO) {
            var userInfo : UserDataBean? = database.userDataDao().getUser()
            userInfo?.nickname = nickName
            userInfo?.let { database.userDataDao().insert(it) }
        }
    }


    fun observeUserInfo() = database.userDataDao().observeUser()
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