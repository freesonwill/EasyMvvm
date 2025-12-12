package com.walisport.module.hall.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtils
import arch.cayenne.lib.common.ui.viewmodel.Event
import com.walisport.module.hall.data.Avatar
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.data.HallRepository
import com.walisport.module.hall.data.HotColdType
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel
import kotlin.random.Random
import arch.cayenne.lib.database.entity.GameSupplierDataModel
import com.walisport.module.hall.data.GameCategoryVo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@KoinViewModel
class GameContentViewModel : BaseViewModel() {

    private val repository: HallRepository by inject { parametersOf(viewModelScope) }

    private val _gameRecentList = UnPeekLiveData<List<GameContentData>>()
    val gameRecentList: LiveData<List<GameContentData>> = _gameRecentList

    private val _gameSupplierList = UnPeekLiveData<List<GameSupplierDataModel>>()
    val gameSupplierList: UnPeekLiveData<List<GameSupplierDataModel>> = _gameSupplierList


    private val _gameCategoryList = UnPeekLiveData<List<GameCategoryVo>>()
    val gameCategoryList: UnPeekLiveData<List<GameCategoryVo>> = _gameCategoryList

    // 保存彈窗中的選中狀態（跨彈窗生命週期）
    private val _savedTournamentSelections = MutableLiveData<List<Int>>(
        emptyList()
    )
    val savedTournamentSelections: LiveData<List<Int>> = _savedTournamentSelections

    // 通知按鈕選中狀態變化
    private val _buttonHasSelection = UnPeekLiveData<Boolean>()
    val buttonHasSelection: UnPeekLiveData<Boolean> = _buttonHasSelection


    fun getCategoryList(){

        gameCategoryList
    }


    fun getSuppliers(type: Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeSupplierByGameTypeId(type)
                .collect {
                    launch(Dispatchers.Main) {
                        _gameSupplierList.value = it
                    }
                }
        }

    }

    // 通知需要清除 tlLeagueList 的選中狀態
    private val _shouldClearLeagueListSelection = MutableLiveData<Event<Unit>>()
    val shouldClearLeagueListSelection: LiveData<Event<Unit>> = _shouldClearLeagueListSelection

    // 保存選中狀態（在確認時調用）
    fun saveTournamentSelections(selections: List<Int>) {
        LogUtils.e("updateTournamentButtonStyle--------->${selections}")
        _savedTournamentSelections.value = selections
        // 通知按鈕狀態更新
        _buttonHasSelection.value = selections.isNotEmpty()
        // TODO: 未來同時保存到後端
    }
    // 觸發清除 tlLeagueList 選中狀態（在彈窗確認時調用）
    fun requestClearLeagueListSelection() {
        _shouldClearLeagueListSelection.value = Event(Unit)
    }
    fun mockList(page: Long = 0){
        LogUtils.e("gameRecentList--------------->${page}")
        val mockData = listOf(
            GameContentData(
                id = "${page}10".toLong(),
                name = "${page}1",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/1.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.NONE
            ),
            GameContentData(
                id = "${page}20".toLong(),
                name = "${page}2",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/2.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.NONE
            ),
            GameContentData(
                id = "${page}30".toLong(),
                name = "${page}3",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/3.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.NONE
            ),
            GameContentData(
                id = "${page}40".toLong(),
                name = "${page}4",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/4.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.NONE
            ),
            GameContentData(
                id = "${page}50".toLong(),
                name = "${page}5",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/5.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.NONE
            ),
            GameContentData(
                id = "${page}60".toLong(),
                name = "${page}6",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/6.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.NONE
            ),
            GameContentData(
                id = "${page}70".toLong(),
                name = "${page}7",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/7.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.NONE
            ),
            GameContentData(
                id = "${page}80".toLong(),
                name = "${page}8",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/8.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.NONE
            ),
            GameContentData(
                id ="${page}90".toLong(),
                name = "${page}9",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/9.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.NONE
            )
            ,
            GameContentData(
                id = "${page}00".toLong(),
                name = "${page}3",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/3.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.NONE
            )
        )
        LogUtils.e("gameRecentList--------------->mockData${mockData}")
        _gameRecentList.postValue(mockData)
    }
}