package com.xcjh.app.ui.home.home.tab

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.addModels
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.grid
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.mutable
import com.drake.brv.utils.setDifferModels
import com.drake.brv.utils.setup
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.xcjh.app.R
import com.xcjh.app.adapter.ImageTitleAdapter
import com.xcjh.app.appViewModel
import com.xcjh.app.base.BaseFragment
import com.xcjh.app.bean.AdvertisementBanner
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.bean.HotReq
import com.xcjh.app.bean.MainTxtBean
import com.xcjh.app.bean.MatchBean
import com.xcjh.app.databinding.FragmentMainRecommendBinding
import com.xcjh.app.databinding.ItemMainBaanerBinding
import com.xcjh.app.databinding.ItemMainLiveListBinding
import com.xcjh.app.databinding.ItemMainProceedBinding
import com.xcjh.app.databinding.ItemMainTxtBinding
import com.xcjh.app.databinding.ItemUnderWayBinding
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.app.web.WebActivity
import com.xcjh.app.websocket.MyWsManager
import com.xcjh.app.websocket.bean.LiveStatus
import com.xcjh.app.websocket.bean.ReceiveChangeMsg
import com.xcjh.app.websocket.bean.ReceiveChatMsg
import com.xcjh.app.websocket.bean.ReceiveWsBean
import com.xcjh.app.websocket.listener.C2CListener
import com.xcjh.app.websocket.listener.LiveStatusListener
import com.xcjh.base_lib.App
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.utils.dp2px
import com.youth.banner.util.BannerUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 首页推荐页面碎片
 */
