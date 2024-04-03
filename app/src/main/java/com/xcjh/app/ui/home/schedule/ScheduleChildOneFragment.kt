package com.xcjh.app.ui.home.schedule

import ando.widget.pickerview.builder.OptionsPickerBuilder
import ando.widget.pickerview.listener.CustomListener
import ando.widget.pickerview.listener.OnOptionsSelectListener
import ando.widget.pickerview.view.OptionsPickerView
import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.lxj.xpopup.XPopup
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.CurrentIndex
import com.xcjh.app.bean.HotMatchBean
import com.xcjh.app.bean.JsonBean
import com.xcjh.app.bean.TimeConstantsDat
import com.xcjh.app.databinding.FrScheduleoneBinding
import com.xcjh.app.listener.OnChooseDateListener
import com.xcjh.app.utils.XPBottomPopu
import com.xcjh.base_lib.utils.bindViewPager3
import com.xcjh.base_lib.utils.initActivity
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.setOnclickNoRepeat
import org.json.JSONArray

class ScheduleChildOneFragment : BaseFragment<ScheduleVm, FrScheduleoneBinding>() {
    private val mFragments: ArrayList<Fragment> = ArrayList<Fragment>()
    private var mTitles: Array<out String>? = null
    var matchtype: String? = ""
    var matchtypeOld: String? = ""
    var status = ""
    var hasData = false
    var isVisble = false
    var calendarTime: String = ""
    var currentCount = 0
    var mOneTabIndex = 0
    var mTwoTabIndex = 0

    var  selectYi=0
    var  selectEr=0
    var  selectSan=0





