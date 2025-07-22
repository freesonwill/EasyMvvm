package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.database.entity.LiveVideoBean
import arch.cayenne.lib.database.entity.VideoSourceBean
import com.walisport.module.live.LiveRemoteManager
import com.walisport.module.live.utils.NumberToChineseUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * 比赛动画的repository,  获取比赛对应的动画源
 */
class LiveMatchAnimationRepository(
    database: GameDatabase
) : BaseRepository() {

    private val liveMatchDao = database.liveMatchDao()
    private val liveVideoDao = database.liveVideoDao()

    var matchId: Long = 0

    fun observeAnimationLiveUrl(observeMatchId: Long) =
        liveMatchDao.observeAnimationLiveUrl(observeMatchId).flowOn(Dispatchers.IO)

    fun observeLiveVideoBean(observeMatchId: Long) =
        liveVideoDao.observeLiveVideoBean(observeMatchId).flowOn(Dispatchers.IO)

}