class MainRecommendFragment : BaseFragment<MainRecommendVm, FragmentMainRecommendBinding>() {
    override fun initView(savedInstanceState: Bundle?) {

        //首页轮询
        appViewModel.appPolling.observeForever{
            if(isAdded){
                mViewModel.getOngoingMatchList(HotReq())
            }
        }
        mDatabind.smartCommon.setHeaderHeight(40f)
        mDatabind.smartCommon.setDisableContentWhenRefresh(true)//是否在刷新的时候禁止列表的操作
        mDatabind.smartCommon.setDisableContentWhenLoading(true)//是否在加载的时候禁止列表的操作
        mDatabind.smartCommon.setRefreshHeader( ClassicsHeader(requireContext()))
        MyWsManager.getInstance(App.app)
            ?.setLiveStatusListener(this.toString(), object : LiveStatusListener {
                override fun onOpenLive(bean: LiveStatus) {
                    var isShow=false
                    if(mDatabind.rcvRecommend.models!=null){
                        for (i in 0 until  mDatabind.rcvRecommend.mutable.size){
                            if(mDatabind.rcvRecommend.mutable[i] is MainTxtBean){
                                for (j in 0 until  (mDatabind.rcvRecommend.mutable[i] as MainTxtBean).list.size){
                                    if((mDatabind.rcvRecommend.mutable[i] as MainTxtBean).list[j].userId.equals(bean.anchorId)){
                                        isShow=true
                                    }
                                }
                            }

                        }
                    }
                    if(!isShow){
                        mViewModel.getOngoingMatchList(bean.id)
                    }
                }

                override fun onCloseLive(bean: LiveStatus) {
                    super.onCloseLive(bean)
                    if(mDatabind.rcvRecommend.models!=null){
                        for (i in 0 until  mDatabind.rcvRecommend.mutable.size){
                            if(mDatabind.rcvRecommend.mutable[i] is MainTxtBean){
                                for (j in 0 until  (mDatabind.rcvRecommend.mutable[i] as MainTxtBean).list.size){
                                    if((mDatabind.rcvRecommend.mutable[i] as MainTxtBean).list[j].userId.equals(bean.anchorId)){
                                        (mDatabind.rcvRecommend.mutable[i] as MainTxtBean).list.removeAt(j)
                                        mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()
                                    }
                                }



                            }

                        }
                    }
                }
            })


        MyWsManager.getInstance(App.app)?.setC2CListener(javaClass.name, object : C2CListener {

            override fun onSendMsgIsOk(isOk: Boolean, bean: ReceiveWsBean<*>) {

            }

            override fun onC2CReceive(chat: ReceiveChatMsg) {

            }

            override fun onChangeReceive(chat: ArrayList<ReceiveChangeMsg>) {
                var refresh=ArrayList<Int>()
                if(mDatabind.rcvRecommend.models!=null){
                    for (i in 0 until  mDatabind.rcvRecommend.mutable.size){
                        if(mDatabind.rcvRecommend.mutable[i] is MatchBean){
                            for (j in 0 until   (mDatabind.rcvRecommend.mutable[i] as MatchBean).list.size){
                                for (k in 0 until  chat.size){
                                    if((mDatabind.rcvRecommend.mutable[i] as MatchBean).list[j].matchId.equals(chat[k].matchId.toString())&&
                                        (mDatabind.rcvRecommend.mutable[i] as MatchBean).list[j].matchType.equals(chat[k].matchType.toString())){
                                        refresh.add(k)
                                        (mDatabind.rcvRecommend.mutable[i] as MatchBean).list[j].awayHalfScore=chat[k].awayHalfScore.toString()
                                        (mDatabind.rcvRecommend.mutable[i] as MatchBean).list[j].awayScore=chat[k].awayScore.toString()
                                        (mDatabind.rcvRecommend.mutable[i] as MatchBean).list[j].homeHalfScore=chat[k].homeHalfScore.toString()
                                        (mDatabind.rcvRecommend.mutable[i] as MatchBean).list[j].homeScore=chat[k].homeScore.toString()
                                        (mDatabind.rcvRecommend.mutable[i] as MatchBean).list[j].runTime=chat[k].runTime.toString()
                                        (mDatabind.rcvRecommend.mutable[i] as MatchBean).list[j].status=chat[k].status.toString()
                                    }
                                }
                            }

                        }

                    }
                    if(refresh.size>0){
                        for (i in 0 until  mDatabind.rcvRecommend.mutable.size){
                            if(mDatabind.rcvRecommend.mutable[i] is MatchBean){
//                                mDatabind.rcvRecommend.bindingAdapter.notifyItemChanged(i)
                            }

                        }
                    }

                }


            }

        })


        dateAdapter()
        //设置正在直播的适配器第三个适配器
       var list=ArrayList<MainTxtBean>()
        list.add(MainTxtBean())
        mDatabind.rcvRecommend.addModels(list)

        //获取广告
        mViewModel.getBannerList()
        mViewModel.getOngoingMatchList(HotReq())
        mViewModel.getNowLive(true)
        //设置下拉刷新的高度-----
        mDatabind.smartCommon.setFooterHeight(20F)
        mDatabind.smartCommon.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                mViewModel.getBannerList()
                mViewModel.getOngoingMatchList(HotReq())
                mViewModel.getNowLive(true)

            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                mViewModel.getNowLive(false)
            }
        })


    }




    override fun createObserver() {
        super.createObserver()

        //打开直播的时候获取到详情
        mViewModel.beingLive.observe(this){
            if(mDatabind.rcvRecommend.models!=null){
                for (i in 0 until mDatabind.rcvRecommend.mutable!!.size) {
                    if(mDatabind.rcvRecommend.mutable[i] is MainTxtBean){
                        (mDatabind.rcvRecommend.mutable[i] as MainTxtBean).list.add(it)
                        mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()
                        return@observe
                    }
                }
            }
        }
        //登录或者登出
        appViewModel.updateLoginEvent.observe(this){
            //获取广告
            mViewModel.getBannerList()
            mViewModel.getOngoingMatchList(HotReq())
            mViewModel.getNowLive(true)
        }


        //获取广告
        mViewModel.bannerList.observe(this){
            if(it.size>0){
            try {
                if(mDatabind.rcvRecommend.mutable!=null){
                    if (mDatabind.rcvRecommend.mutable[0] is AdvertisementBanner) {
                        (mDatabind.rcvRecommend.mutable[0] as AdvertisementBanner).list.clear()
                        (mDatabind.rcvRecommend.mutable[0] as AdvertisementBanner).list.addAll(it)
                        mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()
                    }else{
                        var advertisementBanner=AdvertisementBanner()
                        advertisementBanner.list.addAll(it)
                        var list=ArrayList<AdvertisementBanner> ()
                        list.add(advertisementBanner)
                        mDatabind.rcvRecommend.addModels(list, index = 0)
                        mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()
                    }
                }
            }catch (e:NullPointerException){
                var advertisementBanner=AdvertisementBanner()
                advertisementBanner.list.addAll(it)
                var list=ArrayList<AdvertisementBanner> ()
                list.add(advertisementBanner)
                mDatabind.rcvRecommend.addModels(list)
                mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()
                 }
            }


        }
        //获取首页的热门比赛
        mViewModel.hotList.observe(this){
            if(it.size>=1){
                try {

                    if(mDatabind.rcvRecommend.mutable.size>=1){
                        if (mDatabind.rcvRecommend.mutable[1] is MatchBean) {
                            (mDatabind.rcvRecommend.mutable[1] as MatchBean).list.clear()
                            (mDatabind.rcvRecommend.mutable[1] as MatchBean).list.addAll(it)
                            mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()
                     }else{
                            var matchBean=MatchBean()
                            matchBean.list.addAll(it)
                            //热门赛事
                            var mainHaveList=ArrayList<MatchBean>()
                            mainHaveList.add(matchBean)
                            mDatabind.rcvRecommend.addModels(mainHaveList, index = 1)
                            mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()
                     }

                    }else{
                        var matchBean=MatchBean()
                        matchBean.list.addAll(it)
                        //热门赛事
                        var mainHaveList=ArrayList<MatchBean>()
                        mainHaveList.add(matchBean)
                        mDatabind.rcvRecommend.addModels(mainHaveList, index = 1)
                        mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()


                    }


                }catch (e:NullPointerException){
                    var matchBean=MatchBean()
                    matchBean.list.addAll(it)
                    //热门赛事
                    var mainHaveList=ArrayList<MatchBean>()
                    mainHaveList.add(matchBean)
                    mDatabind.rcvRecommend.addModels(mainHaveList, index = 1)
                    mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()

                }
            }

            // 调度定时任务
//            timer.scheduleAtFixedRate(MyTimerTask(), delay, period)
        }
        //正在直播的热门比赛
        mViewModel.liveList.observe(this){
            if (it.isSuccess) {
                //成功
                when {
                    //第一页并没有数据 显示空布局界面
                    it.isFirstEmpty -> {
                        mDatabind.smartCommon.finishRefresh()
                       if(mDatabind.rcvRecommend.models!=null){
                           for (i in 0 until mDatabind.rcvRecommend.mutable!!.size) {
                               if(mDatabind.rcvRecommend.mutable[i] is MainTxtBean){
                                 (mDatabind.rcvRecommend.mutable[i] as MainTxtBean).list.clear()
                                  mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()
                                   return@observe
                               }
                           }
                       }

                    }
                    //是第一页
                    it.isRefresh -> {
                        mDatabind.smartCommon.finishRefresh()
                        mDatabind.smartCommon.resetNoMoreData()
                        if(it.listData.size<20){
                            mDatabind.smartCommon.finishLoadMore()
                            mDatabind.smartCommon.setEnableLoadMore(false)
//                            mDatabind.smartCommon.finishLoadMoreWithNoMoreData()
                        }else{
                            mDatabind.smartCommon.finishLoadMore()
                            mDatabind.smartCommon.setEnableLoadMore(true)
                        }
                        if(mDatabind.rcvRecommend.models!=null){
                            for (i in 0 until mDatabind.rcvRecommend.mutable!!.size) {
                                if(mDatabind.rcvRecommend.mutable[i] is MainTxtBean){
                                    (mDatabind.rcvRecommend.mutable[i] as MainTxtBean).list.clear()
                                    (mDatabind.rcvRecommend.mutable[i] as MainTxtBean).list.addAll(it.listData)
                                    mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()
                                    return@observe
                                }
                            }
                        }
                    }
                    //不是第一页
                    else -> {
                        if(it.listData.isEmpty()) {
                            mDatabind.smartCommon.finishLoadMore()
                            mDatabind.smartCommon.setEnableLoadMore(false)
//                            mDatabind.smartCommon.finishLoadMoreWithNoMoreData()
                        }else{
                            mDatabind.smartCommon.setEnableLoadMore(true)
                            mDatabind.smartCommon.finishLoadMore()
                            if(mDatabind.rcvRecommend.models!=null){
                                for (i in 0 until mDatabind.rcvRecommend.mutable!!.size) {
                                    if(mDatabind.rcvRecommend.mutable[i] is MainTxtBean){
                                        (mDatabind.rcvRecommend.mutable[i] as MainTxtBean).list.addAll(it.listData)
                                        mDatabind.rcvRecommend.bindingAdapter.notifyDataSetChanged()
                                        return@observe
                                    }
                                }
                            }
                        }



                    }

                }

            }else{
                mDatabind.smartCommon.finishRefresh()
                mDatabind.smartCommon.resetNoMoreData()

            }

        }



    }


    /**
     * 适配器
     */
    fun dateAdapter(){
        mDatabind.rcvRecommend.linear().setup {
            addType<AdvertisementBanner>(R.layout.item_main_baaner)
            addType<MatchBean>(R.layout.item_main_proceed)
            addType<MainTxtBean>(R.layout.item_main_txt)
            addType<BeingLiveBean>(R.layout.item_main_live_list)
            onCreate {
                when (itemViewType) {
                    R.layout.item_main_baaner -> {
//                var binding=getBinding<AdapterTitleImageBinding>()


                    }
                    R.layout.item_main_proceed -> {
//                var binding=getBinding<AdapterTitleImageBinding>()
                    }

                }
            }
            onBind {
                when (itemViewType) {
                    //广告位
                    R.layout.item_main_baaner -> {
                        var binding=getBinding<ItemMainBaanerBinding>()
                        var ad=_data as AdvertisementBanner
                        var  imageAdapter= ImageTitleAdapter(ad.list)
                        binding.banner.setAdapter(imageAdapter)
                        imageAdapter.setOnBannerListener { data, position ->
                            startNewActivity<WebActivity>() {
                                this.putExtra(Constants.WEB_URL, data.targetUrl)
                                this.putExtra(Constants.CHAT_TITLE, getString(R.string.my_app_name))
                            }
                        }
                        //在布局文件中使用指示器，这样更灵活
                        binding.banner.setIndicator(binding.indicator, false)
                        //选中的宽度
                        binding.banner.setIndicatorSelectedWidth(BannerUtils.dp2px(20f))
                        binding.banner.setIndicatorSelectedColor(ContextCompat.getColor(requireContext(),
                            com.xcjh.base_lib.R.color.white))
                        binding.banner.setIndicatorNormalColor(ContextCompat.getColor(requireContext(),R.color.c_8c8c8c))
                        //间距
                        binding.banner.setIndicatorSpace(BannerUtils.dp2px(5f))
                        //未选择的宽度 和选择的
                        binding.banner.setIndicatorWidth(BannerUtils.dp2px(5f),BannerUtils.dp2px(20f))
//                        //高度
                        binding.banner.setIndicatorHeight(BannerUtils.dp2px(5f))

                    }
                    R.layout.item_main_proceed -> {//正在进行中的比赛
                        var binding=getBinding<ItemMainProceedBinding>()
                        var matchBeanNew=_data as MatchBean
//                        if(matchBeanNew.list.size>0){
//                            val date = Date(matchBeanNew.list[0].matchTime.toLong())
//                            var formatter = SimpleDateFormat("MM月dd日", Locale.getDefault())
//                            binding.tvProceedDate.text = resources.getString(R.string.main_txt_date,formatter.format(date))
//                        }
                        binding.tvProceedSession.text=resources.getString(R.string.main_txt_session,"${matchBeanNew.list.size}")
                        binding.rcvReProceed.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                        binding.rcvReProceed.setup{
                            addType<MatchBean>(R.layout.item_under_way)
//                            // 如果要求刷新不白屏请参考以下代码逻辑
//                             itemDifferCallback = object : ItemDifferCallback {
//
//                                 override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
//                                     return (oldItem as MatchBean).matchId == (newItem as MatchBean).matchId
//                                 }
//
//                                 override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
//                                     return (oldItem as MatchBean).matchType == (newItem as MatchBean).matchType
//                                 }
//
//
//                                 override fun getChangePayload(oldItem: Any, newItem: Any): Any? {
//                                     return true
//                                 }
//                             }
                            onBind {
                                when (itemViewType) {
                                    R.layout.item_under_way -> {
                                        var binding=getBinding<ItemUnderWayBinding>()
                                        var matchBean=_data as MatchBean
                                        //主队
                                        binding.txtCompetition.text=matchBean.competitionName
                                        binding.txtMatchAnimation.visibility=View.GONE
                                        //比赛类型：1：足球；2：篮球,可用值:1,2
                                        binding.txtMatchTime.visibility=View.GONE
                                        binding.txtMatchIsStart.visibility=View.GONE
                                        if(matchBean.matchType.equals("2")){
                                            binding.txtMatchStatus.visibility=View.GONE
                                            Glide.with(requireContext())
                                                .load(matchBean.awayLogo) // 替换为您要加载的图片 URL
                                                .error(R.drawable.default_team_logo)
                                                .placeholder(R.drawable.default_team_logo)
                                                .into(binding.txtWayHome)

                                            //客队
                                            Glide.with(requireContext())
                                                .load(matchBean.homeLogo) // 替换为您要加载的图片 URL
                                                .error(R.drawable.main_load_icon)
                                                .placeholder(R.drawable.main_load_icon)
                                                .into(binding.ivGuestIcon)
                                            binding.txtHomeName.text=matchBean.awayName
                                            binding.txtGuestName.text=matchBean.homeName
                                            if(matchBean.status.equals("1")){
                                                binding.txtHomeScore.text=""
                                                binding.txtGuestScore.text=""
                                            }else{
                                                binding.txtHomeScore.text=matchBean.awayScore
                                                binding.txtGuestScore.text=matchBean. homeScore
                                            }
                                            if(matchBean.status.equals("0")||matchBean.status.equals("1")||matchBean.status.equals("10")
                                                ||matchBean.status.equals("11")||matchBean.status.equals("12")||matchBean.status.equals("13")
                                                ||matchBean.status.equals("14")||matchBean.status.equals("15")){
                                                binding.txtMatchStatus.visibility=View.GONE
                                                binding.txtMatchStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.c_f5f5f5))
                                                val date = Date(matchBean.matchTime.toLong())
                                                var formatter = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                                                binding.txtMatchTime.text=formatter.format(date)
                                                binding.txtMatchTime.visibility=View.VISIBLE


                                            }else if(matchBean.status.equals("2")||matchBean.status.equals("3")){
                                                binding.txtMatchStatus.visibility=View.VISIBLE
                                                binding.txtMatchStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.c_f69521))
                                                binding.txtMatchStatus.text=resources.getString(R.string.main_txt_basketball_phase,"一")
                                                binding.txtMatchIsStart.visibility=View.VISIBLE
                                            }else if(matchBean.status.equals("4")||matchBean.status.equals("5")){
                                                binding.txtMatchStatus.visibility=View.VISIBLE
                                                binding.txtMatchStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.c_f69521))
                                                binding.txtMatchStatus.text=resources.getString(R.string.main_txt_basketball_phase,"二")
                                                binding.txtMatchIsStart.visibility=View.VISIBLE
                                            }else if(matchBean.status.equals("6")||matchBean.status.equals("7")){
                                                binding.txtMatchStatus.visibility=View.VISIBLE
                                                binding.txtMatchStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.c_f69521))
                                                binding.txtMatchStatus.text=resources.getString(R.string.main_txt_basketball_phase,"三")
                                                binding.txtMatchIsStart.visibility=View.VISIBLE
                                            }else if(matchBean.status.equals("8")){
                                                binding.txtMatchStatus.visibility=View.VISIBLE
                                                binding.txtMatchStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.c_f69521))
                                                binding.txtMatchStatus.text=resources.getString(R.string.main_txt_basketball_phase,"四")
                                                binding.txtMatchIsStart.visibility=View.VISIBLE
                                            }else if(matchBean.status.equals("9")){
                                                binding.txtMatchStatus.visibility=View.VISIBLE
                                                binding.txtMatchStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.c_f69521))
                                                binding.txtMatchStatus.text=resources.getString(R.string.home_txt_overtime)
                                                binding.txtMatchIsStart.visibility=View.VISIBLE
                                            }
                                        }else{

                                            Glide.with(requireContext())
                                                .load(matchBean.homeLogo) // 替换为您要加载的图片 URL
                                                .error(R.drawable.default_team_logo)
                                                .placeholder(R.drawable.default_team_logo)
                                                .into(binding.txtWayHome)


                                            //客队
                                            Glide.with(requireContext())
                                                .load(matchBean.awayLogo) // 替换为您要加载的图片 URL
                                                .error(R.drawable.default_team_logo)
                                                .placeholder(R.drawable.default_team_logo)
                                                .into(binding.ivGuestIcon)



                                            binding.txtHomeName.text=matchBean.homeName
                                            binding.txtMatchStatus.visibility=View.GONE
                                            if(matchBean.status.equals("1")){
                                                binding.txtHomeScore.text=""
                                                binding.txtGuestScore.text=""
                                            }else{
                                                binding.txtHomeScore.text=matchBean.homeScore
                                                binding.txtGuestScore.text=matchBean.awayScore
                                            }

                                            binding.txtGuestName.text=matchBean.awayName



                                            if(matchBean.status.equals("0")){
                                                binding.txtMatchStatus.visibility=View.GONE

                                            }else if(matchBean.status.equals("1")){
                                                binding.txtMatchStatus.visibility=View.GONE
                                                binding.txtMatchStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.c_f5f5f5))

                                                val date = Date(matchBean.matchTime.toLong())
                                                var formatter = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                                                binding.txtMatchTime.visibility=View.VISIBLE
                                                binding.txtMatchTime.text=formatter.format(date)
                                            }else if(matchBean.status.equals("3")){
                                                binding.txtMatchStatus.visibility=View.VISIBLE
                                                binding.txtMatchStatus.text=resources.getString(R.string.zc)
                                                binding.txtMatchStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.c_f69521))
                                            } else if(matchBean.status.equals("2")||matchBean.status.equals("4")){
                                                binding.txtMatchStatus.visibility=View.VISIBLE
                                                binding.txtMatchAnimation.visibility=View.VISIBLE
                                                binding.txtMatchIsStart.visibility=View.VISIBLE
                                                binding.txtMatchStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.c_f69521))
                                                if(matchBean.runTime!=null){
//                                                    binding.txtMatchStatus.text=resources.getString(R.string.main_txt_under,matchBean.runTime)
                                                    binding.txtMatchStatus.text=matchBean.runTime
                                                }

                                                val fadeIn = ObjectAnimator.ofFloat(binding.txtMatchAnimation, "alpha", 0f, 1f)
                                                fadeIn.duration = 500
                                                fadeIn.startDelay = 200 // 延迟200毫秒开始动画
                                                val fadeOut = ObjectAnimator.ofFloat(binding.txtMatchAnimation, "alpha", 1f, 0f)
                                                fadeOut.duration = 500
                                                fadeOut.startDelay = 200 // 延迟200毫秒开始动画
                                                val animatorSet = AnimatorSet()
                                                animatorSet.playSequentially(fadeIn, fadeOut) // 顺序播放渐显和渐隐动画
                                                animatorSet.startDelay = 200 // 延迟200毫秒开始第一次播放动画
                                                animatorSet.addListener(object : AnimatorListenerAdapter() {
                                                    override fun onAnimationEnd(animation: Animator) {
                                                        // 动画结束时重新播放
                                                        super.onAnimationEnd(animation)
                                                        animatorSet.start()
                                                    }
                                                })
                                                animatorSet.cancel()
                                                animatorSet.start()
                                            }else{
                                                binding.txtMatchStatus.visibility=View.VISIBLE
                                                binding.txtMatchStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.c_f5f5f5))

                                                val date = Date(matchBean.matchTime.toLong())
                                                var formatter = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                                                binding.txtMatchStatus.text=formatter.format(date)

                                            }


                                        }

                                    }

                                }


                            }

                            R.id.llcClickRecommended.onClick {
                                val bean=_data as MatchBean
                                if(bean.anchorList!=null&&bean.anchorList.size>=1){
                                    MatchDetailActivity.open(matchType =bean.matchType, matchId = bean.matchId,matchName = "${bean.homeName}VS${bean.awayName}", anchorId = bean.anchorList[0].userId )
                                }else{
                                    MatchDetailActivity.open(matchType =bean.matchType, matchId = bean.matchId,matchName = "${bean.homeName}VS${bean.awayName}" )

                                }

                            }
                        }
                        binding.rcvReProceed.setDifferModels(matchBeanNew.list)
