package com.walisport.module.hall.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.hall.data.Avatar
import com.walisport.module.hall.data.GameAllContentData
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.data.HallRepository
import com.walisport.module.hall.data.HotColdType
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel
import kotlin.random.Random

@KoinViewModel
class GameAllViewModel : BaseViewModel() {

    private val repository: HallRepository by inject { parametersOf(viewModelScope) }

    private val _gameRecentList = UnPeekLiveData<List<GameAllContentData>>()
    val gameRecentList: UnPeekLiveData<List<GameAllContentData>> = _gameRecentList


    fun mockAllList(page:Int){

        val mockData = listOf(

            GameAllContentData(
                id = "11${page}".toLong(),
                name = "热门",
                gameList = mockList(page)
            )
            ,

            GameAllContentData(
                id = "12${page}".toLong(),
                name = "棋牌",
                gameList = mockList(page)
            )
            ,

            GameAllContentData(
                id = "13${page}".toLong(),
                name = "老虎机",
                gameList = mockList(page)
            )

            ,

            GameAllContentData(
                id = "14${page}".toLong(),
                name = "捕鱼",
                gameList = mockList(page)
            )

            ,

            GameAllContentData(
                id = "15${page}".toLong(),
                name = "真人",
                gameList = mockList(page)
            )
            ,

            GameAllContentData(
                id = "16${page}".toLong(),
                name = "原创",
                gameList = mockList(page)
            )
            ,

            GameAllContentData(
                id = "17${page}".toLong(),
                name = "彩票",
                gameList = mockList(page)
            ),
            GameAllContentData(
                id = "18${page}".toLong(),
                name = "电竞",
                gameList = mockList(page)
            )
        )
        _gameRecentList.postValue(mockData)

    }


   private fun mockList(page:Int):List<GameContentData>{
        val mockData = listOf(
            GameContentData(
                id = "${page}1".toLong(),
                name = "${page}1",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/1.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.COLD
            ),
            GameContentData(
                id = "${page}2".toLong(),
                name = "${page}2",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/2.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.HOT
            ),
            GameContentData(
                id = "${page}3".toLong(),
                name = "${page}3",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/3.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.COLD
            ),
            GameContentData(
                id = "${page}4".toLong(),
                name = "${page}4",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/4.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.HOT
            ),
            GameContentData(
                id = "${page}5".toLong(),
                name = "${page}5",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/5.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.HOT
            ),
            GameContentData(
                id = "${page}6".toLong(),
                name = "${page}6",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/6.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.COLD
            ),
            GameContentData(
                id = "${page}7".toLong(),
                name = "${page}7",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/7.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.HOT
            ),
            GameContentData(
                id = "${page}8".toLong(),
                name = "${page}8",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/8.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.HOT
            ),
            GameContentData(
                id = "${page}9".toLong(),
                name = "${page}9",
                avatar = Avatar(
                    url = "https://eu.xa148.com/gameresource/games/9.avif",
                    thumbhash = "JTqGLAQPcwaGh4h3cgq2moiAgHB4CAiHAA==",
                    css = "--ar: 0.76; --c1: #efd94b; --css-bg: radial-gradient(...);"
                ),
                online = Random.nextInt(10, 32767),
                reward = 0.95,
                hasMore = true,
                hotOrCold = HotColdType.COLD
            )
        )
        return mockData
    }
}