package com.walisport.module.gamedetail.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.gamedetail.data.GameDetailRepository
import com.walisport.module.gamedetail.data.model.GameDetailBean
import com.walisport.module.gamedetail.data.model.GamePreviewBean
import com.walisport.module.gamedetail.data.model.PreviewType
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class GameDetailPageViewModel : BaseViewModel() {
    private val repository: GameDetailRepository by inject { parametersOf(viewModelScope) }

    // Shared state for Carousel index
    val sharedCarouselIndex = MutableLiveData(0)

    private val _gameDetailData = MutableLiveData<GameDetailBean>()
    val gameDetailData: LiveData<GameDetailBean> = _gameDetailData

    private val _previewData = MutableLiveData<List<GamePreviewBean>>()
    val previewData: LiveData<List<GamePreviewBean>> = _previewData

    init {
        loadMockData()
    }

    private fun loadMockData() {
        // Mock Game Detail Data
        _gameDetailData.value = GameDetailBean(
            id = 1,
            name = "Slot Game 1",
            type = 1,
            supplier = "Supplier A",
            avatar = "https://example.com/game1.png",
            reward = 0.95,
            maxOdds = 1000,
            online = 1234,
            score = 4.5,
            comments = 9999,
            tryIt = true,
            hasMore = true,
            collect = false,
            materials = true,
            currency = emptyList()
        )

        // Mock Preview Data
        _previewData.value = listOf(
            // GamePreviewBean(PreviewType.VIDEO, "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_1MB.mp4"),
            GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
            GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
            GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
            GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
            GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
            GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
            GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
            GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
            GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
            GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
            GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg"),
            GamePreviewBean(PreviewType.IMAGE, "https://xxx.com/xxx.jpg")
        )
    }

    fun queryGameDetail(gameId: Int) {

    }
}