//                        binding.rcvReProceed.models=matchBeanNew.list

                    }
                    R.layout.item_main_txt -> {//正在直播的文字,和正在直播的列表
                        var binding=getBinding<ItemMainTxtBinding>()
                        var mainTxtBean=_data as MainTxtBean
                        if(mainTxtBean.list.size>0){
                            binding.rvExplore.visibility= View.VISIBLE
                            binding.llShowTxt.visibility= View.GONE
                            binding.rvExplore.grid(2).setup {
                                addType<BeingLiveBean>(R.layout.item_main_live_list)
                                onBind {
                                    when (itemViewType) {
                                        R.layout.item_main_live_list -> {
                                            setLiveMatchItem()
                                        }
                                    }
                                }
                                R.id.llLiveSpacing.onClick {
                                    val bean=_data as BeingLiveBean
                                    MatchDetailActivity.open(matchType =bean.matchType, matchId = bean.matchId,matchName = "${bean.homeTeamName}VS${bean.awayTeamName}", anchorId = bean.userId,videoUrl = bean.playUrl )
                                }
                            }
                            binding.rvExplore.addModels(mainTxtBean.list)
                        }else{
                            binding.rvExplore.visibility= View.GONE
                            binding.llShowTxt.visibility= View.VISIBLE
                        }
                    }
                }
            }

        }
    }
}

