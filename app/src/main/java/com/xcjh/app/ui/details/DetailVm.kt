package com.xcjh.app.ui.details

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.bean.*
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.bean.UpdateUiState
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request

/**
 * 公用vm,缺点：创建多个无用的对象占用较多资源
 */
class DetailVm : BaseViewModel() {
    private var pageNo = 1
    var tt = 1
    //主播信息
    var anchorInfo = UnPeekLiveData<AnchorListBean>()

    var detail = UnPeekLiveData<MatchDetailBean>()
    var scrollTextList = UnPeekLiveData<UpdateUiState<ArrayList<ScrollTextBean>>>()
    var showAd = UnPeekLiveData<UpdateUiState<ScrollTextBean>>()
    var anchor = UnPeekLiveData<DetailAnchorBean>()

    var odds = UnPeekLiveData<OddsBean>()
    var foot = UnPeekLiveData<FootballLineupBean>()
    var basket = UnPeekLiveData<BasketballLineupBean>()

    var basketScore = UnPeekLiveData<BasketballScoreBean>()
    var basketStatus = UnPeekLiveData<BasketballSBean>()   //篮球赛况下表格数据
    var footStatus = UnPeekLiveData<ArrayList<StatusBean>>()     //足球赛况表格数据

    //获取比赛详情
    fun getMatchDetail(matchId: String, matchType: String?,showD:Boolean=false) {
        request(
            {
                apiService.getMatchDetail(matchId, matchType)
            }, {
                detail.value = it
            }, {
                myToast(it.errorMsg)
            }, showD
        )
    }

    //查询滚动文字列表
    fun getScrollTextList() {
        request(
            {
                apiService.getScrollTextList()
            }, {
                scrollTextList.value = UpdateUiState(true, it)
            }, {
                scrollTextList.value = UpdateUiState(false, null, it.errorMsg)
              //  myToast(it.errorMsg)
            }, false
        )
    }

    //获取比赛详情
    fun getShowAd() {
        request(
            {
                apiService.getShowAd()
            }, {
                showAd.value = UpdateUiState(true, it)
            }, {
                showAd.value = UpdateUiState(false, null, it.errorMsg)
                //myToast(it.errorMsg)
            }, false
        )
    }

    //获取主播详情接口，DetailAnchorFragment界面调用
    fun getDetailAnchorInfo(id: String?="") {
        request({
            apiService.getDetailAnchorInfo(id?:"")
        }, {
            anchor.postValue(it)
        }, {
           // myToast(it.errorMsg)
        })
    }
    var isfocus = UnPeekLiveData<Boolean>()
    //关注主播接口，DetailAnchorFragment界面调用
    fun followAnchor(id: String) {
        request({
            apiService.getNoticeUser(id)
        }, {
            isfocus.value=true
        }, {
            myToast(it.errorMsg)
            isfocus.value=false
        },true)
    }
    var isUnFocus = UnPeekLiveData<Boolean>()
    //取消关注主播接口，DetailAnchorFragment界面调用
    fun unFollowAnchor(id: String) {
        request({
            apiService.unfollowAnchor(id)
        }, {
            isUnFocus.value=true
        }, {
            myToast(it.errorMsg)
            isUnFocus.value=true
        },true)
    }

    //获取指数接口，DetailAnchorFragment界面调用
    fun getOddsInfo(matchId: String) {
        request({
            apiService.getOddsInfo(matchId)
        }, {
            odds.value = it
        }, {

        })
    }
    var liveList = UnPeekLiveData<ListDataUiState<BeingLiveBean>>()
    /**
     * 获取首页热门比赛  type 1足球，2篮球
     */
    fun getNowLive(isRefresh: Boolean, type: String, exceptId:String) {
        if (isRefresh) {
            pageNo = 1
        }
        request({
            apiService.getNowLive(LiveReq(current = pageNo, size = 10, matchType = type, exceptId = exceptId))
        }, {
            pageNo++
            liveList.value = ListDataUiState(
                isSuccess = true,
                isRefresh = isRefresh,
                isEmpty = it.records.isEmpty(),
                isFirstEmpty = isRefresh && it.records.isEmpty(),
                listData = it.records
            )
        }, {
            liveList.value = ListDataUiState(
                isSuccess = false,
                isRefresh = isRefresh,
                errMessage = it.errorMsg,
                listData = arrayListOf()
            )
        })
    }

