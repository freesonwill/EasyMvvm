package com.walisport.module.popup.slot.data

import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.repository.BaseRepository
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.websocket.WebSocketManager
import com.walisport.module.popup.slot.data.PopupSlotBean.Companion.UNINITIALIZED_ANCHOR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PopupSlotRepository(
    override val scope: CoroutineScope ,
    private val socketManager: WebSocketManager ,
    private val manager: UserDataManager
) : BaseRepository() {

    private val _popupSlotDataListLiveData: MutableLiveData<List<PopupSlotBean>> =
        MutableLiveData()
    val popupSlotDataListLiveData: MutableLiveData<List<PopupSlotBean>>
        get() = _popupSlotDataListLiveData


    private var hasFetched = false


    fun getPopupSlotData() {
        //该方法仅被执行一次
        if (hasFetched) return
        hasFetched = true
        scope.launch(Dispatchers.IO) {

            delay(300)

            withContext(Dispatchers.Main) {
                _popupSlotDataListLiveData.value = mockData()
            }

        }
    }

    private fun mockData(): List<PopupSlotBean> {
        return listOf(
            PopupSlotBean(
                listOf(
                    PopupSlotDataModel(
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSYR7KdbJuSCOOE2ddY6VX3hh8tn_RfFtPgSA&s" ,
                        "https://www.google.com/"
                    ) ,
                    PopupSlotDataModel(
                        "https://pic.5tu.cn/uploads/allimg/2410/pic_5tu_big_6672913_6720a1c561819-thumb-650.jpg" ,
                        "https://www.facebook.com/"
                    ) ,
                    PopupSlotDataModel(
                        "https://pic.5tu.cn/uploads/allimg/2404/pic_5tu_big_6672913_6627b3e462e70-thumb-650.jpg" ,
                        "https://www.twitter.com/"
                    )
                )
            ) , PopupSlotBean(
                listOf(
                    PopupSlotDataModel(
                        "https://img.freepik.com/premium-photo/scenic-view-lake-mountains-against-sky_1048944-27460488.jpg?semt=ais_se_enriched&w=740&q=80" ,
                        "https://www.google.com/"
                    ) ,
                    PopupSlotDataModel(
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcStv-haBrS16nwCmPCYl7UABWeeIJfnQQ_-ZQ&s" ,
                        "https://www.facebook.com/"
                    ) ,
                    PopupSlotDataModel(
                        "https://bpic.wotucdn.com//original/36/22/17/36221765-1abaf60f5484fd4315504e22fa79c3d6.png!waterxwebp320x556" ,
                        "https://www.twitter.com/"
                    )
                )
            )
        )

    }

}


/**
 *
 * 小弹窗广告位数据模型
 * @date: 2025/12/9 14:29
 * @description:
 */
data class PopupSlotDataModel(val imgUrl: String , val contentUrl: String)

data class PopupSlotBean(
    val data: List<PopupSlotDataModel> ,
    var anchorX: Float = UNINITIALIZED_ANCHOR ,//显示位置X
    var anchorY: Float = UNINITIALIZED_ANCHOR, //显示位置
    var show: Boolean = true
) {
    companion object {
        const val UNINITIALIZED_ANCHOR = -1f
    }
}