fun BindingAdapter.BindingViewHolder.setLiveMatchItem() {
    val bindingItem= getBinding<ItemMainLiveListBinding>()
    val bean = _data as BeingLiveBean
    Glide.with(context)
        .load(bean.titlePage)
        .error(R.drawable.main_top_load)
        .placeholder(R.drawable.main_top_load)
        .into(bindingItem.ivLiveBe)
    Glide.with(context)
        .load(bean.userLogo) // 替换为您要加载的图片 URL
        .error(R.drawable.default_anchor_icon)
        .placeholder(R.drawable.default_anchor_icon)
        .into(bindingItem.ivLiveHead)
    bindingItem.txtLiveName.text = bean.nickName
    if (bean.matchType == "1") {
        bindingItem.txtLiveTeam.text =
            "${bean.homeTeamName} VS ${bean.awayTeamName}"
    } else {
        bindingItem.txtLiveTeam.text =
            "${bean.awayTeamName} VS ${bean.homeTeamName}"
    }
    bindingItem.txtLiveCompetition.text = bean.competitionName
    if (bean.hotValue <= 9999) {
        bindingItem.txtLiveHeat.text = "${bean.hotValue}"
    } else {
        bindingItem.txtLiveHeat.text = "9999+"
    }
    if (layoutPosition % 2 == 0) {
        val layoutParams =
            bindingItem.llLiveSpacing.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, 0, context.dp2px(3), context.dp2px(8))
        bindingItem.llLiveSpacing.layoutParams = layoutParams
    } else {
        val layoutParams =
            bindingItem.llLiveSpacing.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(context.dp2px(3), 0, 0, context.dp2px(8))
        bindingItem.llLiveSpacing.layoutParams = layoutParams
    }
}