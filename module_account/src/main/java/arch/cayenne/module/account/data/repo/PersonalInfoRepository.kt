package arch.cayenne.module.account.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.data.constants.BASE_URL
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.AvatarEmbedded
import arch.cayenne.lib.database.entity.SystemAvatarBean
import arch.cayenne.lib.database.entity.UserDataBean
import arch.cayenne.lib.database.entity.WalletBean
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.http._interface.IAccount
import arch.cayenne.lib.http.data.AccountInfo
import arch.cayenne.lib.http.data.ApiNickname
import arch.cayenne.lib.http.data.ApiUpAvatar
import arch.cayenne.lib.websocket.WebSocketManager
import arch.cayenne.module.account.R
import arch.cayenne.module.account.data.model.PersonalInfoData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

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
    private val httpClient: HttpClient,
) : BaseRepository() {

    private val _uploadAvatarResult = MutableLiveData<String>()
    val uploadAvatarResult: LiveData<String> = _uploadAvatarResult

    private val _uploadResult = MutableLiveData<String>()
    val uploadResult: LiveData<String> = _uploadResult

    fun getAccountNicknameRecommendations(): UnPeekLiveData<List<String>> {
        val nicknameRecommenListLiveData = UnPeekLiveData<List<String>>()
        val api = httpClient.create(IAccount::class.java)
        scope.launch(Dispatchers.IO) {
            httpClient.safeRequest(
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

    fun changeNickname(nickNames: String): UnPeekLiveData<Boolean> {
        val changeNickNameLiveData = UnPeekLiveData<Boolean>()
        scope.launch(Dispatchers.IO) {
            val api = httpClient.create(IAccount::class.java)
            httpClient.safeRequest(
                request = {
                    val apiNickname = ApiNickname(nickname = nickNames)
                    api.changeNickname(apiNickname)
                },
                onSuccess = { resp ->
                    LogUtils.d("ChangeNickname", "Response: $resp")
                    if (resp.code == 0) {
                        saveUserInfo(nickNames)
                        getAccountInfo()
                        changeNickNameLiveData.postValue(true)
                    }
                },
                onFailure = { code, msg, throwable ->
                    LogUtils.d("ChangeNickname", "Response: $code, $msg")
                    changeNickNameLiveData.postValue(false)
                }
            )
        }
        return changeNickNameLiveData
    }

    //更新用户头像
    fun updateAvatar(avatarUrl: String, type: String, avatarId: String) {
        scope.launch(Dispatchers.IO) {
            val api = httpClient.create(IAccount::class.java)
            httpClient.safeRequest(
                request = {
                    api.updateAvatar(ApiUpAvatar(type = type, url = avatarUrl, avatarId = avatarId))
                },
                onSuccess = { resp ->
                    LogUtils.d("updateAvatar", "Response: $resp")
                    if (resp.code == 0) _uploadAvatarResult.postValue(avatarUrl)
                    scope.launch {
                        database.userDataDao().updateAvatarUrl(BASE_URL + avatarUrl)
                    }
                },
                onFailure = { code, msg, _ ->
                    LogUtils.d("updateAvatar", "Response: $code, $msg")
                    _uploadAvatarResult.postValue("")
                }
            )
        }
    }


    fun uploadAvatar(uid: String, token: String, filePath: String) {
        scope.launch(Dispatchers.IO) {
            val api = httpClient.create(IAccount::class.java)
            httpClient.safeRequest(
                request = {
                    val file = File(filePath)
                    val requestFile = file.asRequestBody("image/*".toMediaType())
                    val filePart =
                        MultipartBody.Part.createFormData("files", file.name, requestFile)
                    val uidBody = uid.toRequestBody("text/plain".toMediaType())
                    val tokenBody = token.toRequestBody("text/plain".toMediaType())
                    api.upAvatar(filePart, uidBody, tokenBody)
                },
                onSuccess = { resp ->
                    LogUtils.d("UploadAvatar", "Response: ${resp.data.filenames}")
                    if (resp.code == 0) {
                        getAccountInfo()
                        _uploadResult.postValue(resp.data.filenames[0])
                    }
                },
                onFailure = { code, msg, throwable ->
                    LogUtils.d("UploadAvatar", "Response: $code, $msg")
                    _uploadResult.postValue("")
                }
            )
        }
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
            var userInfo: UserDataBean? = database.userDataDao().getUser()
            userInfo?.nickname = nickName
            userInfo?.let { database.userDataDao().insert(it) }
        }
    }


    fun observeUserInfo() = database.userDataDao().observeUser()
    fun getPersonalInfoData(): List<SystemAvatarBean> {
        return database.systemAvatarDao().querySystemAvatar().toMutableList()
    }

}