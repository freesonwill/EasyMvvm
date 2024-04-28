package com.xcjh.app.ui.search


import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.drake.brv.listener.ItemDifferCallback
import com.drake.brv.utils.*
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.AnchorBean
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.bean.MatchBean
import com.xcjh.app.databinding.ActivitySearchBinding
import com.xcjh.app.databinding.ItemJsBinding
import com.xcjh.app.databinding.ItemMainLiveListBinding
import com.xcjh.app.databinding.ItemSchAllBinding
import com.xcjh.app.databinding.ItemSearchHotTitleBinding
import com.xcjh.app.ui.details.DetailVm
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.app.ui.home.home.tab.setLiveMatchItem
import com.xcjh.app.ui.notice.MyNoticeVm
import com.xcjh.app.utils.SoundManager
import com.xcjh.app.utils.TimeUtil
import com.xcjh.app.utils.judgeLogin
import com.xcjh.app.view.CustomHeader
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.bindViewPager2
import com.xcjh.base_lib.utils.bindadapter.CustomBindAdapter.afterTextChanged
import com.xcjh.base_lib.utils.distance
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.grid
import com.xcjh.base_lib.utils.initActivity
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.vertical
import com.xcjh.base_lib.utils.view.clickNoRepeat
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SearchActivity : BaseActivity<SearchVm, ActivitySearchBinding>() {
    private var searchType: Int = 0  //0是搜索  1是取消
    private var mFragList = ArrayList<Fragment>()
    private var selectType = 0//选择的是0是直播 1是赛程
    var strTimeZu: MutableList<String> = ArrayList<String>()
    var listdata: MutableList<MatchBean> = ArrayList<MatchBean>()
    var mview: LottieAnimationView? = null
    var mview1: ImageView? = null
    var index: Int = 0

    private val vm by lazy {
        ViewModelProvider(this)[MyNoticeVm::class.java]
    }
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .navigationBarDarkIcon(true)
            .navigationBarColor(R.color.c_ffffff)
            .titleBar(mDatabind.rlSearchTitle)
            .init()
        mFragList.add(Fragment())
        mFragList.add(Fragment())
        //取消下拉刷新
        mDatabind.smartCommon.setEnableRefresh(false)
        mDatabind.smartCommon.setRefreshHeader(CustomHeader(this))
        mDatabind.smartCommon.setOnRefreshListener {
            mViewModel.getGameList(mDatabind.etSearchInput.text.toString(),true)
        } .setOnLoadMoreListener {
            mViewModel.getGameList(mDatabind.etSearchInput.text.toString(),false)
            }

        mDatabind.viewPager.initActivity(this, mFragList, true, 4)

        //初始化 magic_indicator
        mDatabind.magicIndicator.bindViewPager2(
            mDatabind.viewPager, arrayListOf(
                getString(R.string.search_txt_live),
                getString(R.string.search_txt_game),
            ),
            R.color.c_37373d,
            R.color.c_94999f,
            14f, 14f, true, false,
            R.color.c_34a853, margin = 30
        )
        mDatabind.viewPager.offscreenPageLimit = mFragList.size
        // 将搜索按钮始终设为可用
//        mDatabind.etSearchInput.isEnablesReturnKeyAutomatically = false
        // 将搜索 EditText 的 imeOptions 设置为 actionSearch
        mDatabind.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectType=position
                if (position == 0) {
                    mDatabind.state.visibility=View.VISIBLE
                    mDatabind.smartCommon.visibility=View.GONE
                } else {
                    mDatabind.state.visibility=View.GONE
                    mDatabind.smartCommon.visibility=View.VISIBLE
                }


            }
        })

        mViewModel.getHotOngoingMatch()
        adapter()
        mDatabind.state.apply {
            onEmpty {
                var icon = this.findViewById<AppCompatImageView>(R.id.ivEmptyIcon)
                var txt = this.findViewById<AppCompatTextView>(R.id.txtEmptyName)
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.zwt_search))
                txt.text = resources.getString(R.string.search_txt_empty_name)

            }

        }

        mDatabind.stateNew.apply {
            onEmpty {
                var icon = this.findViewById<AppCompatImageView>(R.id.ivEmptyIcon)
                var txt = this.findViewById<AppCompatTextView>(R.id.txtEmptyName)
                icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.zwt_search))
                txt.text = resources.getString(R.string.no_data_hint)

            }

        }

        mDatabind.etSearchInput.afterTextChanged {

            if (it.isNotEmpty()) {
//                searchFlow.tryEmit(it)
                mDatabind.ivClear.visibility = View.VISIBLE
                searchType = 0
//                mDatabind.txtSearchClick.text=resources.getString(R.string.search)

                mViewModel.getNowLive(mDatabind.etSearchInput.text.toString())
                mViewModel.getGameList(mDatabind.etSearchInput.text.toString(),true)

                searchType = 1
//                mDatabind.txtSearchClick.text=resources.getString(R.string.cancel)
                mDatabind.llSearchShow.visibility = View.GONE
                mDatabind.llSearchShow.visibility = View.GONE
                mDatabind.rlShowRe.visibility = View.VISIBLE
                mDatabind.rlChuNew.visibility=   View.VISIBLE
                mDatabind.ViewHu.visibility=   View.GONE

//                if (selectType == 0) {
//                    mDatabind.state.visibility=View.VISIBLE
//                    mDatabind.smartCommon.visibility=View.GONE
//                    mDatabind.rlShowRe.visibility=View.VISIBLE
//
//
//                } else {
//                    mDatabind.state.visibility=View.GONE
//                    mDatabind.smartCommon.visibility=View.VISIBLE
//                }


            } else {
                mDatabind.ivClear.visibility = View.GONE
//                if (mDatabind.rcHotRace.models != null && mDatabind.rcHotRace.models!!.isNotEmpty()) {
//                    mDatabind.llSearchShow.visibility = View.VISIBLE
//                    mDatabind.rlShowRe.visibility = View.GONE
//
//                } else {
//                    mDatabind.llSearchShow.visibility = View.GONE
//                }
                if (mDatabind.rcHotRace.models != null && mDatabind.rcHotRace.models!!.isNotEmpty()) {
                    mDatabind.llSearchShow.visibility = View.VISIBLE
                    mDatabind.rlShowRe.visibility = View.GONE


                } else {
                    mDatabind.llSearchShow.visibility = View.GONE
                    mDatabind.rlShowRe.visibility = View.VISIBLE
                }


                //清空
                if (mDatabind.rcSearchList.models != null) {
                    var remove = mDatabind.rcSearchList.mutable
                    mDatabind.rcSearchList.mutable.removeAll(remove)
                    mDatabind.rcSearchList.adapter!!.notifyItemRangeRemoved(0, remove.size)
                }
                mDatabind.state.showEmpty()


                if (mDatabind.rcSearchListNew.models?.size != null) {
                    mDatabind.rcSearchListNew.mutable.clear()
                }
                mDatabind.smartCommon.finishRefresh()
                mDatabind.stateNew.showEmpty()

            }
        }
        //删除输入的内容
        mDatabind.ivClear.clickNoRepeat {
            SoundManager.playMedia()
            mDatabind.etSearchInput.setText("")
            if (mDatabind.rcHotRace.models != null && mDatabind.rcHotRace.models!!.isNotEmpty()) {
                mDatabind.llSearchShow.visibility = View.VISIBLE
                mDatabind.rlShowRe.visibility = View.GONE


            } else {
                mDatabind.llSearchShow.visibility = View.GONE
                mDatabind.rlShowRe.visibility = View.VISIBLE
            }


            //清空
            if (mDatabind.rcSearchList.models != null) {
                var remove = mDatabind.rcSearchList.mutable
                mDatabind.rcSearchList.mutable.removeAll(remove)
                mDatabind.rcSearchList.adapter!!.notifyItemRangeRemoved(0, remove.size)
            }
            mDatabind.state.showEmpty()


            if (mDatabind.rcSearchListNew.models?.size != null) {
                mDatabind.rcSearchListNew.mutable.clear()
            }
            mDatabind.smartCommon.finishRefresh()
            mDatabind.stateNew.showEmpty()

        }

        //点击搜索
        mDatabind.etSearchInput.setOnEditorActionListener { v, actionId, event ->

            if (mDatabind.etSearchInput.text.toString().isEmpty()) {
                myToast(getString(R.string.detail_txt_input))

            } else {
                hideKeyboard(this, mDatabind.etSearchInput)
                mViewModel.getNowLive(mDatabind.etSearchInput.text.toString())
                mViewModel.getGameList(mDatabind.etSearchInput.text.toString(),true)
                searchType = 1
//                mDatabind.txtSearchClick.text=resources.getString(R.string.cancel)
                mDatabind.llSearchShow.visibility = View.GONE
                mDatabind.rlShowRe.visibility = View.VISIBLE
                mDatabind.rlChuNew.visibility=   View.VISIBLE
                mDatabind.ViewHu.visibility=   View.GONE

            }

            true
        }

        //根据是否搜索来判断
        mDatabind.txtSearchClick.clickNoRepeat {
            SoundManager.playMedia()
            finish()
//            if(mDatabind.etSearchInput.text.toString().trim().isEmpty()){
//                myToast(getString(R.string.contact_hint_input))
//                return@clickNoRepeat
//            }
//
//            if(searchType==0){
//                mViewModel.getNowLive(mDatabind.etSearchInput.text.toString().trim())
//                searchType=1
//                mDatabind.txtSearchClick.text=resources.getString(R.string.cancel)
//                mDatabind.llSearchShow.visibility=View.GONE
//                mDatabind.state.visibility=View.VISIBLE
//            }else{
//                searchType=0
//                mDatabind.txtSearchClick.text=resources.getString(R.string.search)
//                mDatabind.llSearchShow.visibility=View.VISIBLE
//                mDatabind.state.visibility=View.GONE
//                mDatabind.etSearchInput.setText("")
//                if(mDatabind.rcSearchList.models!=null){
//                    mDatabind.rcSearchList.mutable.clear()
//                }
//
//            }
        }
