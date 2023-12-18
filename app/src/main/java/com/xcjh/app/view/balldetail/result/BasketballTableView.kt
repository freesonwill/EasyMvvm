package com.xcjh.app.view.balldetail.result

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.google.gson.Gson
import com.xcjh.app.R
import com.xcjh.app.bean.BasketballScore
import com.xcjh.app.bean.BasketballScoreBean
import com.xcjh.app.databinding.ViewBasketballTableBinding
import com.xcjh.app.databinding.ViewBasketballTableChildBinding
import com.xcjh.base_lib.utils.dip2px
import com.xcjh.base_lib.utils.horizontal
import com.xcjh.base_lib.utils.loge
import com.xcjh.base_lib.utils.notNull

class BasketballTableView : RelativeLayout {

    private lateinit var binding: ViewBasketballTableBinding
    private val scoreList = arrayListOf<BasketballScore>()

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initView()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context, attributeSet, defStyleAttr
    ) {
        initView()
    }

    private fun initView() {
        // LayoutInflater.from(context).inflate(R.layout.view_basketball_table, this)
        binding = ViewBasketballTableBinding.inflate(LayoutInflater.from(context), null, false)
        binding.rcvCommon.isNestedScrollingEnabled = false
        binding.rcvCommon.horizontal().setup {
            addType<BasketballScore> {
                R.layout.view_basketball_table_child // 公告
            }
            onCreate {

            }
            onBind {
                when (val item = _data) {
                    is BasketballScore -> {
                        models?.size.notNull({
                            if (it>0){
                                getBinding<ViewBasketballTableChildBinding>().root.layoutParams.width = binding.rcvCommon.width/ if (it>6) 6 else it
                            }
                        })

                        val binding = getBinding<ViewBasketballTableChildBinding>()
                        binding.tvTitle.text = item.name
                        binding.tvHome.text = item.homeScore
                        binding.tvAway.text = item.awayScore
                    }
                }
            }
        }
        addView(binding.root)
    }

    fun setTeamInfo(homeIcon: String?, homeName: String?, awayIcon: String?, awayName: String?) {
        binding.tvNameHome.text = homeName?:""
        Glide.with(context).load(homeIcon).placeholder(R.drawable.def_basketball)
            .into(binding.ivAvatarHome)
        binding.tvNameAway.text = awayName?:""
        Glide.with(context).load(awayIcon).placeholder(R.drawable.def_basketball)
            .into(binding.ivAvatarAway)
    }

    fun setTeamData(bean: BasketballScoreBean) {
        bean.apply {
            var homeAll = 0
            scoreList.clear()
            homeScoreList.notNull({
                for (i in homeScoreList.indices) {
                    homeAll += homeScoreList[i]
                    if (i < 4) {
                        //排除最后的加时赛比赛
                        scoreList.add(
                            BasketballScore(getTitle(i),
                                getScore(status, homeScoreList[i], i),
                                getScore(status, awayScoreList[i], i)
                            )
                        )
                    }
                }
            })

            //加时赛比分
            homeOverTimeScoresList.notNull({
                if (it.isNotEmpty()){
                    for (i in it.indices) {
                        scoreList.add(
                            BasketballScore(getAddTitle(i),
                                it[i].toString(),
                                awayOverTimeScoresList!![i].toString()))
                    }
                }else{
                    /*scoreList.add(
                        BasketballScore("", "", ""))*/
                }
            }, {
               /* scoreList.add(
                    BasketballScore("", "", ""))*/
            })


            var awayAll = 0
            awayScoreList.notNull({
                awayAll = awayScoreList.reduce { result, item ->
                    result + item
                }
            })

            binding.tvHomeTotal.text = homeAll.toString()
            binding.tvAwayTotal.text = awayAll.toString()
          //  Gson().toJson(scoreList).loge()
            binding.rcvCommon.models = scoreList
        }
    }

    //正常
    private fun getTitle(pos: Int): String {
        var title = ""
        when (pos) {
            0 -> {
                title=context.getString(R.string.one)
            }
            1 -> {
                title=context.getString(R.string.two)
            }
            2 -> {
                title=context.getString(R.string.three)
            }
            3 -> {
                title=context.getString(R.string.four)
            }
        }
        return title
    }

    //加时
    private fun getAddTitle(pos: Int): String {
        var title = context.getString(R.string.over_time)+(pos+1)
        /*when (pos) {
            0 -> {
                title=context.getString(R.string.over_time) + context.getString(R.string.one)
            }
            1 -> {
                title=context.getString(R.string.over_time) + context.getString(R.string.two)
            }
            2 -> {
                title=context.getString(R.string.over_time) + context.getString(R.string.three)
            }
            3 -> {
                title=context.getString(R.string.over_time) + context.getString(R.string.four)
            }
        }*/
        return title
    }

    /**
     * 当前比分
     * 未开始的展示 “ - ”
     */
    private fun getScore(status: Int, s: Int, pos: Int): String {
        var score = s.toString()
        when (status) {
            0 -> score = "-"
            1 -> score = "-"
            2, 3 -> {
                //"第一节"//"第一节完"
                if (pos > 0) {
                    score = "-"
                }
            }
            4, 5 -> {
                //"第二节"//"第二节完"
                if (pos > 1) {
                    score = "-"
                }
            }
            6, 7 -> {
                //"第三节"//"第三节完"
                if (pos > 2) {
                    score = "-"
                }
            }
            8 -> {
                //"第四节"
                if (pos > 3) {
                    score = "-"
                }
            }
            9, 10 -> {
                //"加时""完赛"
            }
            else -> score = "-"
        }
        return score
    }

}