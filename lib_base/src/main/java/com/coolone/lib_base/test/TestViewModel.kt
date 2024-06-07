package com.coolone.lib_base.test

import com.coolone.lib_base.mvi.base.BaseViewModel
import com.coolone.lib_base.mvi.base.IUiIntent
import com.coolone.lib_base.mvi.base.IUiState

data class Banner(val name: String)
data class Article(val title: String)

sealed class BannerUiState {
    object INIT : BannerUiState()
    data class SUCCESS(val models: List<Banner>) : BannerUiState()
}

sealed class ArticleUiState {
    object INIT : ArticleUiState()
    data class SUCCESS(val models: Article) : ArticleUiState()
}

data class MainState(
    val bannerUiState: BannerUiState,
    val articleUiState: ArticleUiState
) : IUiState

/**
 * 事件
 */
sealed class MainIntent : IUiIntent {
    //不要参数逇使用object
    object GetBanner : MainIntent()

    //带参数的使用data class
    data class GetDetail(val page: Int) : MainIntent()
}

/**
 * 单个数据源：MainState,MainIntent
 * MainState是对状态的整合
 * MainIntent是对事件的整合
 */
class TestViewModel : BaseViewModel<MainState, MainIntent>() {

    override fun initUiState(): MainState {
        return MainState(BannerUiState.INIT, ArticleUiState.INIT)
    }

    override fun handleIntent(intent: MainIntent) {
        when (intent) {
            MainIntent.GetBanner -> {
                //进行网络请求
            }

            is MainIntent.GetDetail -> {
                //获取详情
            }
        }
    }

}