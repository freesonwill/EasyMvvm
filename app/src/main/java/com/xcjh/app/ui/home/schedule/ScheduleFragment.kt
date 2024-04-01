package com.xcjh.app.ui.home.schedule

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.CurrentIndex
import com.xcjh.app.bean.JsonBean
import com.xcjh.app.bean.TimeConstantsDat
import com.xcjh.app.databinding.FrCourseBinding
import com.xcjh.app.utils.GetJsonDataUtil
import com.xcjh.app.vm.MainVm
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.LiveStatus
import com.xcjh.app.websocket.bean.ReceiveChangeMsg
import com.xcjh.app.websocket.listener.LiveStatusListener
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.bindViewPager2
import com.xcjh.base_lib.utils.initActivity
import org.json.JSONArray


class ScheduleFragment : BaseFragment<MainVm, FrCourseBinding>() {
    private val mFragments: ArrayList<Fragment> = ArrayList<Fragment>()
    private var mTitles: Array<out String>? = null
    private val mtypes = arrayOf("0", "1", "2", "3")
    private val status = arrayOf("", "", "", "99")
    var tags = "ScheduleFragment"
    var index = 0
    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this)
            .statusBarDarkFont(true)//黑色
            .navigationBarColor(R.color.c_ffffff)
            .navigationBarDarkIcon(true)
            .titleBar(mDatabind.rlTitle)
            .init()
        initEvent()
        MyWsManager.getInstance(requireActivity())!!
            .setLiveStatusListener(tags, object : LiveStatusListener {

                override fun onChangeReceive(chat: ArrayList<ReceiveChangeMsg>) {
                    super.onChangeReceive(chat)
                    appViewModel.appPushMsg.postValue(chat)
                }

                override fun onOpenLive(bean: LiveStatus) {
                    super.onOpenLive(bean)
                    appViewModel.appPushLive.postValue(bean)
                }

                override fun onCloseLive(bean: LiveStatus) {
                    super.onCloseLive(bean)
                    appViewModel.appPushLive.postValue(bean)
                }
            })
    }

    override fun onDestroy() {
        MyWsManager.getInstance(requireActivity())!!.removeLiveStatusListener(tags)
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()


    }

    override fun onResume() {
        super.onResume()

        try {

            //这里需要告诉子fragment 可见了//注意第一次启动不能调用
            (mFragments[index] as ScheduleChildOneFragment).checkData()
            if (Constants.isLoading) {
                LogUtils.d("子frahment可见了  判断是否需要刷新")
                var bean = CurrentIndex()
                bean.currtOne = index
                bean.currtTwo =
                    (mFragments[index] as ScheduleChildOneFragment).getCurrentIndex()
                appViewModel.updateSchedulePosition.postValue(bean)

            }

        } catch (e: Exception) {
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        //  isVisble = isVisibleToUser
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    fun getCurrentIndex(): Int {
        // 子 Fragment 的逻辑操作
        return mDatabind.vp.currentItem
    }

    private fun initEvent() {
        try {


            mTitles = resources.getStringArray(R.array.str_schedule_tab_top)
            for (i in 0 until mTitles!!.size) {
                mFragments.add(ScheduleChildOneFragment.newInstance(mtypes[i], status[i], i))
                (mFragments[i] as ScheduleChildOneFragment).setPanrent(this@ScheduleFragment)
            }
            mDatabind.vp.initActivity(requireActivity(), mFragments, true)
            //初始化 magic_indicator
            mDatabind.magicIndicator.bindViewPager2(
                mDatabind.vp, arrayListOf(
                    mTitles!![0],
                    mTitles!![1],
                    mTitles!![2],
                    mTitles!![3]
                ),
                R.color.c_37373d,
                R.color.c_94999f,
                18f, 16f, true, true,
                R.color.c_34a853, margin = 15
            )
            mDatabind.vp.offscreenPageLimit = 4
            // mDatabind.vp.isUserInputEnabled = false

//        mDatabind.vp.adapter= MyPagerAdapter(childFragmentManager);
//        mDatabind.slide.setViewPager(mDatabind.vp)
//        mDatabind.vp.currentItem = 0
//        mDatabind.vp.offscreenPageLimit=4
            appViewModel.updateViewpager.observeForever {

                // mDatabind.vp.isUserInputEnabled = it
            }

            mDatabind.vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    index = position
                    var bean = CurrentIndex()
                    bean.currtOne = position
                    bean.currtTwo =
                        (mFragments[position] as ScheduleChildOneFragment).getCurrentIndex()
                    appViewModel.updateSchedulePosition.postValue(bean)
                }

            })
        } catch (e: Exception) {
        }
    }



    private val MSG_LOAD_DATA = 0x0001
    private val MSG_LOAD_SUCCESS = 0x0002
    private val MSG_LOAD_FAILED = 0x0003
    private var thread: Thread? = null
    private var isLoaded = false



    fun  initJsonData(){
        //解析数据
        /*
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         */
        val jsonData: String =
            GetJsonDataUtil().getJson(context, "province.json") //获取assets目录下的json文件数据
        val jsonBean: ArrayList<JsonBean> = parseData(jsonData) //用Gson 转成实体
        /*
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        TimeConstantsDat.options1Items = jsonBean

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
            /*
             * 添加城市数据
             */
            TimeConstantsDat.options2Items.add(cityList)
            /*
             * 添加地区数据
             */
            TimeConstantsDat.options3Items.add(province_AreaList)

            mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS)
        }

    }


    fun parseData(result: String?):  ArrayList<JsonBean>  { //Gson 解析
        val detail = java.util.ArrayList<JsonBean>()
        try {
            val data = JSONArray(result)
            val gson = Gson()
            for (i in 0 until data.length()) {
                val entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean::class.java)
                detail.add(entity)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED)
        }
        return detail
    }

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_LOAD_DATA -> if (thread == null) { //如果已创建就不再重新创建子线程了
                    Toast.makeText(context, "Begin Parse Data", Toast.LENGTH_SHORT)
                        .show()
                    thread = Thread { // 子线程中解析省市区数据
                        initJsonData()
                    }
                    thread!!.start()
                }

                MSG_LOAD_SUCCESS -> {

                    isLoaded = true
                }

                MSG_LOAD_FAILED -> Toast.makeText(
                    context,
                    "Parse Failed",
                    Toast.LENGTH_SHORT
                ).show()

                else -> {}
            }
        }
    }

}