    /**
     * 查询比赛阵容-足球
     */
    fun getFootballLineUp(matchId: String) {
        request({
            apiService.getFootballLineUp(matchId)
        }, {
            foot.value = it
        }, {
            //myToast(it.errorMsg)
        })
    }

    /**
     * 查询比赛阵容-篮球
     */
    fun getBasketballLineUp(matchId: String) {
        request({
            apiService.getBasketballLineUp(matchId)
        }, {
            basket.value = it
        }, {
            //myToast(it.errorMsg)
        })
    }

    /**
     * 查询比赛得分-篮球
     */
    fun getBasketballScore(matchId: String) {
        request({
            apiService.getBasketballScore(matchId)
        }, {
            basketScore.value = it
        }, {
            //myToast(it.errorMsg)
           /* val it= BasketballScoreBean()
            it.homeScoreList= arrayListOf(10,12,15,18,10)
            it.awayScoreList= arrayListOf(8,14,12,20,10)
            it.homeOverTimeScoresList= arrayListOf(5,6,7)
            it.awayOverTimeScoresList= arrayListOf(5,3,5)
            basketScore.value = it*/
        })
    }

    /**
     * 查询比赛技术统计-篮球
     */
    fun getBasketballStatus(matchId: String) {
        request({
            apiService.getBasketballStatus(matchId)
        }, {
            basketStatus.value = it
        }, {
            //myToast(it.errorMsg)
           // basketStatus.value = BasketballSBean()
        })
    }

    /**
     * 查询比赛技术统计-足球
     */
    fun getFootballStatus(matchId: String) {
        request({
            apiService.getFootballStatus(matchId)
        }, {
            footStatus.value = it
        }, {
            //myToast(it.errorMsg)
        })
    }

    /**
     * 查询比赛技术统计-足球
     */
    var text = UnPeekLiveData<ArrayList<LiveTextBean>>()
    fun getLiveEvent(matchId: String) {
        request({
            apiService.getLiveEvent(matchId)
        }, {
            text.value = it
        }, {
           /* var  it= arrayListOf<LiveTextBean>()
            it.add(LiveTextBean("11111",1,1,"2", type = 1))
            it.add(LiveTextBean("22222",1,2,"4", type = 2))
            it.add(LiveTextBean("33333",1,1,"8", type = 3))
            it.add(LiveTextBean("33333",1,0,"8", type = 3))
            it.add(LiveTextBean("4444as=======jfakljsflksj水=========电费开始缴费时间4",1,2,"16", type = 4))
            it.add(LiveTextBean("5555",1,1, "32",type = 5))
            it.add(LiveTextBean("6666",1,2, "64",type = 6))
            it.add(LiveTextBean("s==6666=====jfakljsflksj水电========费开始缴费时间4",1,0, "64",type = 6))
            it.add(LiveTextBean("9999",1,2, "99",type = 9))
            it.add(LiveTextBean("7777",1,1, "100",type = 7))
            it.add(LiveTextBean("8888",1,2, "120",type = 8))
            text.value = it*/
        })
    }

    /**
     * 查询重要事件-足球
     */
    var incidents = UnPeekLiveData<ArrayList<IncidentsBean>>()
    fun getIncidents(matchId: String) {
        request({
            apiService.getIncidents(matchId)
        }, {
            incidents.value = it
        }, {

        })
    }

    /**
     * 增加观看直播历史
     */
    fun addLiveHistory(liveId: String?) {
        request({
            apiService.addLiveHistory(liveId)
        },{

        })
    }

}