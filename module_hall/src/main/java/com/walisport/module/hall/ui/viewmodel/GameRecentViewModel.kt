package com.walisport.module.hall.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtils
import com.walisport.module.hall.data.Avatar
import com.walisport.module.hall.data.GameContentData
import com.walisport.module.hall.data.HallRepository
import com.walisport.module.hall.data.HotColdType
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel
import kotlin.random.Random

@KoinViewModel
class GameRecentViewModel : BaseViewModel() {

    private val repository: HallRepository by inject { parametersOf(viewModelScope) }

    private val _gameRecentList = UnPeekLiveData<List<GameContentData>>()
    val gameRecentList: LiveData<List<GameContentData>> = _gameRecentList

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