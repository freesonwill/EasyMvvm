package com.walisport.module.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.GameSortType
import arch.cayenne.lib.common.utils.ext.getFormatDate
import arch.cayenne.lib.database.entity.GameSupplierDataModel
import com.walisport.module.search.data.repo.SearchRepository
import com.walisport.module.search.ui.model.Avatar
import com.walisport.module.search.ui.model.HotColdType
import com.walisport.module.search.ui.model.SearchGameContentData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

/**
 * Search 模組專用的 GameContentViewModel，用於顯示遊戲分類匹配結果（例如「電子」）。
 * 結構與 hall 模組的 GameContentViewModel 對齊，但完全獨立實作。
 * 
 * 目前使用 mock 資料，未來可切換為真實後端 API。
 */
@KoinViewModel
class SearchGameContentViewModel : BaseViewModel() {
    private val repository: SearchRepository by inject { parametersOf(viewModelScope) }

    private val _gameListLiveData: MutableLiveData<List<SearchGameContentData>> = MutableLiveData()
    val gameListLiveData: LiveData<List<SearchGameContentData>> = _gameListLiveData

    private val _gameSupplierList = UnPeekLiveData<List<GameSupplierDataModel>>()
    val gameSupplierList: UnPeekLiveData<List<GameSupplierDataModel>> = _gameSupplierList

    private var page: Int = INITIAL_PAGE
    private var sortType: GameSortType = GameSortType.HOT
    private var suppliers: List<Int> = emptyList()
    private var gameTypeId: Int = 4 // 預設為「電子」分類

    companion object {
        private const val INITIAL_PAGE = 1
    }

    /**
     * 設定遊戲分類 ID（例如：電子 = 4）
     */
    fun setGameTypeId(gameTypeId: Int) {
        this.gameTypeId = gameTypeId
    }

    /**
     * 載入供應商列表（與 GameContentFragment 一致）
     */
    fun getSuppliers(type: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val suppliers = repository.querySuppliersByGameType(type)
            _gameSupplierList.postValue(suppliers)
        }
    }

    /**
     * 設定選中的供應商列表
     */
    fun setSupplier(suppliers: List<Int>) {
        this.suppliers = suppliers
    }

    /**
     * 設定排序類型
     */
    fun setSortType(sortType: GameSortType) {
        this.sortType = sortType
    }

    /**
     * 重新載入（重置頁碼並清空列表）
     */
    fun reload() {
        page = INITIAL_PAGE
        _gameListLiveData.value = emptyList()
        queryGameList()
    }

    /**
     * 載入下一頁
     */
    fun loadNextPage() {
        if (apiStateListener.value == DataState.LoadSuccess) {
            page++
            queryGameList()
        }
    }

    /**
     * 查詢遊戲列表（目前使用 mock 資料）
     */
    private fun queryGameList() {
        setState(DataState.Loading)
        viewModelScope.launch {
            // TODO: 未來切換為真實 API
            // repository.queryGameList(page, sortType, suppliers, gameTypeId)
            
            // 目前使用 mock 資料
            generateMockGames(sortType, suppliers)
        }
    }

    /**
     * 產生 mock 遊戲列表（與 SearchResultDirectMatchViewModel.generateMockGames 一致）
     */
    private fun generateMockGames(sortType: GameSortType, suppliers: List<Int>) {
        val mockGameNames = listOf(
            "Gates of Olympus 1000",
            "Sweet Bonanza",
            "Big Bass Bonanza",
            "Starlight Princess",
            "Sugar Rush",
            "Wild West Gold",
            "The Dog House",
            "Fire Strike",
            "Lucky Grace",
            "Book of Dead",
            "Razor Shark",
            "Reactoonz"
        )

        val base = (suppliers.firstOrNull() ?: 0).toLong() * 1000

        val mockList = (1..12).map { index ->
            val gameIndex = (index - 1) % mockGameNames.size
            val nameSuffix = if (index > mockGameNames.size) " ${index - mockGameNames.size}" else ""
            val name = mockGameNames[gameIndex] + nameSuffix

            val avatar = Avatar(
                url = "https://via.placeholder.com/300x400?text=${mockGameNames[gameIndex].replace(" ", "+")}",
                thumbhash = "",
                css = ""
            )

            val reward = when (sortType) {
                GameSortType.HOT_REWARD -> (970..999).random() / 10.0
                GameSortType.COLD_REWARD -> (900..950).random() / 10.0
                else -> (940..980).random() / 10.0
            }

            val hotOrCold = when (sortType) {
                GameSortType.HOT_REWARD -> HotColdType.HOT
                GameSortType.COLD_REWARD -> HotColdType.COLD
                else -> HotColdType.NONE
            }

            SearchGameContentData(
                id = base + index,
                name = name,
                avatar = avatar,
                online = (100..5000).random(),
                reward = reward,
                hasMore = index % 3 == 0,
                hotOrCold = hotOrCold
            )
        }

        _gameListLiveData.value = (_gameListLiveData.value?.toMutableList() ?: mutableListOf()).apply {
            addAll(mockList)
        }
        setState(if (mockList.isEmpty()) DataState.DataEmpty else DataState.LoadSuccess)
    }

    /**
     * 清除供應商選中狀態（目前簡化實作，未來可對齊 GameContentViewModel）
     */
    fun clearSupplierSelected() {
        // TODO: 未來可實作本地選中狀態管理
    }

    /**
     * 選中供應商 ID（目前簡化實作）
     */
    fun selectSupplierId(id: Int) {
        // TODO: 未來可實作本地選中狀態管理
    }
}

