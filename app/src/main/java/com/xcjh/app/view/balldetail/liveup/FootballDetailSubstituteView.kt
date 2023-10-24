package com.xcjh.app.view.balldetail.liveup

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.divider
import com.drake.brv.utils.grid
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.google.gson.Gson
import com.xcjh.app.R
import com.xcjh.app.bean.FootballLineupBean
import com.xcjh.app.bean.FootballPlayer
import com.xcjh.app.bean.MatchDetailBean
import com.xcjh.app.bean.MatchTeam
import com.xcjh.app.databinding.ItemDetailGameSubstituteBinding
import com.xcjh.app.databinding.ItemDetailGameSubstituteTopBinding

/**
 * 替补阵容
 */
class FootballDetailSubstituteView : LinearLayout {
    private lateinit var rcvPlayer: RecyclerView

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
        initView(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context)
    }

    fun initView(context: Context) {
        val v = LayoutInflater.from(context).inflate(R.layout.view_detail_game_substitute, this)
        rcvPlayer = v.findViewById(R.id.rcvPlayer)
        rcvPlayer.grid(2).divider {
           // setDrawable(R.drawable.divider_horizontal)
            setDivider(1, false)
            setColor("#666777")
            orientation = DividerOrientation.HORIZONTAL
        }.setup {
            addType<MatchTeam> {
                R.layout.item_detail_game_substitute_top
            }
            addType<FootballPlayer> {
                R.layout.item_detail_game_substitute
            }
            onBind {
                when (val item = _data) {
                    is MatchTeam -> {
                        val binding = getBinding<ItemDetailGameSubstituteTopBinding>()
                        Glide.with(context).load(item.logo).into(binding.ivIcon)
                        binding.rltItem.setBackgroundColor(context.getColor(if(this.modelPosition%2==0) R.color.c_21152a else R.color.c_18152A ))
                        binding.tvName.text = item.name
                    }
                    is FootballPlayer -> {
                        val binding = getBinding<ItemDetailGameSubstituteBinding>()
                      /*  if (item.logo.isNotEmpty()){

                        }*/
                        Glide.with(context).load(if(this.modelPosition%2==0) R.drawable.icon_red_cloth else R.drawable.icon_team_blue).into(binding.ivIcon)
                        binding.ctlItem.setBackgroundColor(context.getColor(if(this.modelPosition%2==0) R.color.c_21152a else R.color.c_18152A ))
                        binding.tvPlayerNum.text = item.shirtNumber.toString()
                        binding.tvName.text = item.name
                        binding.tvPosition.text = getPlayerPos(item.position)
                    }
                }
            }
        }

    }

    private fun getPlayerPos(position: String): CharSequence? {

        var pos= context.getString(R.string.wz)
        when(position){
            ////球员位置，F前锋、M中场、D后卫、G守门员、其他为未知
            "F"->{
                pos= context.getString(R.string.qf)
            }
            "M"->{
                pos=context.getString(R.string.zc)
            }
            "D"->{
                pos=context.getString(R.string.hw)
            }
            "G"->{
                pos=context.getString(R.string.smy)
            }
        }
        return pos
    }

    val list = arrayListOf<Any>()
    fun setData(data: FootballLineupBean, match: MatchDetailBean) {
        list.clear()

        list.add(MatchTeam(name = match.homeName, logo = data.homeLogo))
        list.add(MatchTeam(name = match.awayName, logo = data.awayLogo))
        val home = data.home.filter {
            it.first == 0
        }
        val away = data.away.filter {
            it.first == 0
        }
        //Log.e("TAG", "setData:home ==="+ Gson().toJson(home))
        if (home.size == away.size) {
            //替补球员数量相同
            for ((i, item) in home.withIndex()) {
                list.add(item)
                list.add(away[i])
            }
        } else {
            if (home.size > away.size) {
                //替补球员数量主队多
                for ((i, item) in home.withIndex()) {
                    list.add(item)
                    try {
                        list.add(away[i])
                    }catch (_:Exception){
                        list.add(FootballPlayer())
                    }
                }
            } else {
                for ((i, item) in away.withIndex()) {
                    try {
                        list.add(home[i])
                    }catch (_:Exception){
                        list.add(FootballPlayer())
                    }
                    list.add(item)
                }
            }
        }
        rcvPlayer.models = list
    }
}