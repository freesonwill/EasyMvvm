package com.walisport.module.live.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.LiveVideoDao
import arch.cayenne.lib.database.entity.LiveVideoBean
import com.walisport.module.live.LiveRemoteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveMainRepository(
    private val liveVideoDao: LiveVideoDao, private val remoteManager: LiveRemoteManager
) : BaseRepository() {

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun observeLiveVideoBean() = liveVideoDao.observeLiveVideoBean()

    fun setPlayingVideoUrl(url: String) {
        scope.launch {
            liveVideoDao.updateUrl(url)
        }
    }

    fun addMockData() {
        scope.launch {
            if (liveVideoDao.queryCount() < 1) {
                liveVideoDao.insert(
                    LiveVideoBean(
                        1,
                        "http://thinkingform.com/wp-content/uploads/2017/09/video-sample-mp4.mp4?_=1"
                    )
                )
            }
        }
    }

    fun liveStream(matchId: Int) {
        scope.launch {
            val resp = remoteManager.liveStream(scope, matchId)

        }

    }


}