    lateinit var bottomDilog: XPBottomPopu
    private lateinit var parentFragment: ScheduleFragment
    private var pvOptions: OptionsPickerView<Any>? = null//省市区
    companion object {
        var mTitles: Array<out String>? = null
        private val MATCHTYPE = "matchtype"
        private val STATUS = "status"
        private val TAB = "tab"
        fun newInstance(matchtype: String, status: String, po: Int): ScheduleChildOneFragment {
            val args = Bundle()
            args.putString(MATCHTYPE, matchtype);
            args.putString(STATUS, status);
            args.putInt(TAB, po);
            val fragment = ScheduleChildOneFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        //  isVisble = isVisibleToUser
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    /**
     * 懒加载初始化 最下层的分类   只执行一次
     */
    override fun lazyLoadData() {
        super.lazyLoadData()

        mViewModel.getHotMatchData(matchtypeOld!!, status)

    }

    override fun onPause() {
        super.onPause()
        isVisble = false
    }

    fun checkData() {
        if (!isFirst && !hasData) {
            mViewModel.getHotMatchData(matchtypeOld!!, status)

        }
    }

    override fun onResume() {
        super.onResume()
        checkData()
    }

    fun setPanrent(mparentFragment: ScheduleFragment) {
        parentFragment = mparentFragment
        // 子 Fragment 的逻辑操作

    }

    fun getCurrentIndex(): Int {
        // 子 Fragment 的逻辑操作
        return mDatabind.vp.currentItem
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun initView(savedInstanceState: Bundle?) {
        try {


            val bundle = arguments
            if (bundle != null) {
                matchtype = bundle.getString(ScheduleChildOneFragment.MATCHTYPE)!!
                matchtypeOld = matchtype
                status = bundle.getString(ScheduleChildOneFragment.STATUS)!!
                mOneTabIndex = bundle.getInt(ScheduleChildOneFragment.TAB)
                mViewModel.getMatchTimeCount(matchtypeOld.toString(),requireContext())
            }

            mDatabind.vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    currentCount = position
                    if (parentFragment != null) {
                        var bean = CurrentIndex()
                        bean.currtOne = parentFragment!!.getCurrentIndex()
                        bean.currtTwo = position
                        appViewModel.updateSchedulePosition.postValue(bean)
                    }

                }

            })
//        appViewModel.updateSchedulePosition.observeForever {
//
//            if (mOneTabIndex==it&&isAdded){
//                var index= mDatabind.vp.currentItem
//                appViewModel.updateScheduleTwoPosition.postValue(index)
//            }
//
//        }

            setOnclickNoRepeat(mDatabind.ivMeau) {
                when (it.id) {
                    R.id.iv_meau -> {

                        //0推荐    1 是足球   2是篮球    3是赛果
                        // 0推荐    1 是足球   2是篮球    3是赛果
                        if(TimeConstantsDat.options1ItemsAll.size>0){

                            showPickerView()
                        }else   if(TimeConstantsDat.options1ItemsFootball.size>0){

                            showPickerView()

                        } else   if(TimeConstantsDat.options1ItemsBasketball.size>0){
                            showPickerView()
                        }else if(TimeConstantsDat.options1ItemsSaiguo.size>0){
                            showPickerView()
                        }


//                        if (mFragments.size > 0) {
//                            calendarTime = (mFragments[currentCount] as ScheduleChildTwoFragment).getCanleTime()
//                            bottomDilog = XPBottomPopu(requireActivity())
//                            var popwindow = XPopup.Builder(context)
//                                .hasShadowBg(true)
//                                .moveUpToKeyboard(false) //如果不加这个，评论弹窗会移动到软键盘上面
//                                .isViewMode(true)
//                                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
//                                //                        .isThreeDrag(true) //是否开启三阶拖拽，如果设置enableDrag(false)则无效
//                                .asCustom(bottomDilog).show()
//                            bottomDilog.setOnLister(
//                                calendarTime,
//                                matchtypeOld!!,
//                                object : OnChooseDateListener {
//                                    override fun onDismiss() {
//                                        popwindow.dismiss()
//
//                                    }
//
//                                    override fun onSure(time: String?) {
//                                        popwindow.dismiss()
//                                        calendarTime = time!!
//
//                                        appViewModel.updateganlerTime.postValue(calendarTime)
//                                    }
//                                })
//                        }

//                    selectDate(requireActivity(), calendarTime) { time ->
//
//                        calendarTime = time
//
//                        appViewModel.updateganlerTime.postValue(calendarTime)
//                    }
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun createObserver() {
        try {


            val empty = requireActivity().layoutInflater!!.inflate(R.layout.layout_empty, null)

            mViewModel.hotMatch.observe(this) {
                if (it.isNotEmpty()) {
                    //成功
                    hasData = true
                    if (matchtypeOld != "3") {
                        var bean = HotMatchBean(
                            "", resources.getString(R.string.all), 0,
                            matchtype.toString()
                        )
                        it.add(0, bean)
                    } else {
                        matchtype = "1"
                        var bean1 = HotMatchBean("", resources.getString(R.string.foot_scr), 0, "1")
                        it.add(0, bean1)
                        var bean2 = HotMatchBean("", resources.getString(R.string.bas_scr), 0, "2")
                        it.add(1, bean2)
                    }

                    initEvent(it)

                } else {
                    var listthis = ArrayList<HotMatchBean>()
                    hasData = true
                    if (matchtypeOld != "3") {
                        var bean = HotMatchBean(
                            "", resources.getString(R.string.all), 0,
                            matchtype.toString()
                        )
                        listthis.add(0, bean)
                    } else {
                        matchtype = "1"
                        var bean1 = HotMatchBean("", resources.getString(R.string.foot_scr), 0, "1")
                        listthis.add(0, bean1)
                        var bean2 = HotMatchBean("", resources.getString(R.string.bas_scr), 0, "2")
                        listthis.add(1, bean2)
                    }

                    initEvent(listthis)
                }

            }
        } catch (e: Exception) {
        }

    }

    private fun initEvent(datas: ArrayList<HotMatchBean>) {
        try {


            mFragments.clear()
            var titles: MutableList<String> = ArrayList<String>()
            for (i in 0 until datas!!.size) {
                mFragments.add(
                    ScheduleChildTwoFragment.newInstance(
                        datas[i].matchType,
                        datas[i].competitionId,
                        status,
                        mOneTabIndex,
                        i
                    )
                )
                titles.add(datas[i].competitionName)
            }
            mDatabind.vp.initActivity(requireActivity(), mFragments, true)
            //初始化 magic_indicator
            mDatabind.magicIndicator.bindViewPager3(
                mDatabind.vp, titles,
                R.color.c_34a853,
                R.color.c_94999f,
                15f, 14f, true, true,
                R.color.translet, margin = 15
            )
            mDatabind.vp.offscreenPageLimit = mFragments.size

//        mDatabind.vp.adapter= MyPagerAdapter(childFragmentManager);
//        mDatabind.slide.setViewPager(mDatabind.vp)
//        mDatabind.vp.currentItem = 0
//        mDatabind.vp.offscreenPageLimit=4
        } catch (e: Exception) {
        }

    }




    @SuppressLint("ResourceAsColor")
    fun showPickerView(){
        var selectYiNew=0
        var selectErNew=0
        var selectSanNew=0

        pvOptions= OptionsPickerBuilder(requireContext(), object: OnOptionsSelectListener {
               //确定
               override fun onOptionsSelect(options1: Int, options2: Int, options3: Int, v: View?) {

               }


           })//滚轮
            .setOptionsSelectChangeListener { options1, options2, options3 ->
                selectYiNew=options1
                selectErNew=options2
                selectSanNew=options3


           }
               .setTitleText("")
            .setDividerColor(Color.TRANSPARENT)
            .setTextColorCenter(ContextCompat.getColor(requireActivity(), R.color.c_34a853)) //设置选中项文字颜色
            .setContentTextSize(15)
            .setBgColor(Color.TRANSPARENT)
            .setLineSpacingMultiplier(2.2f)
            .setAlphaGradient(true)
            .setTypeface(Typeface.DEFAULT_BOLD)
            .setSelectOptions(selectYi,selectEr,selectSan)
            .setTitleColor(ContextCompat.getColor(requireActivity(), R.color.c_34a853))//标题文字颜色
            .setLayoutRes(R.layout.select_time_new, CustomListener() {
                val tvcz = it.findViewById<TextView>(R.id.tvcz)
                val tvsure = it.findViewById<TextView>(R.id.tvsure)
                val ivNext = it.findViewById<ImageView>(R.id.ivNext)
                //重置   //0推荐    1 是足球   2是篮球    3是赛果
                tvcz.setOnClickListener {
                    if(matchtypeOld.equals("0")||matchtypeOld.equals("1")||matchtypeOld.equals("2")){
                        pvOptions?.setSelectOptions(0,0,0)
                    }else{
                        pvOptions?.setSelectOptions(TimeConstantsDat.saiYiNew,TimeConstantsDat.saiErNew,TimeConstantsDat.saiSanNew)
                    }

                }
                ivNext.setOnClickListener {
                    pvOptions?.dismiss()
                }
                tvsure.setOnClickListener {
                    if(matchtypeOld.equals("3")){
                        TimeConstantsDat.saiYi=selectYiNew
                        TimeConstantsDat.saiEr=selectErNew
                        TimeConstantsDat.saiSan=selectSanNew
                    }else{
                        selectYi=selectYiNew
                        selectEr=selectErNew
                        selectSan=selectSanNew

                    }
                    selectYi=selectYiNew
                    selectEr=selectErNew
                    selectSan=selectSanNew

                    // 0推荐    1 是足球   2是篮球    3是赛果
                    if(matchtypeOld.equals("0")){
                        var opt0=TimeConstantsDat.options1ItemsAll.get(selectYi).getPickerViewText()
                        var opt1=TimeConstantsDat.options2ItemsAll.get(selectYi).get(selectEr)
                        var opt2=TimeConstantsDat.options3ItemsAll.get(selectYi).get(selectEr).get(selectSan)
                        calendarTime= cleanDate("$opt0-$opt1-$opt2")

                    }else   if(matchtypeOld.equals("1")){


                        var opt0=TimeConstantsDat.options1ItemsFootball.get(selectYi).getPickerViewText()
                        var opt1=TimeConstantsDat.options2ItemsFootball.get(selectYi).get(selectEr)
                        var opt2=TimeConstantsDat.options3ItemsFootball.get(selectYi).get(selectEr).get(selectSan)
                        calendarTime=cleanDate(opt0+"-"+opt1+"-"+opt2)

                    } else   if(matchtypeOld.equals("2")){
                        var opt0=TimeConstantsDat.options1ItemsBasketball.get(selectYi).getPickerViewText()
                        var opt1=TimeConstantsDat.options2ItemsBasketball.get(selectYi).get(selectEr)
                        var opt2=TimeConstantsDat.options3ItemsBasketball.get(selectYi).get(selectEr).get(selectSan)
                        calendarTime=cleanDate(opt0+"-"+opt1+"-"+opt2)
                    }else{
                        var opt0=TimeConstantsDat.options1ItemsSaiguo.get(selectYi).getPickerViewText()
                        var opt1=TimeConstantsDat.options2ItemsSaiguo.get(selectYi).get(selectEr)
                        var opt2=TimeConstantsDat.options3ItemsSaiguo.get(selectYi).get(selectEr).get(selectSan)
                        calendarTime=cleanDate(opt0+"-"+opt1+"-"+opt2)
                    }

                    appViewModel.updateganlerTime.postValue(calendarTime)
                    pvOptions?.dismiss()
                }
            })
            .isDialog(true)
            .setOutSideCancelable(true) .build<Any>()
        // 0推荐    1 是足球   2是篮球    3是赛果
        if(matchtypeOld.equals("0")){
            pvOptions!!.setPicker (TimeConstantsDat.options1ItemsAll as List<Any>?,
                TimeConstantsDat.options2ItemsAll as List<MutableList<Any>>?,
                TimeConstantsDat.options3ItemsAll as List<MutableList<MutableList<Any>>>?
            )

        }else  if(matchtypeOld.equals("1")){
              pvOptions!!.setPicker (TimeConstantsDat.options1ItemsFootball as List<Any>?,
                TimeConstantsDat.options2ItemsFootball as List<MutableList<Any>>?,
                TimeConstantsDat.options3ItemsFootball as List<MutableList<MutableList<Any>>>?
            )
        }else  if(matchtypeOld.equals("2")){
            pvOptions!!.setPicker (TimeConstantsDat.options1ItemsBasketball as List<Any>?,
                TimeConstantsDat.options2ItemsBasketball as List<MutableList<Any>>?, TimeConstantsDat.options3ItemsBasketball as List<MutableList<MutableList<Any>>>?
            )
        }else{
            pvOptions!!.setPicker (TimeConstantsDat.options1ItemsSaiguo as List<Any>?,
                TimeConstantsDat.options2ItemsSaiguo as List<MutableList<Any>>?, TimeConstantsDat.options3ItemsSaiguo as List<MutableList<MutableList<Any>>>?
            )
        }



        val mDialog: Dialog = pvOptions!!.getDialog()
        if (mDialog != null) {
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )
            params.leftMargin = 0
            params.rightMargin = 0
            pvOptions!!.getDialogContainerLayout().setLayoutParams(params)
            val dialogWindow = mDialog.window
            if (dialogWindow != null) {
                var lParams = dialogWindow.attributes
                lParams. dimAmount = 0.3f
                lParams.width = WindowManager.LayoutParams.MATCH_PARENT
                lParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                lParams.gravity=Gravity.BOTTOM
                dialogWindow.setGravity(Gravity.BOTTOM) //改成Bottom,底部显示
                dialogWindow.setWindowAnimations(ando.widget.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setDimAmount(0.3f)
                dialogWindow.attributes=lParams
            }
        }

        if(matchtypeOld.equals("3")){

            pvOptions!!.setSelectOptions(TimeConstantsDat.saiYi,TimeConstantsDat.saiEr,TimeConstantsDat.saiSan)
        }

        pvOptions!!.show()

    }

    fun cleanDate(dateStr: String): String {
        val parts = dateStr.split("-")
        val year = parts[0]
        val month = parts[1].replace(resources.getString(R.string.month_txt), "")
        val day = parts[2].replace(resources.getString(R.string.day_txt), "")

        return "$year-$month-$day"
    }

}