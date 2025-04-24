package com.walisport.module.live.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.LiveVideoDao
import arch.cayenne.lib.database.entity.LiveVideoBean
import com.walisport.module.live.LiveRemoteManager
import galaxy.client.proto.Sloth
import galaxy.common.proto.Common
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveMainRepository(
    private val liveVideoDao: LiveVideoDao, private val remoteManager: LiveRemoteManager
) : BaseRepository() {

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun observeLiveVideoBean() = liveVideoDao.observeLiveVideoBean()


    fun setPlayingVideoId(id: Int) {
        scope.launch {
            liveVideoDao.updatePlayingId(id)
        }
    }

    fun queryLiveStream(matchId: Long) {
        scope.launch {
            val resp = remoteManager.queryLiveStream(scope, matchId)
            liveVideoDao.deleteAll()
            val data = resp?.map {
                LiveVideoBean(
                    id = 0,
                    name = it.name,
                    urlSource = it.urlSource,
                    streamType = it.streamType,
                    rtmpUrl = it.rtmpUrl,
                    m3U8Url = it.m3U8Url,
                    flvUrl = it.flvUrl,
                    language = it.language,

                    thumb = "",
                    title = "",
                    subTitle = "",
                    isPlaying = false,
                )
            } ?: emptyList()
            //默认自动播放第一条
            data.firstOrNull()?.isPlaying = true
            liveVideoDao.insert(data)
        }
    }

    suspend fun getMatchReq( matchId: Long ):Common.Match?  {
        return remoteManager.getMatchReq(scope, matchId)?.firstOrNull{it.matchId==matchId}
    }

}