//        mDatabind.etSearchInput.postDelayed({
//            showSoftKeyboard()
//        }, 200)

    }

    fun adapter() {
        //热门标签
        mDatabind.rcHotRace.linear().setup {
            addType<BeingLiveBean>(R.layout.item_search_hot_title)

            onBind {
                when (itemViewType) {
                    R.layout.item_search_hot_title -> {
                        var bindingItem = getBinding<ItemSearchHotTitleBinding>()
                        var bean = _data as BeingLiveBean
                        //比赛类型 1足球，2篮球,可用值:1,2
                        if (bean.matchType.equals("1")) {
                            bindingItem.txtHotNAme.text =
                                "${bean.competitionName}\n\n${bean.homeTeamName}VS${bean.awayTeamName}"
                        } else {
                            bindingItem.txtHotNAme.text =
                                "${bean.competitionName}\n\n${bean.awayTeamName}VS${bean.homeTeamName}"
                        }
                        bindingItem.ivHotRanking.visibility = View.VISIBLE
                        if (modelPosition == 0) {
                            bindingItem.ivHotRanking.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this@SearchActivity,
                                    R.drawable.search_icon_yi
                                )
                            )
                        } else if (modelPosition == 1) {
                            bindingItem.ivHotRanking.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this@SearchActivity,
                                    R.drawable.search_icon_er
                                )
                            )
                        } else if (modelPosition == 2) {
                            bindingItem.ivHotRanking.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this@SearchActivity,
                                    R.drawable.search_icon_san
                                )
                            )
                        } else if (modelPosition == 3) {
                            bindingItem.ivHotRanking.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this@SearchActivity,
                                    R.drawable.search_icon_si
                                )
                            )
                        } else if (modelPosition == 4) {
                            bindingItem.ivHotRanking.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this@SearchActivity,
                                    R.drawable.search_icon_wu
                                )
                            )
                        } else {
                            bindingItem.ivHotRanking.visibility = View.GONE
                        }
                        //是热门
                        if (bean.hottest) {
                            bindingItem.ivHotHeat.visibility = View.VISIBLE
                            //语言 0是中文  1是繁体  2是英文
                            if(Constants.languageType!=0&&Constants.languageType!=1){
                                var layoutParams= bindingItem.ivHotHeat.layoutParams
                                layoutParams.width=dp2px(29)
                                layoutParams.height=dp2px(18)
                            }else{
                                var layoutParams= bindingItem.ivHotHeat.layoutParams
                                layoutParams.width=dp2px(18)
                                layoutParams.height=dp2px(18)
                            }
                            bindingItem.ivHotHeat.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this@SearchActivity,
                                    R.drawable.search_icon_hot
                                )
                            )
                        } else if (bean.newest) {//最新的
                            bindingItem.ivHotHeat.visibility = View.VISIBLE
                            if(Constants.languageType!=0&&Constants.languageType!=1){
                                var layoutParams= bindingItem.ivHotHeat.layoutParams
                                layoutParams.width=dp2px(34)
                                layoutParams.height=dp2px(18)

                            }else{
                                var layoutParams= bindingItem.ivHotHeat.layoutParams
                                layoutParams.width=dp2px(18)
                                layoutParams.height=dp2px(18)
                            }

                            bindingItem.ivHotHeat.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this@SearchActivity,
                                    R.drawable.search_icon_new
                                )
                            )
                        } else {

                            bindingItem.ivHotHeat.visibility = View.GONE
                        }


                    }


                }

            }
            R.id.llClickHot.onClick {
                SoundManager.playMedia()
                val bean = _data as BeingLiveBean
                MatchDetailActivity.open(
                    matchType = bean.matchType,
                    matchId = bean.matchId,
                    matchName = "${bean.homeTeamName}VS${bean.awayTeamName}",
                    anchorId = bean.userId,
                    videoUrl = bean.playUrl
                )

            }


        }

        //这个直播间搜索
        mDatabind.rcSearchList.grid(2).setup {
            addType<BeingLiveBean>(R.layout.item_main_live_list)
            onBind {
                when (itemViewType) {
                    R.layout.item_main_live_list -> {
                        setLiveMatchItem()
//                        var bindingItem = getBinding<ItemMainLiveListBinding>()
//                        var bean = _data as BeingLiveBean
//                        Glide.with(context)
//                            .load(bean.titlePage) // 替换为您要加载的图片 URL
//                            .error(R.drawable.main_top_load)
//                            .placeholder(R.drawable.main_top_load)
//                            .into(bindingItem.ivLiveBe)
//                        Glide.with(context)
//                            .load(bean.userLogo) // 替换为您要加载的图片 URL
//                            .error(R.drawable.default_anchor_icon)
//                            .placeholder(R.drawable.default_anchor_icon)
//                            .into(bindingItem.ivLiveHead)
//                        bindingItem.txtLiveName.text = bean.nickName
//                        //比赛类型 1足球，2篮球,可用值:1,2
//                        if (bean.matchType.equals("1")) {
//                            bindingItem.txtLiveTeam.text =
//                                "${bean.homeTeamName} VS ${bean.awayTeamName}"
//                        } else {
//                            bindingItem.txtLiveTeam.text =
//                                "${bean.awayTeamName} VS ${bean.homeTeamName}"
//                        }
//
//                        bindingItem.txtLiveCompetition.text = bean.competitionName
//                        if (bean.hotValue <= 9999) {
//                            bindingItem.txtLiveHeat.text = "${bean.hotValue}"
//                        } else {
//                            bindingItem.txtLiveHeat.text = "9999+"
//                        }
//
//                        if (layoutPosition % 2 == 0) {
//                            val layoutParams =
//                                bindingItem.llLiveSpacing.layoutParams as ViewGroup.MarginLayoutParams
//                            layoutParams.setMargins(0, 0, context.dp2px(4), context.dp2px(8))
//                            bindingItem.llLiveSpacing.layoutParams = layoutParams
//                        } else {
//                            val layoutParams =
//                                bindingItem.llLiveSpacing.layoutParams as ViewGroup.MarginLayoutParams
//                            layoutParams.setMargins(0, 0, context.dp2px(4), context.dp2px(8))
//                            bindingItem.llLiveSpacing.layoutParams = layoutParams
//                        }
                    }

                }

            }
            R.id.llLiveSpacing.onClick {
                SoundManager.playMedia()
                val bean = _data as BeingLiveBean
//                MatchDetailActivity.open(
//                    matchType = bean.matchType,
//                    matchId = bean.matchId,
//                    matchName = "${bean.homeTeamName}VS${bean.awayTeamName}",
//                    anchorId = bean.userId,
//                    videoUrl = bean.playUrl
//                )
                if(bean.pureFlow){
                    MatchDetailActivity.open(matchType =bean.matchType, matchId = bean.matchId,matchName = "${bean.homeTeamName}VS${bean.awayTeamName}",pureFlow = true  )
                }else{
                    MatchDetailActivity.open(matchType =bean.matchType, matchId = bean.matchId,matchName = "${bean.homeTeamName}VS${bean.awayTeamName}", anchorId = bean.userId,videoUrl = bean.playUrl )
                }
            }

        }


        //赛事搜索
        mDatabind.rcSearchListNew.run {
            vertical()
            // adapter=mAdapter
            distance(0, 0, 0, 0)
        }
        mDatabind.rcSearchListNew.linear().setup {
            addType<MatchBean>(R.layout.item_sch_all)

            onBind {

                // findView<TextView>(R.id.tvname).text = getModel<MatchBean>().competitionName
                var binding = getBinding<ItemSchAllBinding>()
                var item = _data as MatchBean
                // 设置item数据

                var time = TimeUtil.getDayOfWeek(item!!.matchTime.toLong(), context)
                var time1 = TimeUtil.timeStamp2Date(item!!.matchTime.toLong(), null)
                if (item.focus) {
                    binding.ivsc.setBackgroundResource(R.drawable.sc_shoucang_icon2)
                } else {
                    binding.ivsc.setBackgroundResource(R.drawable.sc_shoucang_icon1)
                }
                binding.tvmiddletime.text = time
                when (item.visbleTime) {
                    0 -> {
                        if (time1!!.substring(0, 10) == TimeUtil.gettimenowYear()) {
                            if (strTimeZu.size == 0) {
                                item.visbleTime = 2
                                binding.tvmiddletime.visibility = View.GONE
                            } else {
                                if (strTimeZu[strTimeZu.size - 1] == time) {
                                    binding.tvmiddletime.visibility = View.GONE
                                    item.visbleTime = 2
                                } else {
                                    binding.tvmiddletime.visibility = View.GONE

                                    item.visbleTime = 1
                                    strTimeZu.add(time)
                                }
                            }
                        } else {
                            if (strTimeZu.size == 0) {
                                binding.tvmiddletime.visibility = View.GONE

                                item.visbleTime = 1
                                strTimeZu.add(time)
                            } else {
                                if (strTimeZu[strTimeZu.size - 1] == time) {
                                    binding.tvmiddletime.visibility = View.GONE
                                    item.visbleTime = 2
                                } else {
                                    binding.tvmiddletime.visibility = View.GONE

                                    item.visbleTime = 1
                                    strTimeZu.add(time)
                                }


                            }

                        }
                    }

                    1 -> {//显示
                        binding.tvmiddletime.visibility = View.GONE

                    }

                    2 -> {//不显示
                        binding.tvmiddletime.visibility = View.GONE

                    }
                }

                LogUtils.d("直播数据" + item.anchorList)
                binding.txtMatchAnimation.visibility = View.VISIBLE
                if (item.matchType == "1") {//比赛类型：1：足球；2：篮球,可用值:1,2
                    binding.tvhafl.text =
                        context.resources.getString(R.string.hafl_rices) + "" + item.homeHalfScore + "-" + item.awayHalfScore
                    binding.tvtime.text = time1!!.substring(11, 16)
                    binding.tvname.text = item.competitionName
                    binding.tvnameLeft.text = item.homeName
                    binding.tvnameRight.text = item.awayName
                    Glide.with(context).load(item.homeLogo)
                        .placeholder(R.drawable.def_football).into(binding.tvflagLeft)
                    Glide.with(context).load(item.awayLogo)
                        .placeholder(R.drawable.def_football).into(binding.tvflagRight)
                    binding.ivtype.setBackgroundResource(R.drawable.football)

                    when (item.status) {
                        "0" -> {
                            binding.txtMatchAnimation.visibility = View.GONE
                            binding.tvstatus.visibility = View.GONE
                            //  clearAnimation(binding.txtMatchAnimation)
                        }

                        "1" -> {
                            binding.tvvs.text = "VS"
                            binding.txtMatchAnimation.visibility = View.GONE
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_94999f
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_37373d
                                )
                            )
                            binding.tvstatus.text =
                                context.resources.getString(R.string.main_txt_wks)
                            clearAnimation(binding.txtMatchAnimation)
                        }

                        "2" -> {
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.txtMatchAnimation.visibility = View.VISIBLE
                            binding.tvvs.text = item.homeScore + "-" + item.awayScore
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_e6820c
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_34a853
                                )
                            )
                            binding.tvstatus.text =
                                if (item.runTime == null)
                                    "0"
                                else {
                                    item.runTime
                                }

                            initAnimation(binding.txtMatchAnimation)
                        }

                        "3" -> {
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.txtMatchAnimation.visibility = View.VISIBLE
                            binding.tvvs.text = item.homeScore + "-" + item.awayScore
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_e6820c
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_34a853
                                )
                            )
                            binding.tvstatus.text = context.resources.getString(
                                R.string.zc
                            )
                            initAnimation(binding.txtMatchAnimation)
                        }

                        "4" -> {
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.txtMatchAnimation.visibility = View.VISIBLE
                            binding.tvvs.text = item.homeScore + "-" + item.awayScore
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_e6820c
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_34a853
                                )
                            )
                            binding.tvstatus.text =
                                if (item.runTime == null)
                                    "0"
                                else {
                                    item.runTime
                                }
                            initAnimation(binding.txtMatchAnimation)
                        }

                        "5", "6" -> {
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.txtMatchAnimation.visibility = View.VISIBLE
                            binding.tvvs.text = item.homeScore + "-" + item.awayScore
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_e6820c
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_34a853
                                )
                            )
                            binding.tvstatus.text = context.resources.getString(
                                R.string.over_time
                            )
                            initAnimation(binding.txtMatchAnimation)

                        }

                        "7" -> {
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.txtMatchAnimation.visibility = View.VISIBLE
                            binding.tvvs.text = item.homeScore + "-" + item.awayScore
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_e6820c
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_34a853
                                )
                            )
                            binding.tvstatus.text = context.resources.getString(
                                R.string.main_dqdz
                            )
                            initAnimation(binding.txtMatchAnimation)

                        }

                        "8" -> {
                            binding.txtMatchAnimation.visibility = View.GONE
                            binding.tvvs.text = item.homeScore + "-" + item.awayScore
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_999999
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_34a853
                                )
                            )
                            binding.tvstatus.text =
                                context.resources.getString(R.string.main_txt_over)
                            clearAnimation(binding.txtMatchAnimation)

                        }

                        "9" -> {

                            binding.txtMatchAnimation.visibility = View.GONE
                            binding.tvvs.text = "VS"
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_8a91a0
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_37373d
                                )
                            )
                            binding.tvstatus.text =
                                context.resources.getString(R.string.main_txt_tc)
                            clearAnimation(binding.txtMatchAnimation)

                        }

                        "10" -> {
                            binding.txtMatchAnimation.visibility = View.GONE
                            binding.tvvs.text = "VS"
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_8a91a0
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_37373d
                                )
                            )
                            binding.tvstatus.text =
                                context.resources.getString(R.string.main_txt_zd)
                            clearAnimation(binding.txtMatchAnimation)

                        }

                        "11" -> {
                            binding.txtMatchAnimation.visibility = View.GONE
                            binding.tvvs.text = "VS"
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_8a91a0
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_37373d
                                )
                            )
                            binding.tvstatus.text =
                                context.resources.getString(R.string.main_txt_yz)

                            clearAnimation(binding.txtMatchAnimation)
                        }

                        "12" -> {
                            binding.txtMatchAnimation.visibility = View.GONE
                            binding.tvvs.text = "VS"
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_8a91a0
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_37373d
                                )
                            )
                            binding.tvstatus.text =
                                context.resources.getString(R.string.main_txt_qx)
                            clearAnimation(binding.txtMatchAnimation)
                        }

                        "13" -> {
                            binding.txtMatchAnimation.visibility = View.GONE
                            binding.tvvs.text = "VS"
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_8a91a0
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_37373d
                                )
                            )
                            binding.tvstatus.text =
                                context.resources.getString(R.string.main_txt_dd)
                            clearAnimation(binding.txtMatchAnimation)
                        }

                        else -> {
                            LogUtils.d("走这里了 哈哈哈")
                            binding.txtMatchAnimation.visibility = View.GONE
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvvs.text = "VS"
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_8a91a0
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_37373d
                                )
                            )
                            clearAnimation(binding.txtMatchAnimation)
                            val date = Date(item.matchTime.toLong())
                            var formatter =
                                SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                            binding.tvstatus.text =
                                context.resources.getString(R.string.main_txt_over)
                        }
                    }
                } else {
                    binding.tvhafl.text =
                        context.resources.getString(R.string.hafl_rices) + "" + item.awayHalfScore + "-" + item.homeHalfScore
                    binding.tvtime.text = time1!!.substring(11, 16)
                    binding.tvname.text = item.competitionName
                    binding.tvnameLeft.text = item.awayName
                    binding.tvnameRight.text = item.homeName
                    Glide.with(context).load(item.awayLogo)
                        .placeholder(R.drawable.def_basketball).into(binding.tvflagLeft)
                    Glide.with(context).load(item.homeLogo)
                        .placeholder(R.drawable.def_basketball).into(binding.tvflagRight)
                    binding.ivtype.setBackgroundResource(R.drawable.basketball)
                    when (item.status) {
                        "0" -> {
                            binding.txtMatchAnimation.visibility = View.GONE
                            binding.tvstatus.visibility = View.GONE
                            clearAnimation(binding.txtMatchAnimation)
                        }

                        "1" -> {
                            binding.tvvs.text = "VS"
                            binding.txtMatchAnimation.visibility = View.GONE
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_94999f
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_37373d
                                )
                            )
                            binding.tvstatus.text =
                                context.resources.getString(R.string.main_txt_wks)
                            clearAnimation(binding.txtMatchAnimation)

                        }

                        "2" -> {
                            binding.txtMatchAnimation.visibility = View.VISIBLE
                            binding.tvvs.text = item.awayScore + "-" + item.homeScore
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_e6820c
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_34a853
                                )
                            )
                            binding.tvstatus.text =
                                context.resources.getString(
                                    R.string.main_txt_basketball_phase,
                                    resources.getString(R.string.one)
                                )
                            initAnimation(binding.txtMatchAnimation)

                        }

                        "3" -> {
                            binding.txtMatchAnimation.visibility = View.VISIBLE
                            binding.tvvs.text = item.awayScore + "-" + item.homeScore
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_e6820c
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_34a853
                                )
                            )
                            binding.tvstatus.text =
                                context.resources.getString(
                                    R.string.main_txt_basketball_phase,
                                    resources.getString(R.string.one)
                                ) + context.resources.getString(
                                    R.string.finis
                                )
                            initAnimation(binding.txtMatchAnimation)

                        }

                        "4" -> {
                            binding.txtMatchAnimation.visibility = View.VISIBLE
                            binding.tvvs.text = item.awayScore + "-" + item.homeScore
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_e6820c
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_34a853
                                )
                            )
                            binding.tvstatus.text =
                                context.resources.getString(
                                    R.string.main_txt_basketball_phase,
                                    resources.getString(R.string.two)
                                )
                            initAnimation(binding.txtMatchAnimation)

                        }

                        "5" -> {
                            binding.txtMatchAnimation.visibility = View.VISIBLE
                            binding.tvvs.text = item.awayScore + "-" + item.homeScore
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_e6820c
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_34a853
                                )
                            )
                            binding.tvstatus.text =
                                context.resources.getString(
                                    R.string.main_txt_basketball_phase,
                                    resources.getString(R.string.two)
                                ) + context.resources.getString(
                                    R.string.finis
                                )
                            initAnimation(binding.txtMatchAnimation)

                        }

                        "6" -> {
                            binding.txtMatchAnimation.visibility = View.VISIBLE
                            binding.tvvs.text = item.awayScore + "-" + item.homeScore
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_e6820c
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_34a853
                                )
                            )
                            binding.tvstatus.text =
                                context.resources.getString(
                                    R.string.main_txt_basketball_phase,
                                    resources.getString(R.string.three)
                                )
                            initAnimation(binding.txtMatchAnimation)

                        }

                        "7" -> {
                            binding.txtMatchAnimation.visibility = View.VISIBLE
                            binding.tvvs.text = item.awayScore + "-" + item.homeScore
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_e6820c
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_34a853
                                )
                            )
                            binding.tvstatus.text =
                                context.resources.getString(
                                    R.string.main_txt_basketball_phase,
                                    resources.getString(R.string.three)
                                ) + context.resources.getString(
                                    R.string.finis
                                )
                            initAnimation(binding.txtMatchAnimation)

                        }

                        "8" -> {
                            binding.txtMatchAnimation.visibility = View.VISIBLE
                            binding.tvvs.text = item.awayScore + "-" + item.homeScore
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_e6820c
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_34a853
                                )
                            )
                            binding.tvstatus.text =
                                context.resources.getString(
                                    R.string.main_txt_basketball_phase,
                                    resources.getString(R.string.four)
                                )
                            initAnimation(binding.txtMatchAnimation)
                        }

                        "9" -> {
                            binding.txtMatchAnimation.visibility = View.VISIBLE
                            binding.tvvs.text = item.awayScore + "-" + item.homeScore
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_e6820c
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_34a853
                                )
                            )
                            binding.tvstatus.text =
                                context.resources.getString(R.string.over_time)
                            initAnimation(binding.txtMatchAnimation)
                        }

                        "10" -> {
                            binding.txtMatchAnimation.visibility = View.GONE
                            binding.tvvs.text = item.awayScore + "-" + item.homeScore
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_999999
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_34a853
                                )
                            )
                            binding.tvstatus.text =
                                context.resources.getString(R.string.main_txt_over)
                            clearAnimation(binding.txtMatchAnimation)

                        }

                        "11", "12", "13", "14", "15" -> {
                            binding.txtMatchAnimation.visibility = View.GONE
                            clearAnimation(binding.txtMatchAnimation)
                            binding.tvvs.text = "VS"
                            binding.tvstatus.visibility = View.VISIBLE
                            binding.tvstatus.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_8a91a0
                                )
                            )
                            binding.tvvs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.c_37373d
                                )
                            )
                            when (item.status) {
                                "11" -> {
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.main_txt_zd)
                                }

                                "12" -> {
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.main_txt_qx)
                                }

                                "13" -> {
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.main_txt_zd)
                                }

                                "14" -> {
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.main_txt_yz)
                                }

                                "15" -> {
                                    binding.tvstatus.text =
                                        context.resources.getString(R.string.main_txt_dd)
                                }
                            }

                        }
                    }

                }



                if (item.anchorList != null && item.anchorList.isNotEmpty()) {
                    if (item.anchorList.size > 5) {
                        for (i in 0 until item.anchorList.size) {
                            if (i > 4) {
                                item.anchorList.removeAt(i)
                            }
                        }
                        item.anchorList = item.anchorList
                    }
                    binding.conlive.visibility = View.VISIBLE
                    if (binding.rec.itemDecorationCount == 0) {//加个判断
                        binding.rec.run {
                            grid(5)
                            distance(0, 0, 0, 0)
                        }
                    }

                    binding.rec.setup {
                        addType<AnchorBean>(R.layout.item_js)
                        onBind {
                            var binding1 = getBinding<ItemJsBinding>()
                            var item1 = _data as AnchorBean
                            if (item1.nickName.length > 5) {
                                binding1.tvname.text = item1.nickName.substring(0, 4) + "..."
                            } else {
                                binding1.tvname.text = item1.nickName
                            }

                            Glide.with(context).load(item1.userLogo)
                                .placeholder(R.drawable.default_anchor_icon)
                                .thumbnail(0.1f) // 设置缩略图大小比例（例如0.1表示原图的十分之一）
                                .override(64, 64) // 指定目标宽度和高度
                                .into(binding1.ivhead)
                            binding1.linroot.setOnClickListener {
                                SoundManager.playMedia()
                                MatchDetailActivity.open(
                                    matchType = item.matchType,
                                    matchId = item.matchId,
                                    matchName = "${item.homeName}VS${item.awayName}",
                                    anchorId = item1.userId,
                                    videoUrl = ""
                                )

                            }
                        }
                    }.models = item.anchorList


                } else {
                    binding.conlive.visibility = View.GONE
                }


                binding.ivsc.clickNoRepeat(1000) {
                    judgeLogin {
//                            startAn(binding.tvcollect)
//                            stopAn(binding.tvcollect)
                        binding.tvcollect.visibility = View.VISIBLE
                        mview = binding.tvcollect
                        mview1 = binding.ivsc

                        index = modelPosition
                        if (item!!.focus) {
                            vm.getUnnotice(
                                item!!.matchId,
                                item!!.matchType
                            )


                        } else {
                            // startAn(binding.tvcollect)

                            vm.getNotice(
                                item!!.matchId,
                                item!!.matchType
                            )


                        }
                    }

                }
                binding.conroot.setOnClickListener {
                    SoundManager.playMedia()
                    MatchDetailActivity.open(
                        matchType = item.matchType,
                        matchId = item.matchId,
                        matchName = "${item.homeName}VS${item.awayName}",
                        anchorId = "",
                        videoUrl = ""
                    )

                }


            }
            itemDifferCallback = object : ItemDifferCallback {
                override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                    return (oldItem as MatchBean).matchId == (newItem as MatchBean).matchId
                }

                override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                    return (oldItem as MatchBean).homeHalfScore == (newItem as MatchBean).homeHalfScore
                }

                override fun getChangePayload(oldItem: Any, newItem: Any): Any? {
                    return true
                }
            }
        }.models = listdata


    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.matchList.observe(this) {


            mDatabind.etSearchInput.postDelayed({
                showSoftKeyboard()
            }, 200)
            if (it.size <= 0) {
                mDatabind.llSearchShow.visibility = View.GONE

                mDatabind.rlShowRe.visibility = View.VISIBLE


            } else {
                mDatabind.llSearchShow.visibility = View.VISIBLE

                mDatabind.rcHotRace.addModels(it)
                mDatabind.rlShowRe.visibility = View.GONE
            }

        }
        mViewModel.liveList.observe(this) {
            if (it.size <= 0) {
                mDatabind.state.showEmpty()
            } else {
                if (mDatabind.rcSearchList.models != null) {

                    mDatabind.rcSearchList.mutable.clear()
                    mDatabind.rcSearchList.adapter!!.notifyDataSetChanged()
                }

                mDatabind.rcSearchList.addModels(it)
                mDatabind.state.showContent()
            }
        }
        try {

            vm.unnoticeData.observe(this) {
                mview!!.setAnimation("shoucang2.json")
                mview!!.loop(false)
                mview!!.playAnimation()
                mview!!.addAnimatorListener(object :
                    Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        mview!!.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })
                (mDatabind.rcSearchListNew.models!![index] as MatchBean).focus = false

                appViewModel.updateCollection.postValue(true)
                //  mDatabind.rec.mutable.removeAt(index)
                // mDatabind.rec.bindingAdapter.notifyItemRemoved(index) // 通知更新
                mview1!!.setBackgroundResource(R.drawable.sc_shoucang_icon1)

            }
            vm.noticeData.observe(this) {

                mview!!.setAnimation("shoucang1.json")
                mview!!.loop(false)
                mview!!.playAnimation()
                mview!!.addAnimatorListener(object :
                    Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        mview!!.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })
                (mDatabind.rcSearchListNew.models!![index] as MatchBean).focus = true
                appViewModel.updateCollection.postValue(false)
                mview1!!.setBackgroundResource(R.drawable.sc_shoucang_icon2)
            }


            mViewModel.hotMatchList.observe(this) {
                if (it.isSuccess) {
                    //成功
                    when {
                        //第一页并没有数据 显示空布局界面
                        it.isFirstEmpty -> {
                            if (mDatabind.rcSearchListNew.models?.size != null) {
                                mDatabind.rcSearchListNew.mutable.clear()
                            }
                            mDatabind.smartCommon.finishRefresh()
                            mDatabind.stateNew.showEmpty()
                        }
                        //是第一页
                        it.isRefresh -> {

                            mDatabind.smartCommon.finishRefresh()
                            mDatabind.smartCommon.resetNoMoreData()
                            mDatabind.rcSearchListNew.models = it.listData
                            //mAdapter.submitList(it.listData)
                            //  mAdapter.emptyView
                            //用户类型[1:游客 2:普通用户 3:会员]
                            //  addItemBinder.notifyDataSetChanged()
                            if (it.listData.size < Constants.BASE_PAGE_SIZE) {
                                mDatabind.smartCommon.finishLoadMoreWithNoMoreData()
                            }
                            mDatabind.stateNew.showContent()


                        }
                        //不是第一页
                        else -> {
                            if (it.listData.isEmpty()) {
//                            mViewBind.smartCommon.setEnableLoadMore(false)
                                mDatabind.smartCommon.finishLoadMoreWithNoMoreData()
                            } else {
//                            mViewBind.smartCommon.setEnableLoadMore(true)
                                mDatabind.smartCommon.finishLoadMore()
//                            addItemBinder.addData(getNewData(it))
                                mDatabind.rcSearchListNew.addModels(it.listData)
                                //mAdapter.addAll(it.listD.lata)
                                //用户类型[1:游客 2:普通用户 3:会员]
                                mDatabind.stateNew.showContent()
                            }

                        }
                    }
                } else {
                    //失败
                    if (it.isRefresh) {
                        mDatabind.smartCommon.finishRefresh()
                        //如果是第一页，则显示错误界面，并提示错误信息
                        // mAdapter.submitList(null)
                        //  addItemBinder.setEmptyView(empty)
                    } else {
                        mDatabind.smartCommon.finishLoadMore(false)
                    }
                }
            }


        }catch (e: Exception) {
        }



    }

    /**
     * 打开软键盘
     */
    private fun showSoftKeyboard() {
        mDatabind.etSearchInput.requestFocus() // 获取焦点
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mDatabind.etSearchInput, InputMethodManager.SHOW_IMPLICIT) // 打开软键盘
    }

    // 隐藏软键盘的函数
    fun hideKeyboard(context: Context, view: AppCompatEditText) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }
    //没撒用
    fun clearAnimation(view: LottieAnimationView) {

    }
    fun initAnimation(view: LottieAnimationView) {

    }

}