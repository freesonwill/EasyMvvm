package com.xcjh.app.ui.search


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

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .navigationBarDarkIcon(true)
            .navigationBarColor(R.color.c_ffffff)
            .titleBar(mDatabind.rlSearchTitle)
            .init()

        // 将搜索按钮始终设为可用
//        mDatabind.etSearchInput.isEnablesReturnKeyAutomatically = false
        // 将搜索 EditText 的 imeOptions 设置为 actionSearch



        mViewModel.getHotOngoingMatch()
        adapter()
        mDatabind.state.apply {
            onEmpty {
                var icon=this.findViewById<AppCompatImageView>(R.id.ivEmptyIcon)
                var txt=this.findViewById<AppCompatTextView>(R.id.txtEmptyName)
                icon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.zwt_search))
                txt.text=resources.getString(R.string.search_txt_empty_name)

            }

        }

        mDatabind.etSearchInput.afterTextChanged{

            if (it.isNotEmpty()) {
                mDatabind.ivClear.visibility=View.VISIBLE
                searchType=0
//                mDatabind.txtSearchClick.text=resources.getString(R.string.search)

                mViewModel.getNowLive(mDatabind.etSearchInput.text.toString())
                searchType=1
//                mDatabind.txtSearchClick.text=resources.getString(R.string.cancel)
                mDatabind.llSearchShow.visibility=View.GONE
                mDatabind.state.visibility=View.VISIBLE
            }else{
                mDatabind.ivClear.visibility=View.GONE


                if(mDatabind.rcHotRace.models!=null&& mDatabind.rcHotRace.models!!.isNotEmpty()){
                    mDatabind.llSearchShow.visibility=View.VISIBLE
                }else{
                    mDatabind.llSearchShow.visibility=View.GONE
                }

                mDatabind.state.visibility=View.GONE

            }
        }
        //删除输入的内容
        mDatabind.ivClear.clickNoRepeat{
            mDatabind.etSearchInput.setText("")
            if(mDatabind.rcHotRace.models!=null&& mDatabind.rcHotRace.models!!.isNotEmpty()){
                mDatabind.llSearchShow.visibility=View.VISIBLE
            }else{
                mDatabind.llSearchShow.visibility=View.GONE
            }

            mDatabind.state.visibility=View.GONE
        }


        mDatabind.etSearchInput.setOnEditorActionListener { v, actionId, event ->

            if(mDatabind.etSearchInput.text.toString().isEmpty()){
                myToast(getString(R.string.contact_hint_input))

            }else{
                hideKeyboard(this, mDatabind.etSearchInput)
                mViewModel.getNowLive(mDatabind.etSearchInput.text.toString())
                searchType=1
//                mDatabind.txtSearchClick.text=resources.getString(R.string.cancel)
                mDatabind.llSearchShow.visibility=View.GONE
                mDatabind.state.visibility=View.VISIBLE
            }

            true
        }

        //根据是否搜索来判断
        mDatabind.txtSearchClick.clickNoRepeat{
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
                        bindingItem.ivHotRanking.visibility=View.VISIBLE
                        if(modelPosition==0){
                            bindingItem.ivHotRanking.setImageDrawable(ContextCompat.getDrawable(this@SearchActivity,R.drawable.search_icon_yi))
                        }else if(modelPosition==1){
                            bindingItem.ivHotRanking.setImageDrawable(ContextCompat.getDrawable(this@SearchActivity,R.drawable.search_icon_er))
                        }else if(modelPosition==2){
                            bindingItem.ivHotRanking.setImageDrawable(ContextCompat.getDrawable(this@SearchActivity,R.drawable.search_icon_san))
                        }else if(modelPosition==3){
                            bindingItem.ivHotRanking.setImageDrawable(ContextCompat.getDrawable(this@SearchActivity,R.drawable.search_icon_si))
                        }else if(modelPosition==4){
                            bindingItem.ivHotRanking.setImageDrawable(ContextCompat.getDrawable(this@SearchActivity,R.drawable.search_icon_wu))
                        }else{
                            bindingItem.ivHotRanking.visibility=View.GONE
                        }
                        //是热门
                        if(bean.hottest){
                            bindingItem.ivHotHeat.visibility=View.VISIBLE
                            bindingItem.ivHotHeat.setImageDrawable(ContextCompat.getDrawable(this@SearchActivity,R.drawable.search_icon_hot))
                        }else if(bean.newest){//最新的
                            bindingItem.ivHotHeat.visibility=View.VISIBLE
                            bindingItem.ivHotHeat.setImageDrawable(ContextCompat.getDrawable(this@SearchActivity,R.drawable.search_icon_new))
                        }else{
                            bindingItem.ivHotHeat.visibility=View.GONE
                        }



                    }


                }

            }
            R.id.llClickHot.onClick {
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
                            bindingItem.txtLiveTeam.text="${bean.homeTeamName} VS ${bean.awayTeamName}"
                        }else{
                            bindingItem.txtLiveTeam.text="${bean.awayTeamName } VS ${bean.homeTeamName}"
                        }

                        bindingItem.txtLiveCompetition.text=bean.competitionName
                        if(bean.hotValue<=9999){
                            bindingItem.txtLiveHeat.text="${bean.hotValue}"
                        }else{
                            bindingItem.txtLiveHeat.text="9999+"
                        }

                        if(layoutPosition%2==0){
                            val layoutParams = bindingItem.llLiveSpacing.layoutParams as ViewGroup.MarginLayoutParams
                            layoutParams.setMargins(0, 0, context.dp2px(4), context.dp2px(8))
                            bindingItem.llLiveSpacing.layoutParams =layoutParams
                        }else{
                            val layoutParams = bindingItem.llLiveSpacing.layoutParams as ViewGroup.MarginLayoutParams
                            layoutParams.setMargins(0, 0, context.dp2px(4), context.dp2px(8))
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


            mDatabind.etSearchInput.postDelayed({
                showSoftKeyboard()
            }, 200)
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

    /**
     * 打开软键盘
     */
    private fun showSoftKeyboard() {
        mDatabind.etSearchInput.requestFocus() // 获取焦点
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput( mDatabind.etSearchInput, InputMethodManager.SHOW_IMPLICIT) // 打开软键盘
    }
    // 隐藏软键盘的函数
    fun hideKeyboard(context: Context, view: AppCompatEditText) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }

}