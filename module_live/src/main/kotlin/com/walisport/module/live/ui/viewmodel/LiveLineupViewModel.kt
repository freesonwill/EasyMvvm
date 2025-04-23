package com.walisport.module.live.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtils
import com.walisport.module.live.data.LiveLineupRepository
import galaxy.client.proto.Sloth
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class LiveLineupViewModel : BaseViewModel() {
    private val repository: LiveLineupRepository by inject { parametersOf(viewModelScope) }
    private val _matchLineupDetail = MutableLiveData<Sloth.MatchLineupDetail?>()
    val matchLineupDetail: LiveData<Sloth.MatchLineupDetail?> = _matchLineupDetail
    fun geMatchLineupDetail(matchId: Long) {
        viewModelScope.launch {
            _matchLineupDetail.value = repository.getMatchLiveReq(matchId)
        }
    }

    /**
     * 阵容排列
     * xMax = 100
     * yMin = 100
     * //队员头像名字控件宽高/2
     * headViewHeight =  50/2
     * headViewWidth = 50/2
     * //获取到一份控件的xy轴
     * sX = 1920
     * sY = 2330
     * viewHeight = sX/100
     * viewWidth =sY/100
     * //根据后台返回的xy 例如; x = 12 y=50 显示具体位置
     * serviceX = 12
     * serviceY = 50
     * x = screenX*screenX-viewHeight
     * y = screenY*screenY-viewWidth
     */

    //isBottom false 正常排列。 true为倒序排列
    fun lineupArrangementXY(headViewNumber: Int, viewNumber: Int, serviceNumber: Int,isBottom:Boolean = false): Int {
        val max = 100.0//最大份数
        val oneViewNumber = (viewNumber.toFloat() / max)//每一份的像素
        val headNumber = headViewNumber.toFloat() / 2.0//队员头像名字控件高度
        LogUtils.d("lineupArrangementX-----headViewNumber-${headViewNumber},viewNumber-${viewNumber},serviceNumber-${serviceNumber}")
        LogUtils.d("lineupArrangementX-----oneViewNumber-${oneViewNumber},headNumber-${headNumber},return-${oneViewNumber * serviceNumber - headNumber}")
        return if (isBottom){
            (oneViewNumber * (max-serviceNumber.toFloat()) - headNumber).toInt()
        }else{
            (oneViewNumber * serviceNumber.toFloat() - headNumber).toInt()
        }
    }
}