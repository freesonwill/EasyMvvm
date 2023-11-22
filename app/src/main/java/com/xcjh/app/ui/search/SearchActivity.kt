package com.xcjh.app.ui.search


import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.drake.brv.utils.*
import com.gyf.immersionbar.ImmersionBar
import com.xcjh.app.R
import com.xcjh.app.base.BaseActivity
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.bean.MatchBean
import com.xcjh.app.databinding.ActivitySearchBinding
import com.xcjh.app.databinding.ItemMainLiveListBinding
import com.xcjh.app.databinding.ItemSearchHotTitleBinding
import com.xcjh.app.ui.details.MatchDetailActivity
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.bindadapter.CustomBindAdapter.afterTextChanged
import com.xcjh.base_lib.utils.dp2px
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.view.clickNoRepeat

class SearchActivity  : BaseActivity<SearchVm, ActivitySearchBinding>() {
    private var searchType:Int=0  //0是搜索  1是取消
//    MatchDetailActivity.open(matchType =bean.matchType, matchId = bean.matchId,matchName = "", anchorId = bean.userId,videoUrl = bean.playUrl )

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .titleBar(mDatabind.rlSearchTitle)
            .init()
        mViewModel.getHotOngoingMatch()
        adapter()

        mDatabind.etSearchInput.afterTextChanged{
            if (it.isNotEmpty()) {
                mDatabind.ivClear.visibility=View.VISIBLE
                searchType=0
                mDatabind.txtSearchClick.text=resources.getString(R.string.search)
            }else{
                mDatabind.ivClear.visibility=View.GONE
            }
        }
        //删除输入的内容
        mDatabind.ivClear.clickNoRepeat{
            mDatabind.etSearchInput.setText("")
        }


        mDatabind.etSearchInput.setOnEditorActionListener { v, actionId, event ->
            if(mDatabind.etSearchInput.text.toString().trim().isEmpty()){
                myToast(getString(R.string.contact_hint_input))

            }else{
                mViewModel.getNowLive(mDatabind.etSearchInput.text.toString().trim())
                searchType=1
                mDatabind.txtSearchClick.text=resources.getString(R.string.cancel)
                mDatabind.llSearchShow.visibility=View.GONE
                mDatabind.state.visibility=View.VISIBLE
            }

            true
        }

