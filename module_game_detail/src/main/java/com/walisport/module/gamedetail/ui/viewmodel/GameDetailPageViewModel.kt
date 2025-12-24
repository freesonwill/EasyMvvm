package com.walisport.module.gamedetail.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.gamedetail.data.GameDetailRepository
import com.walisport.module.gamedetail.data.model.GameDetailBean
import com.walisport.module.gamedetail.data.model.GameDetailVo
import com.walisport.module.gamedetail.data.model.GamePreviewBean
import com.walisport.module.gamedetail.data.model.PreviewType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    // 收藏状态
    private val _isCollected = MutableLiveData<Boolean>()
    val isCollected: LiveData<Boolean> = _isCollected

    init {
        loadMockData()
    }

    private fun loadMockData() {
        // Mock Preview Data
        _previewData.value = listOf(
            // GamePreviewBean(PreviewType.VIDEO, "https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_1MB.mp4"),
            GamePreviewBean(PreviewType.IMAGE , "https://xxx.com/xxx.jpg") ,
            GamePreviewBean(PreviewType.IMAGE , "https://xxx.com/xxx.jpg") ,
            GamePreviewBean(PreviewType.IMAGE , "https://xxx.com/xxx.jpg") ,
            GamePreviewBean(PreviewType.IMAGE , "https://xxx.com/xxx.jpg") ,
            GamePreviewBean(PreviewType.IMAGE , "https://xxx.com/xxx.jpg") ,
            GamePreviewBean(PreviewType.IMAGE , "https://xxx.com/xxx.jpg") ,
            GamePreviewBean(PreviewType.IMAGE , "https://xxx.com/xxx.jpg") ,
            GamePreviewBean(PreviewType.IMAGE , "https://xxx.com/xxx.jpg") ,
            GamePreviewBean(PreviewType.IMAGE , "https://xxx.com/xxx.jpg") ,
            GamePreviewBean(PreviewType.IMAGE , "https://xxx.com/xxx.jpg") ,
            GamePreviewBean(PreviewType.IMAGE , "https://xxx.com/xxx.jpg") ,
            GamePreviewBean(PreviewType.IMAGE , "https://xxx.com/xxx.jpg")
        )
    }


    fun queryGameDetail(gameId: Long) {
        setState(DataState.Loading)
        viewModelScope.launch {
            callApi(
                {
                    repository.getGameDetail(gameId)
                } ,
                {
                    when (it) {
                        is ApiResponseState.Failed -> {
                            setState(DataState.NetworkUnavailable)
                        }

                        is ApiResponseState.Succeeded<*> -> {
                            setState(DataState.LoadSuccess)

                            viewModelScope.launch(Dispatchers.IO) {
                                val gameDetailVo = it.dataAs<GameDetailVo>()
                                val toGameDetailBean = repository.toGameDetailBean(gameDetailVo)
                                withContext(Dispatchers.Main) {
//                                    _previewData.value = gameDetailVo?.avatar?.map { vo ->
//                                        GamePreviewBean(PreviewType.IMAGE , vo.url)
//                                    }
                                    toGameDetailBean.let { gameDetailBean ->
                                        _gameDetailData.value = gameDetailBean
                                    }
                                    _isCollected.value = gameDetailVo?.collect ?: false
                                }
                            }
                        }

                        else -> {}
                    }
                } , autoUpdateState = false
            )
        }
    }

    fun toggleCollectStatus(gameId: Long) {
        _isCollected.value = !(_isCollected.value ?: false)
        val status = _isCollected.value ?: true

        viewModelScope.launch {
            callApi(
                {
                    repository.updateGameCollect(
                        gameId ,
                        status
                    )
                } ,
                {
                    when (it) {
                        is ApiResponseState.Failed -> {

                        }

                        is ApiResponseState.Succeeded<*> -> {
                        }

                        else -> {}
                    }
                } , autoUpdateState = false
            )
        }

    }


}
