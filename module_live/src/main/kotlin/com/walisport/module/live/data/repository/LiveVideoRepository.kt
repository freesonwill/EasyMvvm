package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.LiveVideoBean
import arch.cayenne.lib.database.entity.VideoSourceBean
import com.walisport.module.live.LiveRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 直播视频的repository,  存储视频源信息
 */
class LiveVideoRepository(
    private val remoteManager: LiveRemoteManager,
    private val database: GameDatabase
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    private val matchDao = database.matchDao()
    private val liveVideoDao = database.liveVideoDao()

    var matchId: Long = 0

    fun observeLiveVideoBean(observeMatchId: Long) =
        liveVideoDao.observeLiveVideoBean(observeMatchId)

    fun observeMatchBean(observeMatchId: Long) = matchDao.observeMatchById(observeMatchId)

    fun setPlayingVideoId(id: Int) {
        scope.launch {

            val liveVideoBean = liveVideoDao.queryLiveVideoBean(matchId)
            liveVideoBean?.source?.forEach { it.isPlaying = it.id == id }
            liveVideoDao.updatePlayingId(liveVideoBean?.source ?: emptyList(), matchId)
        }
    }

    fun queryLiveStream() {
        scope.launch {
            val resp = remoteManager.queryLiveStream(scope, matchId)
            
            val data = resp?.mapIndexed { index, matchLiveStream ->
                VideoSourceBean(
                    id = index,
                    name = matchLiveStream.name,
                    urlSource = matchLiveStream.urlSource,
                    streamType = matchLiveStream.streamType,
                    rtmpUrl = matchLiveStream.rtmpUrl,
                    m3U8Url = matchLiveStream.m3U8Url,
                    flvUrl = matchLiveStream.flvUrl,
                    language = matchLiveStream.language,

                    thumb = "",
                    title = "",
                    subTitle = "",
                    isPlaying = false,
                )
            } ?: emptyList()
            //默认自动播放第一条
            data.firstOrNull()?.isPlaying = true
            liveVideoDao.insert(LiveVideoBean(matchId, data))
        }

    }
}