        //根据是否搜索来判断
        mDatabind.txtSearchClick.clickNoRepeat{
            if(mDatabind.etSearchInput.text.toString().trim().isEmpty()){
                myToast(getString(R.string.contact_hint_input))
                return@clickNoRepeat
            }

            if(searchType==0){
                mViewModel.getNowLive(mDatabind.etSearchInput.text.toString().trim())
                searchType=1
                mDatabind.txtSearchClick.text=resources.getString(R.string.cancel)
                mDatabind.llSearchShow.visibility=View.GONE
                mDatabind.state.visibility=View.VISIBLE
            }else{
                searchType=0
                mDatabind.txtSearchClick.text=resources.getString(R.string.search)
                mDatabind.llSearchShow.visibility=View.VISIBLE
                mDatabind.state.visibility=View.GONE
                mDatabind.etSearchInput.setText("")
                if(mDatabind.rcSearchList.models!=null){
                    mDatabind.rcSearchList.mutable.clear()
                }

            }
        }
    }

    fun adapter(){
        mDatabind.rcHotRace.linear().setup {
            addType<BeingLiveBean>(R.layout.item_search_hot_title)

            onBind {
                when (itemViewType) {
                    R.layout.item_search_hot_title -> {
                        var bindingItem=getBinding<ItemSearchHotTitleBinding>()
                        var  bean=_data as BeingLiveBean
                        //比赛类型 1足球，2篮球,可用值:1,2
                        if(bean.matchType.equals("1")){
                            bindingItem.txtHotNAme.text="${bean.competitionName}\n\n${bean.homeTeamName}VS${bean.awayTeamName}"
                        }else{
                            bindingItem.txtHotNAme.text="${bean.competitionName}\n\n${bean.awayTeamName }VS${bean.homeTeamName}"
                        }



                    }


                }

            }
            R.id.txtHotNAme.onClick {
                val bean=_data as BeingLiveBean
                MatchDetailActivity.open(matchType =bean.matchType, matchId = bean.matchId,matchName = "${bean.homeTeamName}VS${bean.awayTeamName}", anchorId = bean.userId,videoUrl = bean.playUrl )

            }


        }


        mDatabind.rcSearchList.grid(2).setup {
            addType<BeingLiveBean>(R.layout.item_main_live_list)
            onBind {
                when (itemViewType) {
                    R.layout.item_main_live_list -> {

                        var bindingItem=getBinding<ItemMainLiveListBinding>()
                        var  bean=_data as BeingLiveBean
                        Glide.with(context)
                            .load(bean.titlePage) // 替换为您要加载的图片 URL
                            .error(R.drawable.main_top_load)
                            .placeholder(R.drawable.main_top_load)
                            .into(bindingItem.ivLiveBe)
                        Glide.with(context)
                            .load(bean.userLogo) // 替换为您要加载的图片 URL
                            .error(R.drawable.default_anchor_icon)
                            .placeholder(R.drawable.default_anchor_icon)
                            .into(bindingItem.ivLiveHead)
                        bindingItem.txtLiveName.text=bean.nickName
                        //比赛类型 1足球，2篮球,可用值:1,2
                        if(bean.matchType.equals("1")){
                            bindingItem.txtLiveTeam.text="${bean.homeTeamName}VS${bean.awayTeamName}"
                        }else{
                            bindingItem.txtLiveTeam.text="${bean.awayTeamName }VS${bean.homeTeamName}"
                        }

                        bindingItem.txtLiveCompetition.text=bean.competitionName
                        if(bean.hotValue<=9999){
                            bindingItem.txtLiveHeat.text="${bean.hotValue}"
                        }else{
                            bindingItem.txtLiveHeat.text="9999+"
                        }

                        if(layoutPosition%2==0){
                            val layoutParams = bindingItem.llLiveSpacing.layoutParams as ViewGroup.MarginLayoutParams
                            layoutParams.setMargins(0, 0, context.dp2px(13), context.dp2px(20))
                            bindingItem.llLiveSpacing.layoutParams =layoutParams
                        }else{
                            val layoutParams = bindingItem.llLiveSpacing.layoutParams as ViewGroup.MarginLayoutParams
                            layoutParams.setMargins(0, 0, 0, context.dp2px(20))
                            bindingItem.llLiveSpacing.layoutParams =layoutParams
                        }
                    }

                }

            }
            R.id.llLiveSpacing.onClick {
                val bean=_data as BeingLiveBean
                MatchDetailActivity.open(matchType =bean.matchType, matchId = bean.matchId,matchName = "${bean.homeTeamName}VS${bean.awayTeamName}", anchorId = bean.userId,videoUrl = bean.playUrl )

            }

        }


    }

    override fun createObserver() {
        super.createObserver()
        mViewModel.matchList.observe(this){
            if(it.size<=0){
                mDatabind.llSearchShow.visibility= View.GONE
                mDatabind.state.visibility=View.VISIBLE
            }else{
                mDatabind.llSearchShow.visibility= View.VISIBLE
                mDatabind.state.visibility=View.GONE
                mDatabind.rcHotRace.addModels(it)

            }

        }
        mViewModel.liveList.observe(this){
            if(it.size<=0){
                mDatabind.state.showEmpty()
            }else{
                if(mDatabind.rcSearchList.models!=null){
                    var remove=mDatabind.rcSearchList.mutable
                    mDatabind.rcSearchList.mutable.removeAll(remove)
                    mDatabind.rcSearchList.adapter!!.notifyItemRangeRemoved(0, remove.size)
                }

                mDatabind.rcSearchList.addModels(it)
                mDatabind.state.showContent()
            }
        }




    }

}