package com.walisport.module.hall.data

import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.data.constants.PreloadEnum
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.AvatarEmbedded
import arch.cayenne.lib.database.entity.GameBean
import arch.cayenne.lib.database.entity.GameSupplierDataModel
import arch.cayenne.lib.database.entity.SystemAvatarBean
import arch.cayenne.lib.database.entity.UserDataBean
import arch.cayenne.lib.database.entity.WalletBean
import arch.cayenne.lib.http.HttpClient
import arch.cayenne.lib.http.HttpException
import arch.cayenne.lib.http._interface.IAccount
import arch.cayenne.lib.http.data.AccountInfo
import arch.cayenne.lib.websocket.WebSocketManager
import com.walisport.module.business.common.data.constants.GameSortType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class HallRepository(
    override val scope: CoroutineScope,
    private val database: GameDatabase,
    private val httpClient: HttpClient,
    private val socketManager: WebSocketManager,
    private val preloadResultChange: MutableStateFlow<PreloadEnum>,
    private val manager: UserDataManager,
) : BaseRepository() {

    private val _gameCategoryListLiveData = UnPeekLiveData<List<GameCategoryVo>>()
    val gameCategoryListLiveData: UnPeekLiveData<List<GameCategoryVo>> = _gameCategoryListLiveData


    /**
     * 观察用户令牌的变化。
     *
     * 此方法通过监听用户数据管理器中存储的用户令牌（KEY_TOKEN），
     * 并将其转换为一个布尔值流，表示令牌是否存在且非空。
     *
     * @return 一个 `Flow<Boolean>`，当令牌存在且非空时发射 `true`，否则发射 `false`。
     */
    fun observeUserToken(): Flow<Boolean> {
        return manager.observe<String>(UserDataKey.KEY_TOKEN).transform { token ->
            emit(token.isNotEmpty())
        }
    }


    /**
     * 检查用户是否已登录。
     *
     * 此方法通过检查用户数据管理器中存储的用户令牌（KEY_TOKEN）是否存在且非空，
     * 来判断用户的登录状态。
     *
     * @return `true` 如果用户令牌存在且非空，表示用户已登录；
     *         否则返回 `false`。
     */
    fun checkIsLogin(): Boolean {
        return !manager.getValue<String>(UserDataKey.KEY_TOKEN).isNullOrEmpty()
    }

    suspend fun queryGameList(
        page: Int,
        sortType: GameSortType,
        suppliers: List<Int>,
        category: Int
    ): ApiResponseState {
        val api = httpClient.create(IHallApi::class.java)
        return suspendCancellableCoroutine<ApiResponseState> { cancellableContinuation ->
            scope.launch(Dispatchers.IO) {
                httpClient.safeRequest(
                    request = {
                        api.queryGameList(
                            page = page ,
                            pageSize = DEFAULT_GAME_SIZE ,
                            sort = sortType.type ,
                            supplier = suppliers ,
                            category = category
                        )
                    } ,
                    onSuccess = { resp ->
                        if (resp.code == 0) {
                            cancellableContinuation.resume(ApiResponseState.Succeeded(resp.data))
                        } else {
                            "response------>${resp.code},${resp.message}".loge(TAG)
                            cancellableContinuation.resume(
                                ApiResponseState.Failed(
                                    HttpException(
                                        resp.code ,
                                        resp.message
                                    )
                                )
                            )
                        }
                    } ,
                    onFailure = { code , msg , throwable ->
                        "response------>$code,$msg,$throwable".loge(TAG)
                        cancellableContinuation.resume(
                            ApiResponseState.Failed(
                                HttpException(
                                    code ,
                                    msg ?: ""
                                )
                            )
                        )
                    }
                )
            }
        }

    }

    //获取通用配置数据
    fun queryGameCommonList() {
        val api = httpClient.create(IHallApi::class.java)
        scope.launch(Dispatchers.IO) {
            httpClient.safeRequest(
                request = {
                    api.queryGameCommon()
                } ,
                onSuccess = { resp ->
                    if (resp.code == 0) {
                        "response------queryGameCommonList>${resp.code},${resp.data}".loge(TAG)
                        setSupplierList(resp.data.gameSupplier , resp.data.category)
                         _gameCategoryListLiveData.postValue(resp.data.category)
                        roomSystemAvatar(resp.data.sysAvatars,resp.data.resourceHost)
                    } else {
                        "response------queryGameCommonList>${resp.code},${resp.message}".loge(TAG)
                    }
                } ,
                onFailure = { code , msg , throwable ->
                    "response------queryGameCommonList>$code,$msg,$throwable".loge(TAG)
                }
            )
        }
    }

    //登录成功后重新获取下个人信心
    fun getAccountInfo() {
        val api = httpClient.create(IAccount::class.java)
        scope.launch(Dispatchers.IO) {
            httpClient.safeRequest(
                request = {
                    api.profileInfo()
                },
                onSuccess = { resp ->
                    "======${resp.data}".loge("测试")
                    if (resp.code == 0) {
                        launch {
                            saveAccountInfo(resp.data)
                        }
                    }
                },
                onFailure = { code, msg, throwable ->
                    "ProfileInfo failure, response------>$code,$msg,$throwable".loge(TAG)
                }
            )
        }
    }

    suspend fun queryAllGameList(
        page: Int ,
        pageSize: Int,
        category: Int
    ): ApiResponseState {
        var sort = if (category==0) 0 else 4
        //LogUtils.e("response------all--category${category}")
        val api = httpClient.create(IHallApi::class.java)
        return suspendCancellableCoroutine<ApiResponseState> { cancellableContinuation ->
            scope.launch(Dispatchers.IO) {
                httpClient.safeRequest(
                    request = {
                        api.queryGameList(
                            page = page ,
                            pageSize = pageSize ,
                            sort = sort ,
                            supplier = emptyList() ,
                            category = category
                        )
                    } ,
                    onSuccess = { resp ->
                        if (resp.code == 0) {
                            cancellableContinuation.resume(ApiResponseState.Succeeded(resp.data))
                        } else {
                            LogUtils.e("response------all--${resp.data}")
                            "response------all>${resp.code},${resp.message}".loge(TAG)
                            cancellableContinuation.resume(
                                ApiResponseState.Failed(
                                    HttpException(
                                        resp.code ,
                                        resp.message
                                    )
                                )
                            )
                        }
                    } ,
                    onFailure = { code , msg , throwable ->
                        LogUtils.e("response------all--${code}")
                        cancellableContinuation.resume(
                            ApiResponseState.Failed(
                                HttpException(
                                    code ,
                                    msg ?: ""
                                )
                            )
                        )
                    }
                )
            }
        }
    }

    private suspend fun saveAccountInfo(profileInfo: AccountInfo) {
        database.userDataDao().insert(
            UserDataBean(
                nickname = profileInfo.nickname,
                avatar = AvatarEmbedded(
                    url = profileInfo.avatar.url,
                    thumbhash = profileInfo.avatar.thumbhash,
                    type = profileInfo.avatar.type
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

    //设置游戏点击状态
    suspend fun setGameClick(flag:Int){
        scope.launch(Dispatchers.IO) {
            database.gameDao().insert(GameBean(id = 1, clickFlag = flag))
        }
    }

    //查询选中供应商数据
    suspend fun queryGameSuppliersSelect(gameType: Int) =
        database.supplierDao().querySelectSupplier(gameType)

    //监听供应商数据变化
    suspend fun getSupplierByGameTypeId(gameType: Int) =
        database.supplierDao().querySelectSupplier(gameType)

    private suspend fun roomGameSupplier(
        supplier: List<GameSupplier> ,
        gameTypeList: List<GameCategoryVo>
    ): List<GameSupplierDataModel> {
        var list: MutableList<GameSupplierDataModel> = mutableListOf()
        gameTypeList.forEach { data ->//分类列表
            data.supplierIds.forEach { ids ->//分类列表 供应商列表ID
            supplier.forEach {it-> //供应商列表
                    if (ids == it.id) {
                        list.add(
                            GameSupplierDataModel(
                                index = list.size + 1 ,
                                id = it.id ,
                                hot = it.hot ,
                                icon = it.icon ,
                                gameTypeId = data.category ,
                                name = it.name ,
                                isSelected = 0
                            )
                        )
                    }
                }
            }
        }
        return list
    }
    private fun roomSystemAvatar(
        avatarVo: List<SysAvatarVo>,host:String
    ) {
        var list: MutableList<SystemAvatarBean> = mutableListOf()
        avatarVo.forEach { data ->//分类列表
            list.add(
                SystemAvatarBean(
                    id = data.id ,
                    groupId = data.groupId ,
                    subId = data.subId ,
                    url = data.url ,
                    host = host
                )
            )
        }
        database.systemAvatarDao().insert(list)
    }


    //供应商存储到room
    fun setSupplierList(supplier: List<GameSupplier> , gameTypeList: List<GameCategoryVo>) {
        scope.launch(Dispatchers.IO) {
            var list: List<GameSupplierDataModel> = roomGameSupplier(supplier , gameTypeList)
            database.supplierDao().insert(list)
        }
    }


    //设置单个ID为选中
   fun selectSupplierId(gameType: Int,id:Int){
        scope.launch(Dispatchers.IO) {
            database.supplierDao().selectSupplierId(gameType,id)
        }
   }

    //清空选中
    fun clearSelectedByType(gameType: Int) {
        scope.launch(Dispatchers.IO) {
            database.supplierDao().clearSelectedByType(gameType)
        }
    }

    companion object {
        const val DEFAULT_GAME_SIZE = 10
        const val INITIAL_PAGE = 1
    }
}