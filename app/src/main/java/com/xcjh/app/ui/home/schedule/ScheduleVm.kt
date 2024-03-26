package com.xcjh.app.ui.home.schedule

import android.animation.Animator
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.drake.brv.utils.bindingAdapter
import com.google.gson.Gson
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.R
import com.xcjh.app.bean.CompetitionBean
import com.xcjh.app.bean.CompetitionRed
import com.xcjh.app.bean.HotMatchBean
import com.xcjh.app.bean.HotMatchReq
import com.xcjh.app.bean.JsonBean
import com.xcjh.app.bean.MatchBean
import com.xcjh.app.bean.PostSchMatchListBean
import com.xcjh.app.bean.TimeConstantsDat
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.callback.livedata.BooleanLiveData
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request
import org.json.JSONArray
import org.json.JSONObject


class ScheduleVm : BaseViewModel() {
    private var pageNo = 1

    var hotMatch = UnPeekLiveData<ArrayList<HotMatchBean>>()
    var hotMatchList = UnPeekLiveData<ListDataUiState<MatchBean>>()
    var noticeData = UnPeekLiveData<Boolean>()
    var unnoticeData = UnPeekLiveData<Boolean>()
    /**
     * 关注
     */
    fun getNotice(matchId: String,matchType:String,iv:ImageView,lott:LottieAnimationView,index:Int,recyview:RecyclerView) {
        request(
            { apiService.getNoticeRaise(matchId,matchType) },

            {
                var num=0
                lott!!.setAnimation("shoucang1.json")
                lott!!.loop(false)
                lott!!.setRepeatCount(0)
                lott!!.playAnimation()

                // mview1!!.setBackgroundResource(R.drawable.sc_shoucang_icon2)
                lott!!.addAnimatorListener(object :
                    Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        if(num==0){
                            iv!!.setBackgroundResource(R.drawable.sc_shoucang_icon2)
                            recyview.bindingAdapter.getModel<MatchBean>(index).focus = true
                            recyview.bindingAdapter.notifyItemChanged(index)
                            lott!!.visibility= View.GONE
                            myToast(appContext.getString(R.string.collect_success))
                            Log.i("VVVVVVVVVV","11111111111111111111111")
                        }
                        num=1
//                        myToast(appContext.getString(R.string.collect_success))
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })
                //noticeData.value=true
            }, {
                //请求失败
               // myToast(it.errorMsg)
            }, false
        )
    }
    /**
     * 取消关注
     */
    fun getUnnotice(matchId: String,matchType:String,iv:ImageView,lott:LottieAnimationView,index:Int,recyview:RecyclerView) {
        request(
            { apiService.getUnNoticeRaise(matchId,matchType) },

            {
                lott!!.setAnimation("shoucang2.json")
                lott!!.loop(false)
                lott!!.playAnimation()

                // mview1!!.setBackgroundResource(R.drawable.sc_shoucang_icon2)
                lott!!.addAnimatorListener(object :
                    Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        iv!!.setBackgroundResource(R.drawable.sc_shoucang_icon1)
                        recyview.bindingAdapter.getModel<MatchBean>(index).focus = false
                        recyview.bindingAdapter.notifyItemChanged(index)
                        lott!!.visibility= View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })
                //unnoticeData.value=true
            }, {
                //请求失败
              //  myToast(it.errorMsg)
            }, false
        )
    }
    /**
     * 赛程热门赛事列表查询
     */
    fun getHotMatchData(id: String,staus:String) {
        request(
            { apiService.getHotMatch(HotMatchReq(id, staus)) },

            {
                hotMatch.value=it
            }, {
                //请求失败

            }, false
        )
    }


    /**
     * 获取比赛日期 type 0推荐    1 是足球   2是篮球    3是赛果
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun getMatchTimeCount(type: String) {
        var mact=type
        var data= CompetitionRed()
        if(type.equals("0")){
            data.matchType=null
        }else if(type.equals("1")){
            data.matchType="1"
        }else if(type.equals("2")){
                data.matchType="2"
          }else{
            data.matchType=null
            data.forward=false
          }
//        data.matchType=type


        request(
            { apiService.getMatchTimeCount(data) },

            {




//               var jsonBean= convertToCustomFormat( formatDates(it))

               var jsonBean= convertToJsonObjectNew ( formatDates(it))


                if(mact.equals("0")){
//                    var ddd=formatDates(it)
//                    ddd.forEach {
//                        Log.i("NNNNNNNN","====="+it.time)
//                    }
                    TimeConstantsDat.options1ItemsAll = jsonBean
                }else  if(mact.equals("1")){

                    TimeConstantsDat.options1ItemsFootball = jsonBean

                }else  if(mact.equals("2")){

                    TimeConstantsDat.options1ItemsBasketball = jsonBean

                }else{
                    TimeConstantsDat.options1ItemsSaiguo = jsonBean
                    TimeConstantsDat.saiYi=TimeConstantsDat.options1ItemsSaiguo.size-1
                    TimeConstantsDat.saiYiNew=TimeConstantsDat.options1ItemsSaiguo.size-1
                }


                for (i in jsonBean.indices) { //遍历省份
                    val cityList = java.util.ArrayList<String>() //该省的城市列表（第二级）
                    val province_AreaList =
                        java.util.ArrayList<java.util.ArrayList<String>>() //该省的所有地区列表（第三极）
                    for (c in 0 until jsonBean[i].cityList.size) { //遍历该省份的所有城市
                        val cityName = jsonBean[i].cityList[c].name
                        cityList.add(cityName) //添加城市
                        val city_AreaList = java.util.ArrayList<String>() //该城市的所有地区列表

                        //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                        /*if (jsonBean.get(i).getCityList().get(c).getArea() == null
                                || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                            city_AreaList.add("");
                        } else {
                            city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                        }*/

                        city_AreaList.addAll(jsonBean[i].cityList[c].area)
                        province_AreaList.add(city_AreaList) //添加该省所有地区数据
                    }

                    if(mact.equals("0")){
                        //添加城市数据
                        TimeConstantsDat.options2ItemsAll.add(cityList)
                        // 添加地区数据
                        TimeConstantsDat.options3ItemsAll.add(province_AreaList)
                    }else  if(mact.equals("1")){
                        //添加城市数据
                        TimeConstantsDat.options2ItemsFootball.add(cityList)
                        // 添加地区数据
                        TimeConstantsDat.options3ItemsFootball.add(province_AreaList)

                    }else  if(mact.equals("2")){
                        //添加城市数据
                        TimeConstantsDat.options2ItemsBasketball.add(cityList)
                        // 添加地区数据
                        TimeConstantsDat.options3ItemsBasketball.add(province_AreaList)
                    }else{
                        //添加城市数据
                        TimeConstantsDat.options2ItemsSaiguo.add(cityList)
                        // 添加地区数据
                        TimeConstantsDat.options3ItemsSaiguo.add(province_AreaList)

                        TimeConstantsDat.saiEr=TimeConstantsDat.options1ItemsSaiguo.get(TimeConstantsDat.options1ItemsSaiguo.size-1).cityList.size-1
                        TimeConstantsDat.saiErNew=TimeConstantsDat.options1ItemsSaiguo.get(TimeConstantsDat.options1ItemsSaiguo.size-1).cityList.size-1
                        TimeConstantsDat.saiSan=TimeConstantsDat.options1ItemsSaiguo.get(TimeConstantsDat.options1ItemsSaiguo.size-1).cityList[TimeConstantsDat.saiEr].area.size-1
                        TimeConstantsDat.saiSanNew=TimeConstantsDat.options1ItemsSaiguo.get(TimeConstantsDat.options1ItemsSaiguo.size-1).cityList[TimeConstantsDat.saiEr].area.size-1

                    }

                }

