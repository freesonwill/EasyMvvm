package com.walisport.module.live.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.LiveVideoDao
import arch.cayenne.lib.database.entity.LiveVideoBean
import arch.cayenne.lib.database.entity.VideoSourceBean
import com.walisport.module.live.LiveRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveMainRepository(
    private val liveVideoDao: LiveVideoDao, private val remoteManager: LiveRemoteManager
) : BaseRepository() {

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun observeLiveVideoBean(matchId: Long) = liveVideoDao.observeLiveVideoBean(matchId)

    fun setPlayingVideoId(sources: List<VideoSourceBean>, matchId: Long) {
        scope.launch {
            liveVideoDao.updatePlayingId(sources, matchId)
        }
    }

    fun queryLiveStream(matchId: Long) {
        scope.launch {
            val resp = remoteManager.queryLiveStream(scope, matchId)
            liveVideoDao.deleteAll()

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