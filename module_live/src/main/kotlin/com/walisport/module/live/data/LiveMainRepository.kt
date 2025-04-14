package com.walisport.module.live.data

import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.database.dao.LiveVideoDao
import arch.cayenne.lib.database.entity.LiveVideoBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveMainRepository(private val liveVideoDao: LiveVideoDao) : BaseRepository() {

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun observeLiveVideoBean() = liveVideoDao.observeLiveVideoBean()

    fun setPlayingVideoUrl(url: String) {
        scope.launch {
            liveVideoDao.updateUrl(url)
        }
    }

    fun addMockData() {
        scope.launch {
            liveVideoDao.insert(
                LiveVideoBean(
                    1,
                    "http://thinkingform.com/wp-content/uploads/2017/09/video-sample-mp4.mp4?_=1"
                )
            )
        }
    }


}