//                hotMatch.value=it
            }, {
                //请求失败

            }, false
        )
    }
    //添加0
    fun formatDates(dataList: ArrayList<CompetitionBean>): List<CompetitionBean> {
        val formattedDates = mutableListOf<CompetitionBean>()

        for (data in dataList) {

            val parts = data.time.split("-")
            val month = parts[1].toInt()
            val day = parts[2].toInt()

            val formattedMonth = if (month < 10) "0$month" else "$month"
            val formattedDay = if (day < 10) "0$day" else "$day"

            val formattedTime = "${parts[0]}-${formattedMonth}月-${formattedDay}日"
            var dat=CompetitionBean(formattedTime,data.num)
            formattedDates.add(dat)
        }


        return formattedDates
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun convertToCustomFormat(list: List<CompetitionBean>): ArrayList<JsonBean> {
        val groupedData = groupByDateNew(list)
        val jsonList = ArrayList<JsonBean>()

        for ((year, monthMap) in groupedData) {
            val cityList = ArrayList<JsonBean.CityBean>()
            for ((month, dayList) in monthMap) {
                val cityBean = JsonBean.CityBean(month, dayList.map { it.toString() })
                cityList.add(cityBean)
            }
            val jsonBean = JsonBean(year, cityList)
            jsonList.add(jsonBean)
        }

        return jsonList
    }


    fun convertToJsonObjectNew(competitionList: List<CompetitionBean>): ArrayList<JsonBean> {
        val jsonList = arrayListOf<JsonBean>()

        val groupedByYear = competitionList.groupBy { it.time.substring(0, 4) }

        for ((year, competitions) in groupedByYear) {
            val groupedByMonth = competitions.groupBy { it.time.substring(5, 7) }

            val cityList = mutableListOf<JsonBean.CityBean>()
            for ((month, competitionsInMonth) in groupedByMonth) {
                val dayList = competitionsInMonth.map { it.time.substring(9, 11) + appContext.getString(R.string.day_txt) }
                val formattedMonth = String.format("%02d", month.toInt())
                val cityBean = JsonBean.CityBean("${formattedMonth}${appContext.getString(R.string.month_txt)}", dayList)
                cityList.add(cityBean)
            }

            val jsonBean = JsonBean(year, cityList)
            jsonList.add(jsonBean)
        }

        return jsonList
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun groupByDateNew(list: List<CompetitionBean>): Map<String, Map<String, List<Int>>> {
        val groupedData = mutableMapOf<String, MutableMap<String, MutableList<Int>>>()

        for (item in list) {
            val dateParts = item.time.split("-")
            val year = dateParts[0]
            val month = dateParts[1]
            val day = dateParts[2].toInt()

            groupedData.computeIfAbsent(year) { mutableMapOf() }
                .computeIfAbsent(month) { mutableListOf() }
                .add(day)
        }

        return groupedData
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun groupByDate(list: List<CompetitionBean>): Map<String, Map<String, List<Int>>> {
        val groupedData = mutableMapOf<String, MutableMap<String, MutableList<Int>>>()

        for (item in list) {
            val dateParts = item.time.split("-")
            val year = dateParts[0]
            val month = dateParts[1]
            val day = dateParts[2].toInt()

            groupedData.computeIfAbsent(year) { mutableMapOf() }
                .computeIfAbsent(month) { mutableListOf() }
                .add(day)
        }

        return groupedData
    }

    /**
     * 赛程
     */
    fun getHotMatchDataList(isRefresh: Boolean,isLoading: Boolean, bean: PostSchMatchListBean) {
        LogUtils.d("请求参数==="+com.alibaba.fastjson.JSONObject.toJSONString(bean))
        if (isRefresh) {
            pageNo = 1
        }
        request(
            { apiService.getHotMatchChildList(bean) },

            {
                pageNo
                hotMatchList.value = ListDataUiState(
                    isSuccess = true,
                    isRefresh = isRefresh,
                    isEmpty = it!!.records.isEmpty(),
                    isFirstEmpty = isRefresh && it.records.isEmpty(),
                    listData = it.records
                )
            }, {
                //请求失败
                hotMatchList.value = ListDataUiState(
                    isSuccess = false,
                    isRefresh = isRefresh,
                    errMessage = it.errorMsg,
                    listData = arrayListOf()
                )
                myToast(it.errorMsg)

            }, isLoading
        )
    }



}