package com.walisport.module.live.data.repository

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.GameDatabase
import arch.cayenne.lib.database.entity.LiveVideoBean
import arch.cayenne.lib.database.entity.StreamInfoBean
import arch.cayenne.lib.database.entity.VideoSourceBean
import com.walisport.module.live.LiveRemoteManager
import com.walisport.module.live.data.model.VideoResolutionBean
import com.walisport.module.live.utils.NumberToChineseUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * 直播视频的repository,  存储视频源信息
 */
class LiveVideoRepository(
    private val remoteManager: LiveRemoteManager,
    private val database: GameDatabase
) : BaseRepository() {
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    private val liveMatchDao = database.liveMatchDao()
    private val liveVideoDao = database.liveVideoDao()

    var matchId: Long = 0

    fun observeLiveVideoBean(observeMatchId: Long) =
        liveVideoDao.observeLiveVideoBean(observeMatchId).flowOn(Dispatchers.IO)

    fun observeMatchBean(observeMatchId: Long) =
        liveMatchDao.observeMatchById(observeMatchId).flowOn(Dispatchers.IO)

    fun observeAnimationLiveUrl(observeMatchId: Long) =
        liveMatchDao.observeAnimationLiveUrl(observeMatchId).flowOn(Dispatchers.IO)

    fun setPlayingVideoId(id: Int) {
        scope.launch {

            val liveVideoBean = liveVideoDao.queryLiveVideoBean(matchId)
            liveVideoBean?.source?.forEach { it.isPlaying = it.id == id }
            liveVideoDao.updatePlayingId(liveVideoBean?.source ?: emptyList(), matchId)
        }
    }

    fun queryLiveStream() {
        scope.launch {
            //已经有比赛对应的视频列表， 不需要再次拉取
            val liveVideoBean = liveVideoDao.queryLiveVideoBean(matchId)
            if (liveVideoBean?.source?.isNotEmpty() == true) {
                return@launch
            }
            val resp = remoteManager.queryLiveStream(scope, matchId)
            
            val data = resp?.mapIndexed { index, matchLiveStream ->
                VideoSourceBean(
                    id = index,
                    name = matchLiveStream.name,

                    liveStreams = matchLiveStream.liveStreamsList.map {
                        StreamInfoBean(
                            it.name,
                            it.urlSource,
                            formatStreamType(it.streamType),
                            it.rtmpUrl,
                            it.m3U8Url,
                            it.flvUrl,
                            it.language,
                            selected = formatStreamType(it.streamType) == "1080P"
                        )
                    },

                    thumb = "",
                    //0522. 由于提供的视频源无标题和副标题名称，选择视频源这里，除了「动画直播」有标题和副标题，视频流统一根据服务端提供的视频流，标题统一命名视频源一，视频源二等，副标题统一命名纯享版
                    title = "视频源${NumberToChineseUtil.toChinese((index + 1).toLong())}",
//                    title = matchLiveStream.title,
                    subTitle = "纯享版",
                    isPlaying = false,
                    coverUrl = matchLiveStream.coverUrl,
                    anchorName = matchLiveStream.anchorName
                )
            } ?: emptyList()
            //默认自动播放第一条
            data.firstOrNull()?.isPlaying = true
            liveVideoDao.insert(LiveVideoBean(matchId, data))
        }

    }

    suspend fun queryVideoResolutionList(): List<VideoResolutionBean>? {

        val bean = liveVideoDao.queryLiveVideoBean(matchId)
        val playing = bean?.source?.firstOrNull { ele -> ele.isPlaying }

        val list = playing?.liveStreams?.map { VideoResolutionBean(it.streamType, it.selected) }

        return list

    }

    fun changeResolution(resolution: String) {
        scope.launch {
            val bean = liveVideoDao.queryLiveVideoBean(matchId)
            val playing = bean?.source?.firstOrNull { ele -> ele.isPlaying }
            playing?.liveStreams?.forEach {
                it.selected = it.streamType == resolution
            }

            if (bean?.source != null) {
                liveVideoDao.updatePlayingId(bean.source, matchId)
            }
        }
    }

    private fun formatStreamType(streamType:String):String{
        return if (streamType.uppercase(Locale.getDefault()) == "HD") {
            "1080P"
        } else if (streamType.uppercase(Locale.getDefault()) == "SD") {
            "720P"
        } else {
            "540P"
        }
    }
}